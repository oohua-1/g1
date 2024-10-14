package h2.fw.stepdefinitions.sql;

import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.api.RestfulComponent;
import h2.fw.core.web.api.WSComponents;
import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.exceptions.ExceptionUtils;
import h2.fw.stepdefinitions.BaseStep;
import h2.fw.stepdefinitions.restful.RestfulSubSteps;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class SqlSteps {
    private static final Logger LOGGER = LogManager.getLogger(SqlSteps.class.getName());

    private final BaseStep baseStep;
    private final EvaluationManager evaluationManager;
    private final RestfulComponent restfulComponent;
    private final WSComponents wsComponents;
    private final SqlSubSteps subSteps;
    public SqlSteps(BaseStep baseStep){
        this.baseStep = baseStep;
        this.evaluationManager = baseStep.getEvaluationManager();
        this.restfulComponent = baseStep.getRestfulComponent();
        this.wsComponents = baseStep.getWsComponents();
        this.subSteps = new SqlSubSteps(baseStep, evaluationManager);


    }
    @When("execute script in database {string} with query")
    public void executeScriptInDatabase(String database, DataTable dataTable){
        ExceptionUtils.handleCheckedException(()->{
            List<Map<String, String>> rows = evaluationManager.setEvaluateRows(dataTable);
            StringBuilder queryBuilder = new StringBuilder();
            for (Map<String, String> row : rows) {
                String query = row.get("query");
                queryBuilder.append(query);

                // Optional: Add a semicolon or newline between queries if needed
//                queryBuilder.append("; ");
            }

            String finalQuery = queryBuilder.toString().trim();

            // Execute the concatenated query
            subSteps.executeQuery(database, finalQuery);
        });


    }

    @When("perform SQL query to database {string}")
    public void performSQLDatabase(String database, DataTable dataTable){
        ExceptionUtils.handleCheckedException(()->{
            List<Map<String, String>> rows = evaluationManager.setEvaluateRows(dataTable);
            StringBuilder queryBuilder = new StringBuilder();
            for (Map<String, String> row : rows) {
                String query = row.get("query");
                queryBuilder.append(query);

                // Optional: Add a semicolon or newline between queries if needed
//                queryBuilder.append("; ");
            }

            String finalQuery = queryBuilder.toString().trim();

            // Execute the concatenated query
            subSteps.performSQLDatabase(database, finalQuery);
        });


    }

    @NeverTakeAllureScreenshotForThisStep
    @Then("the latest result from database {string} is saved to variable")
    public void saveLatestResultToVariable(String database, DataTable dataTable) {
        List<Map<String, String>> rows = evaluationManager.setEvaluateRows(dataTable);

        for (Map<String, String> row : rows) {
            String column = row.get("column");
            int rowIndex = Integer.parseInt(row.get("rowIndex"));
            String variableName = row.get("variable");

            subSteps.saveResultToVariable(database, column, rowIndex, variableName);
        }
    }

    @NeverTakeAllureScreenshotForThisStep
    @Then("the following values from database {string} should match")
    public void assertDatabaseValues(String database, DataTable dataTable) {
        List<Map<String, String>> rows = evaluationManager.setEvaluateRows(dataTable);
        subSteps.assertDatabaseValues(database, rows);
    }

}
