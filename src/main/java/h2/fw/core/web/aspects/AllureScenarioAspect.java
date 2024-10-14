package h2.fw.core.aspects;//package core.aspects;
//
//import io.qameta.allure.AllureLifecycle;
//import io.qameta.allure.model.Status;
//import io.qameta.allure.model.StatusDetails;
//import io.cucumber.java.Scenario;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//
//@Aspect
//public class AllureScenarioAspect {
//
//    private final AllureLifecycle lifecycle = AllureLifecycleSingleton.getInstance();
//
//    @Pointcut("execution(* io.cucumber.java.Scenario(..))")
//    public void scenarioExecution() {}
//
//    @After("scenarioExecution() && args(scenario)")
//    public void afterScenario(Scenario scenario) {
//        String uuid = scenario.getId();
//
//        // Check if the scenario has failed
//        if (scenario.isFailed()) {
//            // Combine any errors from soft assertions
//            StringBuilder sb = new StringBuilder("Soft Assertion Mode: The following errors were found:\n");
//            if (!AssertExt.getInterceptedErrors().isEmpty() && !AssertExt.getInterceptedErrors().getLast().isEmpty()) {
//                for (Throwable error : AssertExt.getInterceptedErrors().getLast()) {
//                    sb.append(error.getMessage()).append("\n");
//                }
//
//                // Clear intercepted errors after reporting them
//                AssertExt.clearInterceptedErrors();
//
//                // Update the test case in Allure with the failure status and details
//                StatusDetails statusDetails = new StatusDetails().setMessage(sb.toString());
//                lifecycle.updateTestCase(uuid, testResult -> testResult
//                        .setStatus(Status.FAILED)
//                        .setStatusDetails(statusDetails)
//                );
//            }
//        }
//
//        // Finalize the test case in Allure
//        lifecycle.stopTestCase(uuid);
//        lifecycle.writeTestCase(uuid);
//    }
//}
