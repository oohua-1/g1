package h2.fw.stepdefinitions.PageSteps;

import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.runner.mobile.CucumberMobileParallelWithTestNGRunnerTest;
import h2.fw.runner.mobile.TestRunnerUtilMobile;
import h2.fw.stepdefinitions.MobileBaseStep;
import h2.fw.utils.SystemConfigManager;
import io.cucumber.java.en.Given;

public class MobilePageSteps {
    SystemConfigManager systemConfigManager = SystemConfigManager.getInstance();
    private final MobileBaseStep mobileBaseStep;
    public MobilePageSteps(MobileBaseStep baseStep) {
        this.mobileBaseStep = baseStep;
    }

    public void pageIsLoaded(String pageName) {
        mobileBaseStep.setActivePageInstance(pageName, TestRunnerUtilMobile.getMobileOs());
        mobileBaseStep.getActivePageInstance().isLoaded();
    }

    @NeverTakeAllureScreenshotForThisStep
    @Given("I launch the app")
    public void launchTheApp() {
        mobileBaseStep.ensureDriverIsStarted();
    }


}
