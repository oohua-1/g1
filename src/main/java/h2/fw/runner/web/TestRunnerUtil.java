package h2.fw.runner.web;

import h2.fw.runner.GlobalContext;
import h2.fw.utils.SystemConfigManager;

public class TestRunnerUtil {
    static SystemConfigManager systemConfigManager = SystemConfigManager.getInstance();

    public static String getThreadBrowser() {
        String runnerType = GlobalContext.getInstance().getRunnerType();
        String browserName = null;
        int parallel = systemConfigManager.getParallel();

        if ("JUnit".equals(runnerType)) {
            browserName = JunitRunner.getThreadBrowser();
        } else {
            if (parallel == 1) {
                return CucumberNoParallelWithTestNGRunnerTest.getThreadBrowser();
            } else if (parallel > 1) {
                return CucumberParallelWithTestNGRunnerTest.getThreadBrowser();
            } else {
                throw new IllegalArgumentException("Invalid configuration for checkScenario and parallel");
            }
        }
        return browserName;
    }
}
