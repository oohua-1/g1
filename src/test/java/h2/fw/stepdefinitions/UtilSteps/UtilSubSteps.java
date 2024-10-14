package h2.fw.stepdefinitions.UtilSteps;

import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.HtmlElement.TypifiedElement;
import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.core.web.aspects.StepExt;
import h2.fw.runner.TestRunnerMain;
import h2.fw.stepdefinitions.BaseStep;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UtilSubSteps {
    private static final Logger LOGGER = LogManager.getLogger(UtilSubSteps.class.getName());

    private final BaseStep baseStep;
    private final EvaluationManager evaluationManager;
    public UtilSubSteps(BaseStep baseStep) {
        this.baseStep = baseStep;
        this.evaluationManager = baseStep.getEvaluationManager();
    }



    @NeverTakeAllureScreenshotForThisStep
    @StepExt("field {fieldName} is equal {expectedValueEval}")
    public void fieldStringEqual(String fieldName, String expectedValueEval){
        TypifiedElement element = baseStep.getElement(fieldName);
        String result = element.textContent();
        assertThat(result, equalTo(expectedValueEval));

    }

    @NeverTakeAllureScreenshotForThisStep
    public void waitFor(int seconds) throws InterruptedException {
        baseStep.getActivePlaywrightPage().waitForTimeout(seconds);
//        Thread.sleep(milliseconds);
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("value {valueEval} is saved to variable {variableName}")
    public void saveEvaluatedVariable(String valueEval, String variableName) {
        LOGGER.info(String.format("'%s' saved in variable '%s'", valueEval, variableName));
        evaluationManager.setEvaluatedVariable(variableName, valueEval);
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("value {value1Eval} equals {value2Eval}")
    public void compareEvaluatedValues(String value1Eval, String value2Eval) {
        assertThat(value1Eval, equalTo(value2Eval));
    }


    @NeverTakeAllureScreenshotForThisStep
    @StepExt("value {value1Eval} contains {value2Eval}")
    public void containsEvaluatedValue(String value1Eval, String value2Eval) {
        assertThat(value1Eval.contains(value2Eval), equalTo(true));
    }

    @NeverTakeAllureScreenshotForThisStep
    @StepExt("all value match conditions")
    public void allValueMatchConditions(List<Map<String, String>> rows) {
        for (Map<String, String> row : rows) {
            String param = evaluationManager.evaluateVariable(row.get("param"));
            String operation = row.get("operation").toLowerCase();
            String value = evaluationManager.evaluateVariable(row.get("value"));

            switch (operation) {
                case "equals":
                    assertThat("Expected value for param '" + param + "' to equal '" + value + "', but was '" + param + "'",
                            param, equalTo(value));
                    break;
                case "not equals":
                    assertThat("Expected value for param '" + param + "' to not equal '" + value + "', but was '" + param + "'",
                            param, not(equalTo(value)));
                    break;
                case "contains":
                    assertThat("Expected value for param '" + param + "' to contain '" + value + "', but it didn't",
                            param.contains(value), equalTo(true));
                    break;
                case "not contains":
                    assertThat("Expected value for param '" + param + "' to not contain '" + value + "', but it did",
                            param.contains(value), equalTo(false));
                    break;
                case "greater than":
                    assertThat("Expected value for param '" + param + "' to be greater than '" + value + "', but it wasn't",
                            Integer.parseInt(param) > Integer.parseInt(value), equalTo(true));
                    break;
                case "less than":
                    assertThat("Expected value for param '" + param + "' to be less than '" + value + "', but it wasn't",
                            Integer.parseInt(param) < Integer.parseInt(value), equalTo(true));
                    break;
                case "starts with":
                    assertThat("Expected value for param '" + param + "' to start with '" + value + "', but it didn't",
                            param.startsWith(value), equalTo(true));
                    break;
                case "ends with":
                    assertThat("Expected value for param '" + param + "' to end with '" + value + "', but it didn't",
                            param.endsWith(value), equalTo(true));
                    break;
                case "matches":
                    assertThat("Expected value for param '" + param + "' to match '" + value + "', but it didn't",
                            param.matches(value), equalTo(true));
                    break;
                case "is empty":
                    assertThat("Expected value for param '" + param + "' to be empty, but it wasn't",
                            param.isEmpty(), equalTo(true));
                    break;
                case "is not empty":
                    assertThat("Expected value for param '" + param + "' to not be empty, but it was",
                            !param.isEmpty(), equalTo(true));
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported operation: " + operation);
            }

        }
    }


}
