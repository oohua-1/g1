package h2.fw.stepdefinitions.UtilSteps;

import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.HtmlElement.TypifiedElement;
import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.stepdefinitions.BaseStep;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class UtilSteps {
    private final BaseStep baseStep;
    private final UtilSubSteps subSteps;
    private final EvaluationManager evaluationManager;
    public UtilSteps(BaseStep baseStep) {
        this.evaluationManager = baseStep.getEvaluationManager();
        this.baseStep = baseStep;
        this.subSteps = new UtilSubSteps(baseStep);  // Inject the same BaseStep instance
    }


    @NeverTakeAllureScreenshotForThisStep
    @Then("field {string} is equal {string}")
    public void getValue(String fieldName, String expectedValue) {
        String expectedValueEval = evaluationManager.evaluateVariable(expectedValue);
        subSteps.fieldStringEqual(fieldName, expectedValueEval);

    }
    @NeverTakeAllureScreenshotForThisStep
    @Then("wait for {string} seconds")
    public void waitSeconds(String seconds) throws InterruptedException {
//        int milliseconds = Integer.parseInt(seconds) * 1000;
        subSteps.waitFor(Integer.parseInt(seconds));
    }

    @NeverTakeAllureScreenshotForThisStep
    @Then("value {string} is saved to variable {string}")
    public void savedVariable(String value, String variable){
        String valueEval = evaluationManager.evaluateVariable(value);
        subSteps.saveEvaluatedVariable(valueEval, variable);
    }




    @NeverTakeAllureScreenshotForThisStep
    @Then("value {string} equals {string}")
    public void compareValue(String value1, String value2) {
        String value1Eval = evaluationManager.evaluateVariable(value1);
        String value2Eval = evaluationManager.evaluateVariable(value2);
        subSteps.compareEvaluatedValues(value1Eval, value2Eval);
    }

    @NeverTakeAllureScreenshotForThisStep
    @Then("load from {string} feature")
    public void loadFromFeature(String feature) {
        System.out.println(feature);
    }

    @Then("^create new \"([^\"]*)\" from \"([^\"]*)\" with$")
    public void loadFromFeature(String feature, String directory, DataTable dataTable) {
        // Convert the DataTable into a Map to handle the variables and values
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        // Example usage: you can now use the feature, directory, and data table
        System.out.println("Feature: " + feature);
        System.out.println("Directory: " + directory);

        // Iterate through the table entries
        for (Map.Entry<String, String> entry : data.entrySet()) {
            System.out.println("Variable: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        // Your logic to handle the feature and directory, and make use of the dataTable
    }

    @NeverTakeAllureScreenshotForThisStep
    @Then("value {string} contains {string}")
    public void containValue(String value1, String value2){
        String value1Eval = evaluationManager.evaluateVariable(value1);
        String value2Eval = evaluationManager.evaluateVariable(value2);
        subSteps.containsEvaluatedValue(value1Eval, value2Eval);
    }

    @NeverTakeAllureScreenshotForThisStep
    @Then("all value match conditions$")
    public void allMatchConditions(DataTable dataTable){
        List<Map<String, String>> rows = EvaluationManager.setEvaluatedRows(dataTable);
        subSteps.allValueMatchConditions(rows);
    }
    @When("the following values are saved to variables$")
    public void saveValuesToVariables(DataTable dataTable) {
        // Convert DataTable to a list of maps
        List<Map<String, String>> rows = EvaluationManager.setEvaluatedRows(dataTable);

        // Loop through each pair and save the value to the variable
        for (Map<String, String> row : rows) {
            String value = row.get("value");
            String variable = row.get("variable");

            // Evaluate the value and save it to the variable
            String valueEval = evaluationManager.evaluateVariable(value);
            subSteps.saveEvaluatedVariable(valueEval, variable);
        }
    }


}
