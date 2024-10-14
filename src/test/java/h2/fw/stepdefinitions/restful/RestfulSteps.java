package h2.fw.stepdefinitions.restful;

import browserstack.shaded.jackson.databind.JsonNode;
import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.api.RestfulComponent;
import h2.fw.core.web.api.WSComponents;
import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.exceptions.ExceptionUtils;
import h2.fw.stepdefinitions.BaseStep;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RestfulSteps {
    private final BaseStep baseStep;
    private final RestfulSubSteps subSteps;
    private final EvaluationManager evaluationManager;
    private final RestfulComponent restfulComponent;
    private final WSComponents wsComponents;

    public RestfulSteps(BaseStep baseStep) {
        this.baseStep = baseStep;
        evaluationManager = baseStep.getEvaluationManager();
        restfulComponent = baseStep.getRestfulComponent();
        wsComponents = baseStep.getWsComponents();
        this.subSteps = new RestfulSubSteps(baseStep, evaluationManager, restfulComponent,wsComponents);

    }

    @NeverTakeAllureScreenshotForThisStep
    @Given("restful web service {string} is created with domain {string}")
    public void resultIsCreatedWithDomain(String service, String domain) throws SQLException, IOException {
        String serviceEval = evaluationManager.evaluateVariable(service);
        String domainEval = evaluationManager.evaluateVariable(domain);
        subSteps.loadTemplateAndInitRequestFromJson(serviceEval, domainEval);
    }

    @NeverTakeAllureScreenshotForThisStep
    @Given("restful headers are set with value$")
    public void restfulHeadersAreSetWithValue(DataTable table){
        List<Map<String, String>> rows = evaluationManager.setEvaluateRows(table);
        subSteps.setHeaders(rows);
    }

    @NeverTakeAllureScreenshotForThisStep
    @When("restful request is sent")
    public void restfulRequestIsSent() {
        ExceptionUtils.handleCheckedException(subSteps::sendRestfulRequest);
    }

    @NeverTakeAllureScreenshotForThisStep
    @Then("restful response value at {string} should be {string}")
    public void checkRestfulResponseValue(String jsonPath, String expectedValue) {
       String expectedValueEval = evaluationManager.evaluateVariable(expectedValue);
       subSteps.checkRestfulResponseValue(jsonPath, expectedValueEval);
    }

    @NeverTakeAllureScreenshotForThisStep
    @Then("all jsonpath value from response match condition$")
    public void checkRestfulResponseValues(DataTable dataTable) {
        List<Map<String, String>> rows = EvaluationManager.setEvaluatedRows(dataTable);
        subSteps.checkRestfulResponseValues(rows);
    }

    @NeverTakeAllureScreenshotForThisStep
    @Then("all jsonpath value from response are saved to variable$")
    public void checkAndSaveToVariables(DataTable dataTable) {
        List<Map<String, String>> rows = EvaluationManager.setEvaluatedRows(dataTable);
        subSteps.checkAndSaveToVariables(rows);
    }

    @NeverTakeAllureScreenshotForThisStep
    @Then("restful response value at {string} is saved to variable {string}")
    public void checkAndSaveToVariable(String jsonPath, String variableName) {
        subSteps.checkAndSaveToVariable(jsonPath, variableName);
    }

    @NeverTakeAllureScreenshotForThisStep
    @When("restful body are set with values$")
    public void checkAndSaveToVariable(DataTable dataTable) {
        List<Map<String, String>> rows = EvaluationManager.setEvaluatedRows(dataTable);
        subSteps.setBody(rows);
    }

    @Then("^print value of the field with context \"([^\"]*)\"$")
    public void printValuesOfFieldsWithContext(String context, DataTable table) {
        subSteps.printValuesOfFieldsWithContext(context, table);

    }

    @Then("track the following variables before operation$")
    public void trackBeforeOperation(DataTable dataTable) {
        subSteps.trackBeforeOperation(dataTable);
    }

    @Then("track the following variables after operation$")
    public void trackAfterOperation(DataTable dataTable) {
        subSteps.trackAfterOperation(dataTable);
    }
    @Then("print all tracked variables")
    public void printAllTrackedVariables() {
        subSteps.printAllTrackedVariables();
    }


}
