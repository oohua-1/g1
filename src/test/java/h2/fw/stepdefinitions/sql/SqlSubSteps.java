package h2.fw.stepdefinitions.sql;

import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.core.web.aspects.StepExt;
import h2.fw.core.web.sql.SQLManager;
import h2.fw.exceptions.ExceptionUtils;
import h2.fw.stepdefinitions.BaseStep;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SqlSubSteps {
    private static final Logger LOGGER = LogManager.getLogger(SqlSubSteps.class.getName());

    private final BaseStep baseStep;
    private final EvaluationManager evaluationManager;
    private final SQLManager sqlManager = SQLManager.getInstance();
    public SqlSubSteps(BaseStep baseStep, EvaluationManager evaluationManager) {
        this.baseStep = baseStep;
        this.evaluationManager = evaluationManager;
    }

    public void executeQuery(String database, String query){
        ExceptionUtils.handleCheckedException(()->{
            sqlManager.executeQuery(database, query);

        });
    }

    public void performSQLDatabase(String database, String query){
        ExceptionUtils.handleCheckedException(()->{
            sqlManager.executeQueryWithResult(database, query);

        });
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("save the result from {column} at row {rowIndex} to variable {variableName}")
    public void saveResultToVariable(String database, String column, int rowIndex, String variableName) {
        // Retrieve the latest results from the specified database
        List<Map<String, String>> results = sqlManager.getLastQueryResults(database);

        // Ensure the rowIndex is within bounds
        if (rowIndex < 0 || rowIndex >= results.size()) {
            throw new IndexOutOfBoundsException("Row index is out of range.");
        }

        Map<String, String> row = results.get(rowIndex);
        String value = row.get(column);

        evaluationManager.setEvaluatedVariable(variableName, value);
    }


    @NeverTakeAllureScreenshotForThisStep
    @StepExt("the following values from database {database} should match")
    public void assertDatabaseValues(String database, List<Map<String, String>>rows) {
        List<Map<String, String>> results = sqlManager.getLastQueryResults(database);

        // Iterate over each row in the DataTable to perform assertions
        for (Map<String, String> row : rows) {
            String columnName = row.get("column");
            int rowIndex = Integer.parseInt(row.get("rowIndex"));
            String operation = row.get("operation").toLowerCase();
            String expectedValue = row.get("value");

            // Ensure the rowIndex is within bounds
            if (rowIndex < 0 || rowIndex >= results.size()) {
                throw new IndexOutOfBoundsException("Row index " + rowIndex + " is out of range.");
            }

            // Retrieve the actual value from the specified row and column
            String actualValue = results.get(rowIndex).get(columnName);

            // Perform the assertion based on the operation
            switch (operation) {
                case "equals":
                    assertThat("Value at row " + rowIndex + ", column '" + columnName + "' should equal '" + expectedValue + "'",
                            actualValue, equalTo(expectedValue));
                    break;

                case "not equals":
                    assertThat("Value at row " + rowIndex + ", column '" + columnName + "' should not equal '" + expectedValue + "'",
                            actualValue, not(equalTo(expectedValue)));
                    break;

                case "contains":
                    assertThat("Value at row " + rowIndex + ", column '" + columnName + "' should contain '" + expectedValue + "'",
                            actualValue, containsString(expectedValue));
                    break;

                case "not contains":
                    assertThat("Value at row " + rowIndex + ", column '" + columnName + "' should not contain '" + expectedValue + "'",
                            actualValue, not(containsString(expectedValue)));
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported operation: " + operation);
            }


        }
    }


    @When("wait up to {int} seconds, polling every {int} seconds, for SQL query to return {string} with value {string} in database {string}")
    public void waitForSQLConditionWithDynamicPolling(int timeoutInSeconds, int pollingIntervalInSeconds, String columnName, String expectedValue, String database, DataTable dataTable) {
        ExceptionUtils.handleCheckedException(() -> {
            List<Map<String, String>> rows = evaluationManager.setEvaluateRows(dataTable);
            StringBuilder queryBuilder = new StringBuilder();
            for (Map<String, String> row : rows) {
                String query = row.get("query");
                queryBuilder.append(query);
            }

            String finalQuery = queryBuilder.toString().trim();

            boolean conditionMet = pollForSQLCondition(database, finalQuery, columnName, expectedValue, timeoutInSeconds, pollingIntervalInSeconds);

            if (!conditionMet) {
                throw new RuntimeException("Condition not met within " + timeoutInSeconds + " seconds for column: " + columnName);
            }
        });
    }
    private boolean pollForSQLCondition(String database, String query, String columnName, String expectedValue, int timeoutInSeconds, int pollingIntervalInSeconds) throws SQLException, IOException {
        int elapsedSeconds = 0;

        while (elapsedSeconds < timeoutInSeconds) {
            // Execute the query and fetch results
            List<Map<String, Object>> results = sqlManager.executeQueryWithResult(database, query);

            if (!results.isEmpty()) {
                // Check if any record matches the expected condition
                for (Map<String, Object> record : results) {
                    if (expectedValue.equals(record.get(columnName))) {
                        LOGGER.info("Condition met: " + columnName + " = " + expectedValue);
                        return true;
                    }
                }
            }

            // Wait for the next polling interval
            try {
                Thread.sleep(pollingIntervalInSeconds * 1000); // Convert seconds to milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling interrupted", e);
            }

            elapsedSeconds += pollingIntervalInSeconds;
        }

        LOGGER.warn("Condition not met after " + timeoutInSeconds + " seconds for column: " + columnName);
        return false;
    }


}
