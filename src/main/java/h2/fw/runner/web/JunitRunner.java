package h2.fw.runner.web;

import h2.fw.utils.ConfigReader;
import io.cucumber.core.options.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectDirectory;
public class JunitRunner implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(JunitRunner.class.getName());

    ConfigReader configReader = ConfigReader.getInstance(null);
    private final String path; // Could be either folder or feature
    private final String browser;
    private final int parallel; // Add parallel as a class field
    private final String tag;

    private static InheritableThreadLocal<String> threadBrowser = new InheritableThreadLocal<>();


    public JunitRunner(String path, String browser, int parallel, String tag) {
        this.path = path;
        this.browser = browser;
        this.parallel = parallel;
        this.tag = tag;

    }

    public static String getThreadBrowser() {
        return threadBrowser.get();
    }

    @Override
    public void run() {
        threadBrowser.set(browser); // Set the browser for the current thread
        // Log the browser to ensure it's set correctly
        LOGGER.info("Thread ID: " + Thread.currentThread().getId() + " - Starting browser: " + browser);

//        String folderPath = configReader.getProperty("FEATURES_FOLDER") + path;
        // Check if path contains multiple folders
        List<String> paths;
        if (path.contains(",")) {
            // Split the comma-separated paths into individual folders
            paths = Arrays.asList(path.split(","));
        } else {
            // Single folder, create a single-element list
            paths = Collections.singletonList(path);
        }

        String gluePaths = "h2.fw.stepdefinitions,h2.fw.hooks";
        // Create a request builder
        LauncherDiscoveryRequestBuilder requestBuilder = LauncherDiscoveryRequestBuilder.request()
//                .selectors(
//                        selectDirectory(folderPath)
//                )
                .filters(
                        EngineFilter.includeEngines("cucumber")
                )
                .configurationParameter(Constants.GLUE_PROPERTY_NAME, gluePaths)
                .configurationParameter(Constants.PLUGIN_PROPERTY_NAME, "pretty, h2.fw.Report.CucumberPlatformScenarioReporterListener");


        // Add selectors for all folders
        for (String folder : paths) {
            String folderPath = configReader.getProperty("FEATURES_FOLDER") + folder.trim();
            requestBuilder.selectors(selectDirectory(folderPath));
        }


        // Decide on parallel execution strategy
        if (parallel > 1) {
            requestBuilder.configurationParameter("cucumber.execution.parallel.enabled", "true")
                    .configurationParameter("cucumber.execution.parallel.config.strategy", "fixed")
                    .configurationParameter("cucumber.execution.parallel.config.fixed.parallelism", String.valueOf(parallel));
        } else {
            requestBuilder.configurationParameter("cucumber.execution.parallel.enabled", "false");
//                    .configurationParameter("cucumber.execution.parallel.config.strategy", "dynamic")
//                    .configurationParameter("cucumber.execution.parallel.config.dynamic.factor", "1.0");
        }
        if (tag != null && !tag.isEmpty()) {
            requestBuilder.configurationParameter(Constants.FILTER_TAGS_PROPERTY_NAME, tag);
        }

        // Build and execute the request
        LauncherDiscoveryRequest request = requestBuilder.build();
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        // Handle summary and failures
        TestExecutionSummary summary = listener.getSummary();
        List<TestExecutionSummary.Failure> failures = summary.getFailures();
        if (!failures.isEmpty()) {
            System.out.println("Some tests failed:");
            failures.forEach(failure -> System.out.println(failure.getTestIdentifier().getDisplayName() + ": " + failure.getException().getMessage()));
        }
    }
}
