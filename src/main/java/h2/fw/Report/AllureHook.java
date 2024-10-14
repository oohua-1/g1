package h2.fw.Report;

import h2.fw.core.web.aspects.AssertExt;
import h2.fw.runner.web.CucumberParallelWithTestNGRunnerTest;
import h2.fw.runner.web.TestRunnerUtil;
import h2.fw.utils.ConfigReader;
import io.cucumber.messages.types.*;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;
import io.cucumber.plugin.event.*;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.cucumber7jvm.testsourcemodel.TestSourcesModelProxy;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.*;
import io.qameta.allure.util.ResultsUtils;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AllureHook implements ConcurrentEventListener {
    ConfigReader configReader = ConfigReader.getInstance(null);
    private final AllureLifecycle lifecycle;
    private final ConcurrentHashMap<String, String> scenarioUuids;
    private final TestSourcesModelProxy testSources;
    private final ThreadLocal<Feature> currentFeature;
    private final ThreadLocal<URI> currentFeatureFile;
    private final ThreadLocal<TestCase> currentTestCase;
    private final ThreadLocal<String> currentContainer;
    private final ThreadLocal<Boolean> forbidTestCaseStatusChange;
    private final EventHandler<TestSourceRead> featureStartedHandler;
    private final EventHandler<TestCaseStarted> caseStartedHandler;
    private final EventHandler<TestCaseFinished> caseFinishedHandler;
    private final EventHandler<TestStepStarted> stepStartedHandler;
    private final EventHandler<TestStepFinished> stepFinishedHandler;
    private final EventHandler<WriteEvent> writeEventHandler;
    private final EventHandler<EmbedEvent> embedEventHandler;
    private static final String TXT_EXTENSION = ".txt";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String CUCUMBER_WORKING_DIR = Paths.get("").toUri().toString();

    public AllureHook() {
        this(Allure.getLifecycle());
    }

    public AllureHook(AllureLifecycle lifecycle) {
        this.scenarioUuids = new ConcurrentHashMap();
        this.testSources = new TestSourcesModelProxy();
        this.currentFeature = new InheritableThreadLocal();
        this.currentFeatureFile = new InheritableThreadLocal();
        this.currentTestCase = new InheritableThreadLocal();
        this.currentContainer = new InheritableThreadLocal();
        this.forbidTestCaseStatusChange = new InheritableThreadLocal();
        this.featureStartedHandler = this::handleFeatureStartedHandler;
        this.caseStartedHandler = this::handleTestCaseStarted;
        this.caseFinishedHandler = this::handleTestCaseFinished;
        this.stepStartedHandler = this::handleTestStepStarted;
        this.stepFinishedHandler = this::handleTestStepFinished;
        this.writeEventHandler = this::handleWriteEvent;
        this.embedEventHandler = this::handleEmbedEvent;
        this.lifecycle = lifecycle;
    }

    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestSourceRead.class, this.featureStartedHandler);
        publisher.registerHandlerFor(TestCaseStarted.class, this.caseStartedHandler);
        publisher.registerHandlerFor(TestCaseFinished.class, this.caseFinishedHandler);
        publisher.registerHandlerFor(TestStepStarted.class, this.stepStartedHandler);
        publisher.registerHandlerFor(TestStepFinished.class, this.stepFinishedHandler);
        publisher.registerHandlerFor(WriteEvent.class, this.writeEventHandler);
        publisher.registerHandlerFor(EmbedEvent.class, this.embedEventHandler);
    }

    private void handleFeatureStartedHandler(TestSourceRead event) {
        this.testSources.addTestSourceReadEvent(event.getUri(), event);
    }

    private void handleTestCaseStarted(TestCaseStarted event) {
        this.currentFeatureFile.set(event.getTestCase().getUri());
        this.currentFeature.set(this.testSources.getFeature((URI) this.currentFeatureFile.get()));
        this.currentTestCase.set(event.getTestCase());
        this.currentContainer.set(UUID.randomUUID().toString());
        this.forbidTestCaseStatusChange.set(false);
        Deque<String> tags = new LinkedList(((TestCase) this.currentTestCase.get()).getTags());
        Feature feature = (Feature) this.currentFeature.get();
        LabelBuilder labelBuilder = new LabelBuilder(feature, (TestCase) this.currentTestCase.get(), tags);
        // Retrieve the browser name using the method you provided
        String browserName = TestRunnerUtil.getThreadBrowser();
        String originalName = ((TestCase) this.currentTestCase.get()).getName();
        String name = originalName + " [" + browserName + "]";

        String customHistoryId = generateConsistentHistoryId(name, browserName);


//        String name = ((TestCase) this.currentTestCase.get()).getName();
        String featureName = feature.getName();
        TestResult result = new TestResult()
                .setUuid(this.getTestCaseUuid((TestCase) this.currentTestCase.get()))
                .setHistoryId(customHistoryId)
                .setFullName(featureName + ": " + name)
                .setName(name)
                .setLabels(labelBuilder.getScenarioLabels())
                .setLinks(labelBuilder.getScenarioLinks());

        Scenario scenarioDefinition = this.testSources.getScenarioDefinition((URI) this.currentFeatureFile.get(), ((TestCase) this.currentTestCase.get()).getLocation().getLine());
        if (scenarioDefinition.getExamples() != null) {
            result.setParameters(this.getExamplesAsParameters(scenarioDefinition, (TestCase) this.currentTestCase.get()));
        }

        String description = (String) Stream.of(feature.getDescription(), scenarioDefinition.getDescription()).filter(Objects::nonNull).filter((s) -> {
            return !s.isEmpty();
        }).collect(Collectors.joining("\n"));
        if (!description.isEmpty()) {
            result.setDescription(description);
        }

        TestResultContainer resultContainer = (new TestResultContainer()).setName(String.format("%s: %s", scenarioDefinition.getKeyword(), scenarioDefinition.getName())).setUuid(this.getTestContainerUuid()).setChildren(Collections.singletonList(this.getTestCaseUuid((TestCase) this.currentTestCase.get())));
        this.lifecycle.scheduleTestCase(result);
        this.lifecycle.startTestContainer(this.getTestContainerUuid(), resultContainer);
        this.lifecycle.startTestCase(this.getTestCaseUuid((TestCase) this.currentTestCase.get()));
    }

    private String generateConsistentHistoryId(String newName, String browserName) {
        String baseString = newName + browserName;
        return UUID.nameUUIDFromBytes(baseString.getBytes()).toString();
    }

    private void handleTestCaseFinished(TestCaseFinished event) {
        String uuid = this.getTestCaseUuid(event.getTestCase());
        System.out.println("from UUID: " + uuid);

        if (AssertExt.getCountInterceptedErrors() > 0) {
            this.updateTestCaseStatus(Status.FAILED);

            StringBuilder aggregatedErrorMessage = new StringBuilder()
                    .append("<strong><span style='font-size: 13px;'>Soft assertion errors found:</span></strong><br>");
            for (List<Throwable> errorList : AssertExt.getInterceptedErrors()) {
                for (Throwable error : errorList) {
                    aggregatedErrorMessage.append("<span style='font-size: 14px; color: red;'><strong>")
                            .append(error.getMessage())
                            .append("</strong></span><br>");
                }
            }

            // Update the test case with the HTML description
            this.lifecycle.updateTestCase(uuid, testResult -> {
                testResult.setDescriptionHtml(aggregatedErrorMessage.toString());
                testResult.setStatus(Status.FAILED);
            });


//            StringBuilder aggregatedErrorMessage = new StringBuilder("Soft assertion errors found:\n");
//            for (List<Throwable> errorList : AssertExt.getInterceptedErrors()) {
//                for (Throwable error : errorList) {
//                    aggregatedErrorMessage.append(error.getMessage()).append("\n");
//                }
//            }
//             Set the aggregated message as the status details
//            StatusDetails statusDetails = new StatusDetails()
//                    .setMessage(aggregatedErrorMessage.toString()).setTrace(null);

//            this.lifecycle.updateTestCase(uuid, testResult -> {
//                testResult.setStatusDetails(statusDetails);
//            });
//            this.lifecycle.updateTestCase(uuid, testResult -> {
//                testResult.setStatus(Status.FAILED);
//                testResult.setDescription(aggregatedErrorMessage.toString());  // Use description instead of message
//            });


//            Optional<StatusDetails> details = ResultsUtils.getStatusDetails(event.getResult().getError());
//            details.ifPresent(statusDetails -> {
//                this.lifecycle.updateTestCase(uuid, testResult -> {
//                    testResult.setStatusDetails(statusDetails);
//                });
//            });

            AssertExt.clearInterceptedErrors();
        } else {
            Optional<StatusDetails> details = ResultsUtils.getStatusDetails(event.getResult().getError());
            details.ifPresent((statusDetails) -> {
                this.lifecycle.updateTestCase(uuid, (testResult) -> {
                    testResult.setStatusDetails(statusDetails);
                });
            });
        }
        this.lifecycle.stopTestCase(uuid);
        this.lifecycle.stopTestContainer(this.getTestContainerUuid());
        this.lifecycle.writeTestCase(uuid);
        this.lifecycle.writeTestContainer(this.getTestContainerUuid());


        // Clear the forbidTestCaseStatusChange flag if you intend to override the status
//        this.forbidTestCaseStatusChange.set(false);

        // Force set the status to PASS
//        this.updateTestCaseStatus(Status.PASSED);

//        Optional<StatusDetails> details = ResultsUtils.getStatusDetails(event.getResult().getError());
//        details.ifPresent(statusDetails -> {
//            this.lifecycle.updateTestCase(uuid, testResult -> {
//                testResult.setStatusDetails(statusDetails);
//                // Ensure the status remains as PASSED
//                testResult.setStatus(Status.PASSED);
//            });
//        });

//        Optional<StatusDetails> details = ResultsUtils.getStatusDetails(event.getResult().getError());
//        details.ifPresent((statusDetails) -> {
//            this.lifecycle.updateTestCase(uuid, (testResult) -> {
//                testResult.setStatusDetails(statusDetails);
//            });
//        });
    }

    private void handleTestStepStarted(TestStepStarted event) {
        if (event.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep pickleStep = (PickleStepTestStep) event.getTestStep();
            String stepKeyword = (String) Optional.ofNullable(this.testSources.getKeywordFromSource((URI) this.currentFeatureFile.get(), pickleStep.getStep().getLine())).orElse("UNDEFINED");
            StepResult stepResult = (new StepResult()).setName(String.format("%s %s", stepKeyword, pickleStep.getStep().getText())).setStart(System.currentTimeMillis());
            this.lifecycle.startStep(this.getTestCaseUuid((TestCase) this.currentTestCase.get()), this.getStepUuid(pickleStep), stepResult);
            StepArgument stepArgument = pickleStep.getStep().getArgument();
            if (stepArgument instanceof DataTableArgument) {
                DataTableArgument dataTableArgument = (DataTableArgument) stepArgument;
//                this.createDataTableAttachment(dataTableArgument);
            }
        } else if (event.getTestStep() instanceof HookTestStep) {
            this.initHook((HookTestStep) event.getTestStep());
        }

    }

    private void initHook(HookTestStep hook) {
        FixtureResult hookResult = (new FixtureResult()).setName(hook.getCodeLocation()).setStart(System.currentTimeMillis());
        if (hook.getHookType() == HookType.BEFORE) {
            this.lifecycle.startPrepareFixture(this.getTestContainerUuid(), this.getHookStepUuid(hook), hookResult);
        } else {
            this.lifecycle.startTearDownFixture(this.getTestContainerUuid(), this.getHookStepUuid(hook), hookResult);
        }

    }

    private void handleTestStepFinished(TestStepFinished event) {
        if (event.getTestStep() instanceof HookTestStep) {
            this.handleHookStep(event);
        } else {
            this.handlePickleStep(event);
        }

    }

    private void handleWriteEvent(WriteEvent event) {
        this.lifecycle.addAttachment("Text output", "text/plain", ".txt", Objects.toString(event.getText()).getBytes(StandardCharsets.UTF_8));
    }

    private void handleEmbedEvent(EmbedEvent event) {
        this.lifecycle.addAttachment(event.name, event.getMediaType(), (String) null, new ByteArrayInputStream(event.getData()));
    }

    private String getTestContainerUuid() {
        return (String) this.currentContainer.get();
    }

    private String getTestCaseUuid(TestCase testCase) {
        return (String) this.scenarioUuids.computeIfAbsent(this.getHistoryId(testCase), (it) -> {
            return UUID.randomUUID().toString();
        });
    }

    private String getStepUuid(PickleStepTestStep step) {
        return ((Feature) this.currentFeature.get()).getName() + this.getTestCaseUuid((TestCase) this.currentTestCase.get()) + step.getStep().getText() + step.getStep().getLine();
    }

    private String getHookStepUuid(HookTestStep step) {
        return ((Feature) this.currentFeature.get()).getName() + this.getTestCaseUuid((TestCase) this.currentTestCase.get()) + step.getHookType().toString() + step.getCodeLocation();
    }

    private String getHistoryId(TestCase testCase) {
        String testCaseLocation = this.getTestCaseUri(testCase) + ":" + testCase.getLocation().getLine();
        return ResultsUtils.md5(testCaseLocation);
    }

    private String getTestCaseUri(TestCase testCase) {
        String testCaseUri = testCase.getUri().toString();
        return testCaseUri.startsWith(CUCUMBER_WORKING_DIR) ? testCaseUri.substring(CUCUMBER_WORKING_DIR.length()) : testCaseUri;
    }

    private Status translateTestCaseStatus(Result testCaseResult) {
        switch (testCaseResult.getStatus()) {
            case FAILED:
                return (Status) ResultsUtils.getStatus(testCaseResult.getError()).orElse(Status.FAILED);
            case PASSED:
                return Status.PASSED;
            case SKIPPED:
            case PENDING:
                return Status.SKIPPED;
            case AMBIGUOUS:
            case UNDEFINED:
            default:
                return null;
        }
    }

    private List<Parameter> getExamplesAsParameters(Scenario scenario, TestCase localCurrentTestCase) {
        Optional<Examples> maybeExample = scenario.getExamples().stream().filter((example) -> {
            return example.getTableBody().stream().anyMatch((row) -> {
                return row.getLocation().getLine() == (long) localCurrentTestCase.getLocation().getLine();
            });
        }).findFirst();
        if (!maybeExample.isPresent()) {
            return Collections.emptyList();
        } else {
            Examples examples = (Examples) maybeExample.get();
            Optional<TableRow> maybeRow = examples.getTableBody().stream().filter((example) -> {
                return example.getLocation().getLine() == (long) localCurrentTestCase.getLocation().getLine();
            }).findFirst();
            if (!maybeRow.isPresent()) {
                return Collections.emptyList();
            } else {
                TableRow row = (TableRow) maybeRow.get();
                return (List) IntStream.range(0, ((TableRow) examples.getTableHeader().get()).getCells().size()).mapToObj((index) -> {
                    String name = ((TableCell) ((TableRow) examples.getTableHeader().get()).getCells().get(index)).getValue();
                    String value = ((TableCell) row.getCells().get(index)).getValue();
                    return ResultsUtils.createParameter(name, value);
                }).collect(Collectors.toList());
            }
        }
    }

    private void createDataTableAttachment(DataTableArgument dataTableArgument) {
        List<List<String>> rowsInTable = dataTableArgument.cells();
        StringBuilder dataTableCsv = new StringBuilder();
        Iterator var4 = rowsInTable.iterator();

        while (true) {
            List columns;
            do {
                if (!var4.hasNext()) {
                    String attachmentSource = this.lifecycle.prepareAttachment("Data table", "text/tab-separated-values", "csv");
                    this.lifecycle.writeAttachment(attachmentSource, new ByteArrayInputStream(dataTableCsv.toString().getBytes(StandardCharsets.UTF_8)));
                    return;
                }

                columns = (List) var4.next();
            } while (columns.isEmpty());

            for (int i = 0; i < columns.size(); ++i) {
                if (i == columns.size() - 1) {
                    dataTableCsv.append((String) columns.get(i));
                } else {
                    dataTableCsv.append((String) columns.get(i));
                    dataTableCsv.append('\t');
                }
            }

            dataTableCsv.append('\n');
        }
    }

    private void handleHookStep(TestStepFinished event) {
        HookTestStep hookStep = (HookTestStep) event.getTestStep();
        String uuid = this.getHookStepUuid(hookStep);
        FixtureResult fixtureResult = (new FixtureResult()).setStatus(this.translateTestCaseStatus(event.getResult()));
        if (!Status.PASSED.equals(fixtureResult.getStatus())) {
            TestResult testResult = (new TestResult()).setStatus(this.translateTestCaseStatus(event.getResult()));
            StatusDetails statusDetails = (StatusDetails) ResultsUtils.getStatusDetails(event.getResult().getError()).orElseGet(StatusDetails::new);
            String errorMessage = event.getResult().getError() == null ? hookStep.getHookType().name() + " is failed." : hookStep.getHookType().name() + " is failed: " + event.getResult().getError().getLocalizedMessage();
            statusDetails.setMessage(errorMessage);
            if (hookStep.getHookType() == HookType.BEFORE) {
                TagParser tagParser = new TagParser((Feature) this.currentFeature.get(), (TestCase) this.currentTestCase.get());
                statusDetails.setFlaky(tagParser.isFlaky()).setMuted(tagParser.isMuted()).setKnown(tagParser.isKnown());
                testResult.setStatus(Status.SKIPPED);
                this.updateTestCaseStatus(testResult.getStatus());
                this.forbidTestCaseStatusChange.set(true);
            } else {
                testResult.setStatus(Status.BROKEN);
                this.updateTestCaseStatus(testResult.getStatus());
            }

            fixtureResult.setStatusDetails(statusDetails);
        }

        this.lifecycle.updateFixture(uuid, (result) -> {
            result.setStatus(fixtureResult.getStatus()).setStatusDetails(fixtureResult.getStatusDetails());
        });
        this.lifecycle.stopFixture(uuid);
    }
//    private void handlePickleStep(TestStepFinished event) {
//        Status stepStatus = this.translateTestCaseStatus(event.getResult());
//        StatusDetails statusDetails;
//        if (event.getResult().getStatus() == io.cucumber.plugin.event.Status.UNDEFINED) {
//            this.updateTestCaseStatus(Status.PASSED);
//            statusDetails = (StatusDetails)ResultsUtils.getStatusDetails(new IllegalStateException("Undefined Step. Please add step definition")).orElse(new StatusDetails());
//            this.lifecycle.updateTestCase(this.getTestCaseUuid((TestCase)this.currentTestCase.get()), (scenarioResult) -> {
//                scenarioResult.setStatusDetails(statusDetails);
//            });
//        } else {
//            statusDetails = (StatusDetails)ResultsUtils.getStatusDetails(event.getResult().getError()).orElse(new StatusDetails());
//            this.updateTestCaseStatus(stepStatus);
//        }
//
//        if (!Status.PASSED.equals(stepStatus) && stepStatus != null) {
//            this.forbidTestCaseStatusChange.set(true);
//        }
//
//        TagParser tagParser = new TagParser((Feature)this.currentFeature.get(), (TestCase)this.currentTestCase.get());
//        statusDetails.setFlaky(tagParser.isFlaky()).setMuted(tagParser.isMuted()).setKnown(tagParser.isKnown());
//        this.lifecycle.updateStep(this.getStepUuid((PickleStepTestStep)event.getTestStep()), (stepResult) -> {
//            stepResult.setStatus(stepStatus).setStatusDetails(statusDetails);
//        });
//        this.lifecycle.stopStep(this.getStepUuid((PickleStepTestStep)event.getTestStep()));
//    }
    private void handlePickleStep(TestStepFinished event) {
        PickleStepTestStep pickleStep = (PickleStepTestStep) event.getTestStep();

        boolean isSubstep = AssertExt.isCurrentStepSubstep();
        Status stepStatus = this.translateTestCaseStatus(event.getResult());
        StatusDetails statusDetails = ResultsUtils.getStatusDetails(event.getResult().getError()).orElse(new StatusDetails());

        if (event.getResult().getStatus() == io.cucumber.plugin.event.Status.UNDEFINED) {
            this.updateTestCaseStatus(Status.PASSED);
            statusDetails = ResultsUtils.getStatusDetails(new IllegalStateException("Undefined Step. Please add step definition")).orElse(new StatusDetails());
            StatusDetails finalStatusDetails1 = statusDetails;
            this.lifecycle.updateTestCase(this.getTestCaseUuid((TestCase) this.currentTestCase.get()), scenarioResult -> {
                scenarioResult.setStatusDetails(finalStatusDetails1);
            });
        } else if (event.getResult().getStatus() == io.cucumber.plugin.event.Status.FAILED && pickleStep.getStep().getText().contains("SYSTEM: SOFT ASSERT MODE IS DEACTIVATED")) {
            stepStatus = Status.PASSED;
            this.updateTestCaseStatus(stepStatus );
            StatusDetails finalStatusDetails2 = statusDetails;
            this.lifecycle.updateStep(this.getStepUuid(pickleStep), stepResult -> {
                stepResult.setStatus(Status.PASSED);
            });
            this.lifecycle.stopStep(this.getStepUuid((PickleStepTestStep) event.getTestStep()));
            // Exit early to avoid further processing
            return;
        } else if (event.getResult().getStatus() == io.cucumber.plugin.event.Status.PASSED) {
            if (AssertExt.getRealTimeErrors().isEmpty()) {
                this.updateTestCaseStatus(Status.PASSED);
                if (AssertExt.isAttachmentNeededForParent() && configReader.getProperty("SCREENSHOT_MODE").contains("AFTER_EACH_STEP")) {
                    ReportAllureManager.attachScreenshot("AFTER STEP");
                }
            } else {
                stepStatus = Status.FAILED;
                if (!isSubstep) {
                    StringBuilder errorMessage = new StringBuilder();
                    StringBuilder errorStackTrace = new StringBuilder();
                    for (Throwable error : AssertExt.getRealTimeErrors().getLast()) {
                        errorMessage.append(error.getMessage()).append("\n");

                        // Capture stack trace and truncate to 100 characters
                        String fullStackTrace = Arrays.toString(error.getStackTrace());
                        String truncatedStackTrace = fullStackTrace.length() > 100
                                ? fullStackTrace.substring(0, 100) + "..."
                                : fullStackTrace;
                        errorStackTrace.append(truncatedStackTrace).append("\n");
                    }

                    statusDetails.setMessage(errorMessage.toString()).setTrace(errorStackTrace.toString());
                    if (configReader.getProperty("SCREENSHOT_MODE").contains("ON_FAILURE")) {
                        ReportAllureManager.attachScreenshot("FAILED STEP");}

                }
                this.updateTestCaseStatus(stepStatus);
            }
        } else {
            // Ensure the parent step is not marked as BROKEN due to soft assert mode deactivation
            if (!(event.getResult().getStatus() == io.cucumber.plugin.event.Status.FAILED
                    && pickleStep.getStep().getText().contains("SYSTEM: SOFT ASSERT MODE IS DEACTIVATED"))) {
                this.updateTestCaseStatus(stepStatus);
            }
        }
        if (!Status.PASSED.equals(stepStatus) && stepStatus != null) {
            this.forbidTestCaseStatusChange.set(true);
        }
        // Clear intercepted errors after handling this step
        AssertExt.clearRealTimeErrors();
        AssertExt.setSubstep(false);

        TagParser tagParser = new TagParser((Feature) this.currentFeature.get(), (TestCase) this.currentTestCase.get());
        statusDetails.setFlaky(tagParser.isFlaky()).setMuted(tagParser.isMuted()).setKnown(tagParser.isKnown());
        Status finalStepStatus = stepStatus;
        StatusDetails finalStatusDetails = statusDetails;
        this.lifecycle.updateStep(this.getStepUuid((PickleStepTestStep) event.getTestStep()), stepResult -> {
            stepResult.setStatus(finalStepStatus).setStatusDetails(finalStatusDetails);
        });
        this.lifecycle.stopStep(this.getStepUuid((PickleStepTestStep) event.getTestStep()));
        AssertExt.clearAttachmentNeededForParent();
        AssertExt.clearScreenshotTaken();

    }

    private void addHtmlTableAttachment() {
        // Example HTML table content
        String htmlTable = "<table border='1'>" +
                "<tr><th>Header 1</th><th>Header 2</th></tr>" +
                "<tr><td>Data 1</td><td>Data 2</td></tr>" +
                "<tr><td>Data 3</td><td>Data 4</td></tr>" +
                "</table>";

        // Convert the HTML string to a byte array
        byte[] tableBytes = htmlTable.getBytes(StandardCharsets.UTF_8);

        // Attach the HTML table to the report
        this.lifecycle.addAttachment("HTML Table", "text/html", ".html", tableBytes);
    }


//    private void handlePickleStep(TestStepFinished event) {
//        Status stepStatus = this.translateTestCaseStatus(event.getResult());
//        StatusDetails statusDetails;
//        if (event.getResult().getStatus() == io.cucumber.plugin.event.Status.UNDEFINED) {
//            this.updateTestCaseStatus(Status.PASSED);
//            statusDetails = (StatusDetails)ResultsUtils.getStatusDetails(new IllegalStateException("Undefined Step. Please add step definition")).orElse(new StatusDetails());
//            this.lifecycle.updateTestCase(this.getTestCaseUuid((TestCase)this.currentTestCase.get()), (scenarioResult) -> {
//                scenarioResult.setStatusDetails(statusDetails);
//            });
//        } else {
//            statusDetails = (StatusDetails)ResultsUtils.getStatusDetails(event.getResult().getError()).orElse(new StatusDetails());
//            this.updateTestCaseStatus(stepStatus);
//        }
//
//        if (!Status.PASSED.equals(stepStatus) && stepStatus != null) {
//            this.forbidTestCaseStatusChange.set(true);
//        }
//
//        TagParser tagParser = new TagParser((Feature)this.currentFeature.get(), (TestCase)this.currentTestCase.get());
//        statusDetails.setFlaky(tagParser.isFlaky()).setMuted(tagParser.isMuted()).setKnown(tagParser.isKnown());
//        this.lifecycle.updateStep(this.getStepUuid((PickleStepTestStep)event.getTestStep()), (stepResult) -> {
//            stepResult.setStatus(stepStatus).setStatusDetails(statusDetails);
//        });
//        this.lifecycle.stopStep(this.getStepUuid((PickleStepTestStep)event.getTestStep()));
//    }

    private void updateTestCaseStatus(Status status) {
        if (!(Boolean)this.forbidTestCaseStatusChange.get()) {
            this.lifecycle.updateTestCase(this.getTestCaseUuid((TestCase)this.currentTestCase.get()), (result) -> {
                result.setStatus(status);
            });
        }

    }
}
