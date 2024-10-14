package h2.fw.stepdefinitions.FieldSteps;
import h2.fw.core.mobile.MobileElement.MobileElementWrapper;
import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.HtmlElement.DropdownList;
import h2.fw.core.web.HtmlElement.TypifiedElement;
import h2.fw.core.web.aspects.StepExt;
import h2.fw.stepdefinitions.BaseStep;
import h2.fw.stepdefinitions.MobileBaseStep;

public class MobileFieldSubSteps {

    private final MobileBaseStep mobileBaseStep;

    public MobileFieldSubSteps(MobileBaseStep mobileBaseStep) {
        this.mobileBaseStep = mobileBaseStep;

    }

    public void clickField(String fieldName) {
        MobileElementWrapper element = mobileBaseStep.getElement(fieldName);
        element.click();
    }


}
