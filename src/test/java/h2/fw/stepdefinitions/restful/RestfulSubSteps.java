package h2.fw.stepdefinitions.restful;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.api.RestfulComponent;
import h2.fw.core.web.api.WSComponents;
import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.core.web.aspects.StepExt;
import h2.fw.core.web.sql.SQLManager;
import h2.fw.exceptions.ExceptionUtils;
import h2.fw.stepdefinitions.BaseStep;
import h2.fw.stepdefinitions.UtilSteps.UtilSubSteps;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RestfulSubSteps {
    private final BaseStep baseStep;
    private final EvaluationManager evaluationManager;
    private final RestfulComponent restfulComponent;
    private final WSComponents wsComponents;
    private static final Logger LOGGER = LogManager.getLogger(RestfulSubSteps.class.getName());

    public RestfulSubSteps(BaseStep baseStep, EvaluationManager evaluationManager, RestfulComponent restfulComponent, WSComponents wsComponents) {
        this.baseStep = baseStep;
        this.evaluationManager = evaluationManager;
        this.restfulComponent = restfulComponent;
        this.wsComponents =  wsComponents;
    }
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";


    @NeverTakeAllureScreenshotForThisStep
    @StepExt("restful web service {serviceNameEval} is created with domain {domainEval}")
    public void loadTemplateAndInitRequest(String serviceNameEval, String domainEval) throws SQLException, IOException {
        SQLManager sqlManager = SQLManager.getInstance();
        String query = "SELECT api_protocol, api_method, api_path, api_template FROM restful_templates WHERE api_name = '" + serviceNameEval + "'";
        List<Map<String, Object>> restfulTemplate = sqlManager.executeQuery("LocalDB", query);

        if (restfulTemplate.isEmpty()) {
            throw new RuntimeException("No matching API info found for the given service name");
        }

        Map<String, Object> apiInfo = restfulTemplate.get(0);
        LOGGER.info(ANSI_RED + "API info: " + apiInfo + ANSI_RESET);

        String protocol = apiInfo.get("api_protocol") != null ? apiInfo.get("api_protocol").toString() : "";
        String path = apiInfo.get("api_path") != null ? apiInfo.get("api_path").toString() : "";
        String method = apiInfo.get("api_method") != null ? apiInfo.get("api_method").toString() : "";
        String template = apiInfo.get("api_template") != null ? apiInfo.get("api_template").toString() : "";

        restfulComponent.initRequest(protocol, domainEval, path, method, template);
    }



    public void loadTemplateAndInitRequestFromJson(String serviceNameEval, String domainEval) throws IOException {
        // Load JSON file
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> restfulTemplates = objectMapper.readValue(
                new File("/Users/h2/Downloads/h2_fw/configs/web/restful_templates.json"),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        // Find the matching API info
        Map<String, Object> apiInfo = restfulTemplates.stream()
                .filter(template -> serviceNameEval.equals(template.get("api_name")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No matching API info found for the given service name"));

//        LOGGER.info("API info: " + apiInfo);
        LOGGER.info(ANSI_RED + "API info: " + apiInfo + ANSI_RESET);

        String protocol = apiInfo.get("api_protocol") != null ? apiInfo.get("api_protocol").toString() : "";
        String path = apiInfo.get("api_path") != null ? apiInfo.get("api_path").toString() : "";
        String method = apiInfo.get("api_method") != null ? apiInfo.get("api_method").toString() : "";
        String template = apiInfo.get("api_template") != null ? apiInfo.get("api_template").toString() : "";

        // Initialize the request with template
        restfulComponent.initRequest(protocol, domainEval, path, method, template);
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("restful headers are set with value")
    public void setHeaders(List<Map<String, String>> rows) {
        Map<String, String> headers = new HashMap<>();
        for (Map<String, String> row : rows) {
            String key = row.get("params");
            String value = row.get("value");
            headers.put(key, value);
        }
        restfulComponent.setHeaders(headers);

    }
    public void setParams(DataTable table) {
        List<Map<String, String>> rows = evaluationManager.setEvaluateRows(table);

        Map<String, String> queryParams = new HashMap<>();
        Map<String, String> pathParams = new HashMap<>();

        for (Map<String, String> row : rows) {
            String key = row.get("params");
            String value = row.get("value");
            if (key.startsWith("path_")) {
                pathParams.put(key.substring(5), value); // Remove "path_" prefix
            } else {
                queryParams.put(key, value);
            }
        }

        System.out.println("Params: " + queryParams); // Debug: Print query parameters
        System.out.println("Path Params: " + pathParams); // Debug: Print path parameters

        restfulComponent.setParameters(queryParams);
        restfulComponent.setPathParameters(pathParams);
    }
    public void setBody(List<Map<String, String>> rows) {

        Map<String, String> bodyParams = new HashMap<>();

        for (Map<String, String> row : rows) {
            String key = row.get("params");
            String value = row.get("value");
            bodyParams.put(key, value);
        }

        System.out.println("Body Params: " + bodyParams);

        restfulComponent.setParsedBody(createBodyFromTemplate(wsComponents.getBody(), bodyParams));
    }

    private String createBodyFromTemplate(String template, Map<String, String> bodyParams) {
        for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        LOGGER.info("Generated Body: " + template);

        return template;
    }
    public void sendRestfulRequest(){
        ExceptionUtils.handleCheckedException(()->{
            restfulComponent.sendRequest();
//            if (!wsComponents.getBody().isEmpty()){
//                LOGGER.info("Body: " + restfulComponent.getParsedBody());
//            }

        });

    }
    public void checkResponseAsString() {
        String response = restfulComponent.getResponseAsString();
        System.out.println("Response as String: " + response);
    }
    public void checkResponseAsJson() {
        JsonNode response = restfulComponent.getResponseAsJson();
        System.out.println("Response as JSON: " + response.toString());
    }


    @NeverTakeAllureScreenshotForThisStep
    @StepExt("restful response value at {jsonPath} should be {expectedValueEval}")
    public void checkRestfulResponseValue(String jsonPath, String expectedValueEval) {
        Object actualValue = restfulComponent.getValueFromResponse(jsonPath);
        if (actualValue instanceof List) {
            actualValue = ((List<?>) actualValue).get(0);
        }

        // Check if the actual value is not null
        assertThat("The value at the given JSON path is null.", actualValue, is(notNullValue()));

        // Check if the actual value matches the expected value
        assertThat("The value at the given JSON path does not match the expected value.",
                actualValue.toString(), equalTo(expectedValueEval));
    }


    @NeverTakeAllureScreenshotForThisStep
    @StepExt("restful response value at {jsonPath} is saved to variable {variableName}")
    public void checkAndSaveToVariable(String jsonPath, String variableName) {
        Object actualValue = restfulComponent.getValueFromResponse(jsonPath);
        if (actualValue instanceof List) {
            actualValue = ((List<?>) actualValue).get(0);
        }
        evaluationManager.setEvaluatedVariable(variableName, String.valueOf(actualValue));
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("all jsonpath value from response are saved to variable")
    public void checkAndSaveToVariables(List<Map<String, String>> rows) {
        for (Map<String, String> row : rows) {
            String jsonPath = row.get("jsonpath");
            String variableName = row.get("variable");

            // Retrieve the actual value from the response using the JSON path
            Object actualValue = restfulComponent.getValueFromResponse(jsonPath);

            // If the value is a list, get the first item (as per your example)
            if (actualValue instanceof List) {
                actualValue = ((List<?>) actualValue).get(0);
            }

            // Save the value to the specified variable
            evaluationManager.setEvaluatedVariable(variableName, String.valueOf(actualValue));
        }
    }



    public void sendRestfulRequestWithRetry(int status1, int status2, int status3, int maxRetries, int delay) throws IOException, InterruptedException {
        List<Integer> retryStatusCodes = List.of(status1, status2, status3);
        restfulComponent.sendRequestWithRetry(maxRetries, Duration.ofSeconds(delay), retryStatusCodes);
    }



    @NeverTakeAllureScreenshotForThisStep
    @StepExt("all jsonpath value from response match condition")
    public void checkRestfulResponseValues(List<Map<String, String>> rows) {
        for (Map<String, String> row : rows) {
            String jsonPath = row.get("jsonpath");
            String operation = row.get("operation").toLowerCase();
            String expectedValue = row.get("value");

            Object actualValue = restfulComponent.getValueFromResponse(jsonPath);

            assertThat("The value at the given JSON path is null.", actualValue, is(notNullValue()));

            switch (operation) {
                case "equals":
                    if (actualValue instanceof List) {
                        List<?> actualList = (List<?>) actualValue;
                        List<?> expectedList = parseExpectedValueAsList(expectedValue);
                        assertThat("Expected array at JSON path '" + jsonPath + "' to equal '" + expectedList + "', but was '" + actualList + "'",
                                actualList, equalTo(expectedList));
                    } else if (actualValue instanceof Map) {
                        Map<?, ?> actualMap = (Map<?, ?>) actualValue;
                        Map<?, ?> expectedMap = parseExpectedValueAsMap(expectedValue);
                        assertThat("Expected object at JSON path '" + jsonPath + "' to equal '" + expectedMap + "', but was '" + actualMap + "'",
                                actualMap, equalTo(expectedMap));
                    } else {
                        assertThat("Expected value at JSON path '" + jsonPath + "' to equal '" + expectedValue + "', but was '" + actualValue + "'",
                                actualValue.toString(), equalTo(expectedValue));
                    }
                    break;

                case "not equals":
                    if (actualValue instanceof List) {
                        List<?> actualList = (List<?>) actualValue;
                        List<?> expectedList = parseExpectedValueAsList(expectedValue);
                        assertThat("Expected array at JSON path '" + jsonPath + "' to not equal '" + expectedList + "', but it did",
                                actualList, not(equalTo(expectedList)));
                    } else if (actualValue instanceof Map) {
                        Map<?, ?> actualMap = (Map<?, ?>) actualValue;
                        Map<?, ?> expectedMap = parseExpectedValueAsMap(expectedValue);
                        assertThat("Expected object at JSON path '" + jsonPath + "' to not equal '" + expectedMap + "', but it did",
                                actualMap, not(equalTo(expectedMap)));
                    } else {
                        assertThat("Expected value at JSON path '" + jsonPath + "' to not equal '" + expectedValue + "', but it did",
                                actualValue.toString(), not(equalTo(expectedValue)));
                    }
                    break;

                case "contains":
                    if (actualValue instanceof List) {
                        List<Object> actualList = (List<Object>) actualValue;
                        Object expectedItem = parseExpectedValue(expectedValue);
                        assertThat("Expected array at JSON path '" + jsonPath + "' to contain '" + expectedValue + "', but it didn't",
                                actualList, hasItem(expectedItem));
                    } else if (actualValue instanceof Map) {
                        Map<?, ?> actualMap = (Map<?, ?>) actualValue;
                        Map<?, ?> expectedMap = parseExpectedValueAsMap(expectedValue);
                        assertThat("Expected object at JSON path '" + jsonPath + "' to contain '" + expectedMap + "', but it didn't",
                                actualMap, IsMapContainingSubset.containsSubset(expectedMap));
                    } else {
                        assertThat("Expected value at JSON path '" + jsonPath + "' to contain '" + expectedValue + "', but it didn't",
                                actualValue.toString(), containsString(expectedValue));
                    }
                    break;

                case "not contains":
                    if (actualValue instanceof List) {
                        List<Object> actualList = (List<Object>) actualValue;
                        Object expectedItem = parseExpectedValue(expectedValue);
                        assertThat("Expected array at JSON path '" + jsonPath + "' to not contain '" + expectedValue + "', but it did",
                                actualList, not(hasItem(expectedItem)));
                    } else if (actualValue instanceof Map) {
                        Map<?, ?> actualMap = (Map<?, ?>) actualValue;
                        Map<?, ?> expectedMap = parseExpectedValueAsMap(expectedValue);
                        assertThat("Expected object at JSON path '" + jsonPath + "' to not contain '" + expectedMap + "', but it did",
                                actualMap, IsMapNotContainingSubset.doesNotContainSubset(expectedMap));
                    } else {
                        assertThat("Expected value at JSON path '" + jsonPath + "' to not contain '" + expectedValue + "', but it did",
                                actualValue.toString(), not(containsString(expectedValue)));
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported operation: " + operation);
            }
        }
    }


    private List<?> parseExpectedValueAsList(String expectedValue) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(expectedValue, List.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse expected value as a list: " + expectedValue, e);
        }
    }

    private Map<?, ?> parseExpectedValueAsMap(String expectedValue) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(expectedValue, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse expected value as a map: " + expectedValue, e);
        }
    }

    private Object parseExpectedValue(String expectedValue) {
        if (expectedValue.startsWith("[") || expectedValue.startsWith("{")) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(expectedValue, Object.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse expected value: " + expectedValue, e);
            }
        } else {
            return expectedValue;
        }
    }

    public void printValuesOfFieldsWithContext(String context, DataTable table) {
        JsonNode jsonResponse = restfulComponent.getResponseAsJson();

        if (jsonResponse == null) {
            LOGGER.error("Response body is null, unable to print values");
            return;
        }

        List<String> fields = table.asList();
        StringBuilder logMessage = new StringBuilder(ANSI_RED + context + ": "); // Start building the log message

        fields.forEach(field -> {
            JsonNode value = findFieldRecursively(jsonResponse, field);
            if (value != null && !value.isMissingNode()) {
                logMessage.append(field).append(" = ").append(value.asText()).append(", ");
            } else {
                logMessage.append(field).append(" = not found, ");
            }
        });

        // Remove trailing comma and space
        if (logMessage.length() > 2) {
            logMessage.setLength(logMessage.length() - 2);
        }

        logMessage.append(ANSI_RESET); // Add ANSI reset at the end
        LOGGER.info(logMessage.toString()); // Log the entire message in one line
    }

    private JsonNode findFieldRecursively(JsonNode node, String fieldName) {
        if (node.has(fieldName)) {
            return node.get(fieldName);
        }

        // If node is an object, iterate through its fields
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode childNode = entry.getValue();

            // Recursively search within objects and arrays
            if (childNode.isObject() || childNode.isArray()) {
                JsonNode result = findFieldRecursively(childNode, fieldName);
                if (result != null && !result.isMissingNode()) {
                    return result;
                }
            }
        }
        return null; // Field not found
    }


    public void printAllTrackedVariables() {
        Map<String, Object> beforeValues = evaluationManager.getTrackedBeforeValues();
        Map<String, Object> afterValues = evaluationManager.getTrackedAfterValues();

        LOGGER.info("Printing tracked variables:");

        // Collect all "before" values in one line
        StringBuilder beforeStringBuilder = new StringBuilder("Tracked Before: ");
        beforeValues.forEach((key, beforeValue) -> {
            beforeStringBuilder.append(String.format("%s: %s, ", key, beforeValue));
        });

        // Collect all "after" values in one line
        StringBuilder afterStringBuilder = new StringBuilder("Tracked After: ");
        afterValues.forEach((key, afterValue) -> {
            afterStringBuilder.append(String.format("%s: %s, ", key, afterValue));
        });

        // Print both lines
        LOGGER.info(ANSI_RED + beforeStringBuilder.toString() + ANSI_RESET);
        LOGGER.info(ANSI_RED + afterStringBuilder.toString() + ANSI_RESET);

        // Optionally clear the tracked values after printing
        evaluationManager.clearTracking();
    }



    public void trackAfterOperation(DataTable dataTable) {
        // Evaluate all values in the DataTable
        List<Map<String, String>> evaluatedRows = EvaluationManager.setEvaluatedRows(dataTable);

        // Loop through each row in the evaluated table and track the "after" values
        for (Map<String, String> row : evaluatedRows) {
            String variableName = row.get("variableName");  // e.g., "balance", "tax"
            String valueExpression = row.get("value");      // e.g., "%{balance}%", "%{tax}%"

            // Evaluate the value (this will resolve things like "%{balance}%")
            String evaluatedValue = evaluationManager.evalVariable(valueExpression);

            // Track the "after" value
            evaluationManager.trackVariableAfter(variableName, evaluatedValue);
        }
    }


    public void trackBeforeOperation(DataTable dataTable) {
        // Evaluate all values in the DataTable
        List<Map<String, String>> evaluatedRows = EvaluationManager.setEvaluatedRows(dataTable);

        // Loop through each row in the evaluated table and track the "before" values
        for (Map<String, String> row : evaluatedRows) {
            String variableName = row.get("variableName");  // e.g., "balance", "tax"
            String valueExpression = row.get("value");      // e.g., "%{balance}%", "%{tax}%"

            // Evaluate the value (this will resolve things like "%{balance}%")
            String evaluatedValue = evaluationManager.evalVariable(valueExpression);

            // Track the "before" value
            evaluationManager.trackVariableBefore(variableName, evaluatedValue);
        }
    }



}
