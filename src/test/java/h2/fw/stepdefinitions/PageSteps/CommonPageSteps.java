package h2.fw.stepdefinitions.PageSteps;

import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.core.web.aspects.SameStep;
import h2.fw.stepdefinitions.BaseStep;
import h2.fw.stepdefinitions.MobileBaseStep;
import h2.fw.stepdefinitions.StepDispatcher;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Arrays;

public class CommonPageSteps {
    private static final Logger LOGGER = LogManager.getLogger(CommonPageSteps.class.getName());

    private final StepDispatcher stepDispatcher;


    // PicoContainer will inject BaseStep and MobileBaseStep automatically
    public CommonPageSteps(BaseStep baseStep, MobileBaseStep mobileBaseStep) {
        this.stepDispatcher = new StepDispatcher(baseStep, mobileBaseStep);
    }

    @Given("Page {string} is loaded")
    public void pageIsLoaded(String pageName) {
        stepDispatcher.dispatchStep("PageSteps", "pageIsLoaded", pageName);  // Dynamically invoke method
    }

    @When("field {string} is clicked")
    public void whenFieldIsClicked(String fieldName) {
        stepDispatcher.dispatchStep("FieldSteps", "whenFieldIsClicked", fieldName);  // Dynamically invoke method
    }




    // Additional step methods can use the same pattern...
}