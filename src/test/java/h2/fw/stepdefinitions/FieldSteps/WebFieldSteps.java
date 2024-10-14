package h2.fw.stepdefinitions.FieldSteps;

import h2.fw.Report.ReportAllureManager;
import h2.fw.Report.ReportPortalAttachmentUtils;
import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.HtmlElement.TypifiedElement;
import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.runner.TestRunnerMain;
import h2.fw.stepdefinitions.BaseStep;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WebFieldSteps {
    private static final Logger LOGGER = LogManager.getLogger(WebFieldSteps.class.getName());

    private final WebFieldSubSteps subSteps;
    private final EvaluationManager evaluationManager;
    private final BaseStep baseStep;
    public WebFieldSteps(BaseStep baseStep) {
        this.baseStep = baseStep;
        evaluationManager = baseStep.getEvaluationManager();
        this.subSteps = new WebFieldSubSteps(baseStep);
    }
    @Then("conditional step")
    public void conditionalStep(DataTable dataTable) {
        subSteps.conditionalStep(dataTable);
    }
    public void whenFieldIsClicked(String fieldName) {
        subSteps.clickField(fieldName);  // Delegate to WebFieldSubSteps
    }

    @When("field {string} is checked radio button")
    public void whenFieldIsChecked(String fieldName) {
        subSteps.checkRadioButton(fieldName);  // Delegate to WebFieldSubSteps
    }

    @When("field {string} is focused")
    public void whenFieldIsFocused(String fieldName) {
        subSteps.focusField(fieldName);  // Delegate to WebFieldSubSteps
    }

    @When("field {string} is clicked (by Javascript)")
    public void whenFieldIsClickedByJS(String fieldName) {
        subSteps.clickFieldByJS(fieldName);  // Delegate to WebFieldSubSteps
    }

    @When("field {string} is double clicked")
    public void whenFieldIsDoubleClicked(String fieldName) {
        subSteps.doubleClickField(fieldName);  // Delegate to WebFieldSubSteps
    }

    @When("field {string} is filled with value {string}")
    public void whenFieldIsFilledWithValue(String fieldName, String value) {
        String valueEval = evaluationManager.evaluateVariable(value);
        subSteps.fillFieldWithValue(fieldName, valueEval);  // Delegate to WebFieldSubSteps
    }

    @Then("Main step")
    public void whenMainstep() {
        subSteps.substep();  // Delegate to WebFieldSubSteps
    }


    @When("field {string} is filled with value {string} (by Javascript)")
    public void whenFieldIsFilledWithValueByJavascript(String fieldName, String value) {
        String valueEval = evaluationManager.evaluateVariable(value);
        subSteps.fillFieldWithValueByJS(fieldName, valueEval);  // Delegate to WebFieldSubSteps
    }

    @When("field {string} is filled with value {string} and Enter")
    public void whenFieldIsFilledWithValueAndEnter(String fieldName, String value) {
        String valueEnv = evaluationManager.evaluateVariable(value);
        subSteps.fillFieldWithValueAndEnter(fieldName, valueEnv);  // Delegate to WebFieldSubSteps
    }

    @When("field {string} is cleared")
    public void whenFieldIsCleared(String fieldName) {
        subSteps.clearField(fieldName);  // Delegate to WebFieldSubSteps
    }

    @When("field {string} is cleared by keyboard")
    public void whenFieldIsClearedByKeyboard(String fieldName) {
        subSteps.clearFieldByKeyboard(fieldName);  // Delegate to WebFieldSubSteps
    }



    @Then("validate negative case$")
    public void validateNegativeCases(DataTable dataTable) {
        List<Map<String, String>> rows = EvaluationManager.setEvaluatedRows(dataTable);
        ReportPortalAttachmentUtils.logDataTableAsHtml(dataTable);

        // Iterate over each row (each negative test case)
        for (Map<String, String> row : rows) {
            String caseNumber = row.get("case");
            if (caseNumber == null || caseNumber.equals("------")) {
                continue; // Skip this iteration
            }
            String actionsString = row.get("actions");
            String validationsString = row.get("validations");

            // Log or print the case number if needed
            LOGGER.info("Executing case: " + caseNumber);

            // Perform the actions
            performActions(actionsString);

            // Validate the result
            validateResults(validationsString, caseNumber);


        }
//        ReportAllureManager.attachScreenshotsInGrid("Validation Screenshots");
        ReportPortalAttachmentUtils.attachScreenshotsInGrid("Validation Screenshots");
    }

    private void performActions(String actionsString) {
        String[] actions = actionsString.split(", ");

        for (String action : actions) {
            // Extract the elementLocator and actions
            String elementLocator = action.substring(0, action.indexOf("["));
            String actionsBlock = action.substring(action.indexOf("[do: ") + 5, action.lastIndexOf("]"));

            // Split the actions by the arrow (->) delimiter
            String[] actionParts = actionsBlock.split(" -> ");

            for (String actionPart : actionParts) {
                String actionType;
                String actionValue = null;

                // If the action contains a value, split by parentheses
                if (actionPart.contains("(")) {
                    actionType = actionPart.substring(0, actionPart.indexOf("("));
                    actionValue = actionPart.substring(actionPart.indexOf("(") + 1, actionPart.lastIndexOf(")"));
                } else {
                    actionType = actionPart;
                }

                performSingleAction(elementLocator, actionType, actionValue);
            }
        }

    }



    private boolean isActionKeyword(String part) {
        // Define your set of action keywords here
        Set<String> actionKeywords = new HashSet<>(Arrays.asList("fill", "clear", "click", "verify"));
        return actionKeywords.contains(part.toLowerCase());
    }



    private void performSingleAction(String inputLocator, String inputAction, String inputValue) {
        switch (inputAction.toLowerCase()) {
            case "fill":
                subSteps.fillFieldWithValue(inputLocator, inputValue);  // Fill the field with the given value
                break;
            case "clear":
                subSteps.clearField(inputLocator);  // Clear the field
                break;
            case "click":
                subSteps.clickField(inputLocator);  // Click the element
                break;
            case "contains":
                String actualValue = getValueForLocator(inputLocator);  // Get the actual value
                assertThat("Validation failed: Expected value does not match actual value for " + inputLocator,
                        actualValue, containsString(inputValue));  // Verify using Hamcrest
                break;
            // Add more actions as needed
        }
    }

    private String getValueForLocator(String inputLocator) {
        TypifiedElement element = baseStep.getElement(inputLocator);

        String value = element.textContent(); // or use element.getValue() if it's an input field

        return value;
    }


    private void validateResults(String validationsString, String caseNumber) {
        if (validationsString == null || validationsString.equals("-")) {
            return;  // No validation needed
        }

        String[] validations = validationsString.split(", ");
        for (String validation : validations) {
            // Validate the format to ensure it's correct
            if (!validation.contains("[do: ")) {
                throw new IllegalArgumentException("Invalid validation format: " + validation);
            }

            // Extract the element locator and validation action
            String elementLocator = validation.substring(0, validation.indexOf("["));
            String validationBlock = validation.substring(validation.indexOf("[do: ") + 5, validation.lastIndexOf("]"));

            // Split by "->" to handle multiple validations if needed
            String[] validationParts = validationBlock.split(" -> ");
            for (String validationPart : validationParts) {
                String validationType;
                String expectedValue = null;

                // If the validation contains a value, extract it
                if (validationPart.contains("(")) {
                    validationType = validationPart.substring(0, validationPart.indexOf("("));
                    expectedValue = validationPart.substring(validationPart.indexOf("(") + 1, validationPart.lastIndexOf(")"));
                } else {
                    validationType = validationPart;
                }

                performValidation(elementLocator, validationType, expectedValue, caseNumber);
            }
        }
    }


    private void performValidation(String elementLocator, String validationType, String expectedValue, String caseNumber) {
        // Get the element and its text content
        TypifiedElement element = baseStep.getElement(elementLocator);
        String actualValue = element.textContent();

        // Unique name for each screenshot
        String screenshotName = "Case " + caseNumber + " - Validation: " + elementLocator + " - " + validationType;
        ReportPortalAttachmentUtils.collectScreenshot(screenshotName);

        switch (validationType.toLowerCase()) {
            case "contains":
                assertThat("Validation failed: Expected value not found in actual value for " + elementLocator,
                        actualValue, containsString(expectedValue));
                break;
            case "equals":
                assertThat("Validation failed: Expected value does not match actual value for " + elementLocator,
                        actualValue, equalTo(expectedValue));
                break;
            // Add more validation types as needed
        }
    }



}
