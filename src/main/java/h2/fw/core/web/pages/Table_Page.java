package h2.fw.core.web.pages;

import com.microsoft.playwright.Page;
import h2.fw.core.web.BasePage;

public class Table_Page extends BasePage {

    public Table_Page(Page page) {
        super(page);
        addStaticLocator("browserWindowsTitle_Text", "//h1[contains(.,'HTML Tables')]");
        addStaticLocator("tableLocator", "//table[@id='customers']");
        addDynamicLocatorTemplate("h2_Text", "//h2[normalize-space(text())='%s']");
//        locators.put("browserWindowsTitle_Text", page.locator("//h1[contains(.,'HTML Tables')]"));
//        locators.put("tableLocator", page.locator("//table[@id='customers']"));

        // Define child locators relative to the parent tableLocator
        addChildLocator("tableLocator", "nameCell", "/tbody[1]/tr[2]/td[1]");
    }


    @Override
    public boolean isLoaded() {
        // Example: Check if key elements on the Browser page are visible
        return checkElementsExistOnThePage("browserWindowsTitle_Text");
    }
}

