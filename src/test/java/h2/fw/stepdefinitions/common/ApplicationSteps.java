package h2.fw.stepdefinitions.common;

import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.stepdefinitions.BaseStep;
import io.cucumber.java.en.When;

public class ApplicationSteps {
    private final BaseStep baseStep;
    private final ApplicationSubSteps subSteps;

    public ApplicationSteps(BaseStep baseStep) {
        this.baseStep = baseStep;
        this.subSteps = new ApplicationSubSteps(baseStep); // Pass the current instance to ApplicationSubSteps
    }
    @NeverTakeAllureScreenshotForThisStep
    @When("Switch to {string} page")
    public void switchTo(String pageName) {
        subSteps.switchToPage(pageName); // Delegate to sub-steps
    }

    @NeverTakeAllureScreenshotForThisStep
    @When("Switch to default page")
    public void switchToDefaultPageStep() {
        subSteps.switchBackToDefaultPage(); // Delegate to sub-steps
    }

    @NeverTakeAllureScreenshotForThisStep
    @When("Close {string} page")
    public void switchToDefaultPageStep(String pageName) {
        subSteps.closePage(pageName);
    }
    @NeverTakeAllureScreenshotForThisStep
    @When("Close window")
    public void closeWindow() {
        subSteps.closeWindow();
    }
}
