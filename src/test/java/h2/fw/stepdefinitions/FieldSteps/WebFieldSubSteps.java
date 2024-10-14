package h2.fw.stepdefinitions.FieldSteps;
import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.HtmlElement.DropdownList;
import h2.fw.core.web.HtmlElement.TypifiedElement;
import h2.fw.core.web.aspects.DontTakeScreenshotForSubstep;
import h2.fw.core.web.aspects.StepExt;
import h2.fw.stepdefinitions.BaseStep;
import h2.fw.stepdefinitions.UtilSteps.UtilSubSteps;
import io.cucumber.datatable.DataTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class WebFieldSubSteps {
    private static final Logger LOGGER = LogManager.getLogger(WebFieldSubSteps.class.getName());

    private final BaseStep baseStep;
    private final EvaluationManager evaluationManager;

    public WebFieldSubSteps(BaseStep baseStep) {
        this.baseStep = baseStep;
        this.evaluationManager = baseStep.getHooks().getEvaluationManager();  // Get EvaluationManager from Hooks

    }
    public void conditionalStep(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String conditionStep = row.get("condition");
            String doStep = row.get("do");
            String elseStep = row.get("else");

            // Evaluate the condition step (hardcoded for simplicity)
            boolean conditionPassed = evaluateConditionStep(conditionStep);

            // Execute the appropriate step based on the result of the condition
            if (conditionPassed) {
                executeFieldAction(doStep);  // Execute "do" step if condition passed
            } else {
                executeFieldAction(elseStep);  // Execute "else" step if condition failed
            }
        }
    }
    // Example of evaluating a simple condition (like "Then value '5' equals '5'")
    private boolean evaluateConditionStep(String conditionStep) {
        if (conditionStep.contains("equals")) {
            String[] parts = conditionStep.split("\"");
            String leftValue = parts[1];
            String rightValue = parts[3];
            return leftValue.equals(rightValue);
        }
        return false;  // Default to false if condition cannot be evaluated
    }
    // Executes the field action step dynamically (like "When field 'abc' is clicked")
    private void executeFieldAction(String stepText) {
        if (stepText.contains("is clicked")) {
            String fieldName = extractFieldName(stepText);
            clickField(fieldName);  // Dynamically call clickField
        } else if (stepText.contains("is focused")) {
            String fieldName = extractFieldName(stepText);
            focusField(fieldName);  // Dynamically call focusField
        } else if (stepText.contains("is checked radio button")) {
            String fieldName = extractFieldName(stepText);
            checkRadioButton(fieldName);  // Dynamically call checkRadioButton
        }
        // You can add more cases here to handle other step actions (double click, JS click, etc.)
    }

    // Utility method to extract the field name from step text (e.g., "When field 'abc' is clicked")
    private String extractFieldName(String stepText) {
        String[] parts = stepText.split("\"");
        return parts[1];  // Extract the field name between the quotes
    }
    public void clickField(String fieldName) {
        TypifiedElement element = baseStep.getElement(fieldName);
        element.click();
    }

    public void checkRadioButton(String fieldName) {
        TypifiedElement element = baseStep.getElement(fieldName);
        element.click();
    }

    public void focusField(String fieldName) {
        TypifiedElement element = baseStep.getElement(fieldName);
        element.focus();
    }

    public void clickFieldByJS(String fieldName) {
        TypifiedElement element = baseStep.getElement(fieldName);
        element.clickByJS();
    }

    public void doubleClickField(String fieldName) {
        TypifiedElement element = baseStep.getElement(fieldName);
        element.doubleClick();
    }

    @StepExt("field {fieldName} is filled with value {valueEval}")
    @DontTakeScreenshotForSubstep
    public void fillFieldWithValue(String fieldName, String valueEval) {
        TypifiedElement element = baseStep.getElement(fieldName);
        element.fill(valueEval);
    }

    @StepExt("Im from substep")
    public void substep() {
        System.out.println("SUBSTEPS");
    }

    @StepExt("field {fieldName} is filled with value {valueEval} (by Javascript)")
    public void fillFieldWithValueByJS(String fieldName, String valueEval) {
        TypifiedElement element = baseStep.getElement(fieldName);
        element.fillByJS(valueEval);
    }

    @StepExt("field {fieldName} is filled with value {valueEval} (by Javascript)")
    public void fillFieldWithValueAndEnter(String fieldName, String valueEval) {
        TypifiedElement element = baseStep.getElement(fieldName);
        element.fill(valueEval);
        element.press("Enter");

    }

    public void clearField(String fieldName) {
        TypifiedElement element = baseStep.getElement(fieldName);
        element.clear();
    }

    public void clearFieldByKeyboard(String fieldName) {
        TypifiedElement element = baseStep.getElement(fieldName);
        element.clearByKeyBoard();
    }

    public void selectOptionInDropdown(String fieldName, String optionText) {
        DropdownList dropdown = (DropdownList) baseStep.getElement(fieldName);
        dropdown.selectByValue(optionText);
    }



}
