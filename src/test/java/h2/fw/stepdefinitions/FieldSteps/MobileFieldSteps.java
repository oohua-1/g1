package h2.fw.stepdefinitions.FieldSteps;

import h2.fw.core.web.EvaluationManager;
import h2.fw.stepdefinitions.BaseStep;
import h2.fw.stepdefinitions.MobileBaseStep;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MobileFieldSteps {
    private final MobileFieldSubSteps subSteps;
    private final MobileBaseStep mobileBaseStep;
    public MobileFieldSteps(MobileBaseStep mobileBaseStep) {
        this.mobileBaseStep = mobileBaseStep;
        this.subSteps = new MobileFieldSubSteps(mobileBaseStep);
    }

    public void whenFieldIsClicked(String fieldName) {
        subSteps.clickField(fieldName);  // Delegate to WebFieldSubSteps
    }
}
