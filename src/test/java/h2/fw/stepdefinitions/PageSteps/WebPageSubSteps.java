package h2.fw.stepdefinitions.PageSteps;

import com.epam.reportportal.annotations.Step;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitUntilState;
import h2.fw.Report.ReportPortalAttachmentUtils;
import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.aspects.StepExt;
import h2.fw.stepdefinitions.BaseStep;
import org.testng.Assert;

public class WebPageSubSteps {
    private final BaseStep baseStep;
    private final EvaluationManager evaluationManager;

    public WebPageSubSteps(BaseStep baseStep) {
        this.baseStep = baseStep;
        this.evaluationManager = baseStep.getHooks().getEvaluationManager();  // Get EvaluationManager from Hooks
    }

    @StepExt("sub step")
    public void imFromSub(){
        Assert.assertEquals(3, 5);
        System.out.println("imFromSub");
    }

    private void openPageWithRetry(String url) {
        int maxRetries = 3;
        int retryCount = 0;
        boolean pageLoaded = false;

        while (retryCount < maxRetries && !pageLoaded) {
            try {
                System.out.println("Attempt " + (retryCount + 1) + " to load page: " + url);

                if (retryCount == 0) {
                    // Initial page load attempt using navigate
                    baseStep.getActivePlaywrightPage().navigate(url, new Page.NavigateOptions()
                            .setWaitUntil(WaitUntilState.LOAD)
                            .setTimeout(30000));  // 30 seconds timeout
                } else {
                    // Reload the page on subsequent attempts
                    System.out.println("Reloading page: " + url);
                    baseStep.getActivePlaywrightPage().reload(new Page.ReloadOptions()
                            .setWaitUntil(WaitUntilState.LOAD)
                            .setTimeout(30000));
                }

                pageLoaded = true;  // Page loaded successfully
            } catch (PlaywrightException e) {
                retryCount++;
                System.out.println("Failed to load page. Retrying... (" + retryCount + "/" + maxRetries + ")");
                if (retryCount == maxRetries) {
                    throw new RuntimeException("Failed to load page after " + maxRetries + " attempts: " + url, e);
                }
            }
        }
    }
    @StepExt("open url {urlEval} in browser")
    public void openUrlInBrowser(String urlEval) {
        openPageWithRetry(urlEval);
    }

    public void currentPageIsReloaded(String url) {
        baseStep.getActivePlaywrightPage().reload();
    }

    public void pageIsLoaded(String url) {
        baseStep.getActivePlaywrightPage().reload();
    }


}
