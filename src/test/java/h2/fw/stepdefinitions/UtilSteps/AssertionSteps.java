package h2.fw.stepdefinitions.UtilSteps;

import h2.fw.core.web.aspects.ConditionalBlockManager;
import io.cucumber.java.en.When;

public class AssertionSteps{
    AssertionSubSteps subSteps;
    public AssertionSteps() {
        this.subSteps = new AssertionSubSteps(); // Initialize here

    }

    @When("SYSTEM: SOFT ASSERT MODE IS ACTIVATED")
    public void softAssertModeIsActive() {
        subSteps.softAssertModeIsActive();
    }

    @When("SYSTEM: SOFT ASSERT MODE IS DEACTIVATED")
    public void softAssertModeIsDeactivated() {
        subSteps.softAssertModeIsDeactivated();
    }

    @When("SYSTEM: START CONDITIONAL BLOCK")
    public void startConditionalBlock() {
       subSteps.startConditionalBlock();
    }

    @When("SYSTEM: END CONDITIONAL BLOCK")
    public void endConditionalBlock() {
        subSteps.endConditionalBlock();
    }

    @When("If the following step is successful")
    public void conditionIfFollowingStepIsSuccessful() {
        subSteps.conditionIfFollowingStepIsSuccessful();
    }

    @When("If not do the step")
    public void conditionElseDoTheStep() {
        subSteps.conditionElseDoTheStep();
        // This step will be handled by the aspect when condition 'else' is executed
    }

    @When("do the step")
    public void doStepInConditionalBlock() {
        subSteps.doStepInConditionalBlock();
        // This step will be executed based on the previous condition ('if' or 'else')
    }
}
