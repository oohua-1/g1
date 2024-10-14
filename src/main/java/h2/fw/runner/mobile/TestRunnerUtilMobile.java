package h2.fw.runner.mobile;

import h2.fw.runner.web.CucumberNoParallelWithTestNGRunnerTest;
import h2.fw.runner.web.CucumberParallelWithTestNGRunnerTest;
import h2.fw.utils.SystemConfigManager;

public class TestRunnerUtilMobile {
    static SystemConfigManager systemConfigManager = SystemConfigManager.getInstance();
    public static String getMobileOs() {
        int parallel = systemConfigManager.getParallel();

        if (parallel == 1) {
            return CucumberMobileNoParallelWithTestNGRunnerTest.getThreadMobileOs();
        } else if (parallel > 1) {
            return CucumberMobileParallelWithTestNGRunnerTest.getThreadMobileOs();
        } else {
            throw new IllegalArgumentException("Invalid configuration for checkScenario and parallel");
        }
    }
}
