package h2.fw.core.web;

import io.cucumber.datatable.DataTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EvaluationManager {
    private static final Logger LOGGER = LogManager.getLogger(EvaluationManager.class.getName());

    // Singleton instance
    private static final EvaluationManager instance = new EvaluationManager();

    // Private constructor to prevent instantiation
    private EvaluationManager() {
    }

    public static EvaluationManager getInstance() {
        return instance;
    }

    private static final String MVEL_SCRIPT_PATTERN = "(?s)\\%\\{(?<script>.*?)\\}\\%";
    private static final Pattern MVEL_SCRIPT_PATTERN_COMPILED = Pattern.compile(MVEL_SCRIPT_PATTERN);

    // Cache for compiled MVEL expressions
    private static final Map<String, Serializable> expressionCache = new HashMap<>();

    // ThreadLocal variable to store variables for each thread
    private static final ThreadLocal<Map<String, Object>> variables = ThreadLocal.withInitial(HashMap::new);

    // ThreadLocal variable to store variables for each thread
    public static final ThreadLocal<Map<Object, Object>> scenarioVariableDb = ThreadLocal.withInitial(HashMap::new);

    private static final ThreadLocal<Map<String, Object>> trackedBeforeValues = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, Object>> trackedAfterValues = ThreadLocal.withInitial(HashMap::new);



    public static Map<String, Object> getVariables() {
        return variables.get();
    }

    // Track the "before" value
    public static void trackVariableBefore(String name, Object value) {
        trackedBeforeValues.get().put(name, value);
    }

    // Track the "after" value
    public static void trackVariableAfter(String name, Object value) {
        trackedAfterValues.get().put(name, value);
    }
    // Retrieve all tracked before values
    public static Map<String, Object> getTrackedBeforeValues() {
        return trackedBeforeValues.get();
    }

    // Retrieve all tracked after values
    public static Map<String, Object> getTrackedAfterValues() {
        return trackedAfterValues.get();
    }

    // Clear all tracked values after printing or when starting new scenarios
    public static void clearTracking() {
        trackedBeforeValues.get().clear();
        trackedAfterValues.get().clear();
    }
    public static Object getVariable(String name) {
        Object value = variables.get().get(name);
        LOGGER.info("Thread {} retrieved variable {} = {}", Thread.currentThread().getName(), name, value);
        return value;
    }


    public static void clearVariables() {
        LOGGER.info("Thread {} is clearing variables", Thread.currentThread().getName());
        variables.get().clear();
    }

    public static void removeVariable(String variable) {
        LOGGER.info("Thread {} is removing variable {}", Thread.currentThread().getName(), variable);
        variables.get().remove(variable);
    }

    private static Object evalMVEL(String expression) {
        // Set up the MVEL context
        Map<String, Object> context = new HashMap<>(variables.get());

        // Directly put the class with static methods into the context
        context.put("utils", ScripUtils.class); // You can rename this to anything you'd prefer

        LOGGER.info("Thread {} is evaluating expression: {}", Thread.currentThread().getName(), expression);

        // Compile or retrieve the expression from cache
        Serializable compiledExpression = expressionCache.computeIfAbsent(expression, MVEL::compileExpression);

        // Execute the expression
        return MVEL.executeExpression(compiledExpression, context);
    }

    public void setEvaluatedVariable(String name, String variable) {
        setVariable(name, variable);
    }

    public static void setVariable(String name, Object value) {
        LOGGER.info("Thread {} is setting variable {} = {}", Thread.currentThread().getName(), name, value);
        variables.get().put(name, value);
    }



    public <T> T evaluateVariable(String param) {
        return evalVariable(param);  // Delegate to the static method
    }

    public static <T> T evalVariable(String param) {
        try {
            if (param.trim().matches(".*" + MVEL_SCRIPT_PATTERN + ".*")) {
                Matcher mvelMatcher = MVEL_SCRIPT_PATTERN_COMPILED.matcher(param);
                StringBuffer mvelSB = new StringBuffer();
                while (mvelMatcher.find()) {
                    String script = mvelMatcher.group("script");
                    String value = String.valueOf(evalMVEL(script));
                    String valueQuote = Matcher.quoteReplacement(value);
                    mvelMatcher.appendReplacement(mvelSB, valueQuote);
                }
                mvelMatcher.appendTail(mvelSB);
                T result = evalVariable(mvelSB.toString());
                LOGGER.info(param + " evaluated to " + result);
                return result;
            }
            return (T) param;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError("Parsing error");
        }
    }

    public List<Map<String, String>> setEvaluateRows(DataTable table) {
        return setEvaluatedRows(table);  // Delegate to the static method
    }
    public static List<Map<String, String>> setEvaluatedRows(DataTable table) {
        // Check if the table has only one row
        if (table.height() == 1) {
            // Convert the single row into a list of maps
            return table.asLists(String.class).stream()
                    .map(row -> {
                        // Each row has two elements: the key (field name) and the value
                        return Map.of(row.get(0), evalVariable(row.get(1)).toString());
                    })
                    .collect(Collectors.toList());
        } else {
            // Convert DataTable to a List<Map<String, String>> for multi-row tables
            return table.asMaps(String.class, String.class).stream()
                    .map(row -> row.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> {
                                        Object evaluatedValue = evalVariable(entry.getValue());
                                        return evaluatedValue.toString();
                                    }
                            ))
                    )
                    .collect(Collectors.toList());
        }
    }


    // Method to manually print the DataTable
    private static void printDataTable(DataTable dataTable) {
        // Convert DataTable to List<List<String>>
        List<List<String>> rows = dataTable.cells();

        // Print each row
        for (List<String> row : rows) {
            System.out.println(row);
        }
    }
}

//}
