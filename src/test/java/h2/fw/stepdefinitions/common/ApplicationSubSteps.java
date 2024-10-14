package h2.fw.stepdefinitions.common;
import com.microsoft.playwright.Page;

import h2.fw.stepdefinitions.BaseStep;

public class ApplicationSubSteps {

    private final BaseStep baseStep;

    public ApplicationSubSteps(BaseStep baseStep) {
        this.baseStep = baseStep;
    }

    public void switchToPage(String pageName) {
        baseStep.switchToPage(pageName); // Call method from BaseStep
    }

    public void switchBackToDefaultPage() {
        baseStep.switchBackToDefaultPage(); // Call method from BaseStep
    }

    public void closePage(String pageName) {
        baseStep.closePage(pageName); // Call method from BaseStep
    }

    public void closeWindow() {
        Page activePage = baseStep.getActivePlaywrightPage();
        if (activePage != null) {
            activePage.close();  // Close the active page
            baseStep.closeBrowser();  // Reset the browser state in BaseStep
        }
    }
}
