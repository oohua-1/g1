package h2.fw.stepdefinitions.UtilSteps;

import h2.fw.core.web.aspects.AssertExt;
import h2.fw.core.web.aspects.ConditionalBlockManager;
import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.core.web.aspects.StepExt;

public class AssertionSubSteps {

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("SYSTEM: SOFT ASSERT MODE IS ACTIVATED")
    public void softAssertModeIsActive() {
        AssertExt.activateSoftAssertMode();  // Activate soft assert mode
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("SYSTEM: SOFT ASSERT MODE IS DEACTIVATED")
    public void softAssertModeIsDeactivated() {
        AssertExt.deactivateSoftAssertMode();  // Deactivate soft assert mode and set the deactivation flag
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("SYSTEM: START CONDITIONAL BLOCK")
    public void startConditionalBlock() {
        ConditionalBlockManager.startConditionalBlock();
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("SYSTEM: END CONDITIONAL BLOCK")
    public void endConditionalBlock() {
        ConditionalBlockManager.endConditionalBlock();
        ConditionalBlockManager.clearState();
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("If the following step is successful")
    public void conditionIfFollowingStepIsSuccessful() {
        // This step will be handled by the aspect when condition 'if' is successful
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("If not do the step")
    public void conditionElseDoTheStep() {
        // This step will be handled by the aspect when condition 'else' is executed
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("do the step")
    public void doStepInConditionalBlock() {
        System.out.println("jump hgere");
        // This step will be executed based on the previous condition ('if' or 'else')
    }
}
