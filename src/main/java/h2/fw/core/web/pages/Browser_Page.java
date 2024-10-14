package h2.fw.core.web.pages;

import com.microsoft.playwright.Page;
import h2.fw.core.web.BasePage;

public class Browser_Page extends BasePage {

    public Browser_Page(Page page) {
        super(page);
//        locators.put("browserWindowsTitle_Text", page.locator("//h1[text()='Browser Windows']"));
//        locators.put("newTabButton_Button", page.locator("//button[text()='New Tab']"));
//        locators.put("tableLocator", page.locator("//table"));
//        locators.put("tableLocator1", page.locator("//table2"));

        // Define child locators relative to the parent tableLocator
        addChildLocator("tableLocator", "nameCell", "//tr/td[1]");
        addChildLocator("tableLocator", "ageCell", "//tr/td[2]");
        addChildLocator("tableLocator1", "jobCell", "//tr/td[3]");
    }


    @Override
    public boolean isLoaded() {
        // Example: Check if key elements on the Browser page are visible
        return checkElementsExistOnThePage("browserWindowsTitle_Text");
    }
}

