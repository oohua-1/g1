package h2.fw.stepdefinitions.PageSteps;

import com.epam.reportportal.annotations.Step;
import h2.fw.Report.ReportPortalAttachmentUtils;
import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.aspects.AssertExt;
import h2.fw.core.web.aspects.NeverTakeAllureScreenshotForThisStep;
import h2.fw.runner.TestRunnerMain;
import h2.fw.stepdefinitions.BaseStep;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class WebPageSteps {
    private final BaseStep baseStep;
    private final WebPageSubSteps subSteps;
    private final EvaluationManager evaluationManager;

    public WebPageSteps(BaseStep baseStep) {
        this.evaluationManager = baseStep.getEvaluationManager();
        this.baseStep = baseStep;
        this.subSteps = new WebPageSubSteps(baseStep);
    }

    private static final Logger LOGGER = LogManager.getLogger(WebPageSteps.class.getName());


    public void pageIsLoaded(String pageName) {
        baseStep.setActivePageInstance(pageName);
        baseStep.getActivePageInstance().isLoaded();
    }

    @NeverTakeAllureScreenshotForThisStep
    @Given("I open the browser")
    public void openBrowser() {
        baseStep.ensureBrowserIsStarted();
        LOGGER.info("OPENED BROWSER");
    }


    @NeverTakeAllureScreenshotForThisStep
    @Given("open url {string} in browser")
    public void openUrl(String url) {
        String urlEval = evaluationManager.evaluateVariable(url);
        subSteps.openUrlInBrowser(urlEval);
    }

    @Given("Im from main")
    public void fromMain() {

        String tableContent = "<table border='1'><tr><th>params</th><th>value</th></tr>" +
                "<tr><td>Content-type</td><td>application-json</td></tr>" +
                "<tr><td>Another param</td><td>Another value</td></tr></table>";
//
        ReportPortalAttachmentUtils.logTableAsHtml(tableContent);

        subSteps.imFromSub();
    }

    @Then("Im inside the block assertion")
    public void insideTheBlock() {
        LOGGER.info("Inside the block assertion");

    }


    @Then("Im outside the block assertion")
    public void outsideTheBlock() {
        LOGGER.info("outside the block assertion");

    }


    @NeverTakeAllureScreenshotForThisStep
    @Given("current page is reloaded")
    public void currentPageIsReloaded() {
        baseStep.getActivePlaywrightPage().reload();
    }

}
