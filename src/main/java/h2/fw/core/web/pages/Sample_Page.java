package h2.fw.core.web.pages;

import com.microsoft.playwright.Page;
import h2.fw.core.web.BasePage;

public class Sample_Page extends BasePage {

    public Sample_Page(Page page) {
        super(page);
//        locators.put("demoPage_Text", page.locator("//h1[text()='This is a sample page']"));

    }

    @Override
    public boolean isLoaded() {
        return checkElementsExistOnThePage("demoPage_Text");
    }
}
