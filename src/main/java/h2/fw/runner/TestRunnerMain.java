package h2.fw.runner;

import h2.fw.runner.mobile.ParallelTestNGRunnerMobile;
import h2.fw.runner.mobile.SingleTestNGRunnerMobile;
import h2.fw.runner.web.*;
import h2.fw.utils.SystemConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TestRunnerMain {
    private static final Logger LOGGER = LogManager.getLogger(TestRunnerMain.class.getName());

    public static void main(String[] args) {
        TestRunnerMain runner = new TestRunnerMain();
        runner.run();
    }

    private void run() {
        EnvironmentSetup.loadConfiguration();
        SystemConfigManager config = SystemConfigManager.getInstance();

        List<String> browsers = List.of(config.getBrowser().split("/"));
        if (config.getPlatform().contains("web")) {
            if (config.getFolder() != null && !config.getFolder().isEmpty()) {
                if (browsers.size() > 1) {
                    GlobalContext.getInstance().setRunnerType("JUnit");
                    ParallelJunit parallelJunit = new ParallelJunit(config.getFolder(), browsers, config.getParallel(), config.getTag());
                    parallelJunit.runInParallel();
                } else {
                    GlobalContext.getInstance().setRunnerType("JUnit");
                    JunitRunner runner = new JunitRunner(config.getFolder(), browsers.get(0), config.getParallel(), config.getTag());
                    runner.run();
                }
            } else if (config.getFeature() != null && !config.getFeature().isEmpty()) {
                if (config.getCI()) {
                    GlobalContext.getInstance().setRunnerType("TestNG");
                    SingleTestNGRunner singleRunner = new SingleTestNGRunner();
                    singleRunner.run(browsers.get(0), config.getFeature(), config.getParallel(), config.getTag(), config.isParallelScenario());
                    return;
                }
                if (browsers.size() > 1) {
                    GlobalContext.getInstance().setRunnerType("TestNG");
                    ParallelTestNGRunner parallelTestNGRunner = new ParallelTestNGRunner();
                    parallelTestNGRunner.run(browsers, config.getFeature(), config.getParallel(), config.getTag(), config.isParallelScenario());
                } else {
                    GlobalContext.getInstance().setRunnerType("TestNG");
                    SingleTestNGRunner singleRunner = new SingleTestNGRunner();
                    singleRunner.run(browsers.get(0), config.getFeature(), config.getParallel(), config.getTag(), config.isParallelScenario());
                }
            }


        }
        if (config.getPlatform().contains("mobile")) {
            List<String> mobileOs = List.of(config.getMobileOs().split("/"));

            if (config.getFolder() != null && !config.getFolder().isEmpty()) {

            } else if (config.getFeature() != null && !config.getFeature().isEmpty()) {
                if (mobileOs.size() > 1) {
                    GlobalContext.getInstance().setRunnerType("TestNG");
                    if (mobileOs.get(0).contains("android")) {
                        LocalDevicesSetup.setupLocalExecution();
                    }
                    if (mobileOs.get(0).contains("ios")) {
                        LocalDevicesSetup.setupLocalIOSExecution();
                    }
                    ParallelTestNGRunnerMobile parallelTestNGRunnerMobile = new ParallelTestNGRunnerMobile();
                    parallelTestNGRunnerMobile.run(mobileOs, config.getFeature(), config.getParallel(), config.getTag(), config.isParallelScenario());
                }else{
                    if (config.getParallel() > 1){
                        GlobalContext.getInstance().setRunnerType("TestNG");
                        if (mobileOs.get(0).contains("android")) {
                            LocalDevicesSetup.setupLocalExecution();
                        }
                        if (mobileOs.get(0).contains("ios")) {
                            LocalDevicesSetup.setupLocalIOSExecution();
                        }
                        ParallelTestNGRunnerMobile parallelTestNGRunnerMobile = new ParallelTestNGRunnerMobile();
                        parallelTestNGRunnerMobile.run(mobileOs, config.getFeature(), config.getParallel(), config.getTag(), config.isParallelScenario());
                    }else{
                        if (mobileOs.get(0).contains("android")) {
                            LocalDevicesSetup.setupLocalExecution();
                        }
                        if (mobileOs.get(0).contains("ios")) {
                            LocalDevicesSetup.setupLocalIOSExecution();
                        }
                        SingleTestNGRunnerMobile singleRunnerMobile = new SingleTestNGRunnerMobile();
                        singleRunnerMobile.run(config.getFeature(), config.getParallel(), config.getTag(), config.isParallelScenario(), mobileOs.get(0));
                    }

                }
            }
        }



//                if (mobileOs.size() > 1) {
//                GlobalContext.getInstance().setRunnerType("TestNG");
//                ParallelTestNGRunnerMobile parallelTestNGRunnerMobile = new ParallelTestNGRunnerMobile();
//                parallelTestNGRunnerMobile.run(mobileOs, config.getFeature(), config.getParallel(), config.getTag(), config.isParallelScenario());
//
//            } else {
//                if (mobileOs.get(0).contains("android")) {
//                    LocalDevicesSetup.setupLocalExecution();
//                }
//                if (mobileOs.get(0).contains("ios")) {
//                    LocalDevicesSetup.setupLocalIOSExecution();
//                }
//                GlobalContext.getInstance().setRunnerType("TestNG");
//                SingleTestNGRunnerMobile singleRunnerMobile = new SingleTestNGRunnerMobile();
//                singleRunnerMobile.run(config.getFeature(), config.getParallel(), config.getTag(), config.isParallelScenario(), mobileOs.get(0));

            }
//
//        }


}
