package h2.fw.core.web.pages;

import com.microsoft.playwright.Page;
import h2.fw.core.web.BasePage;

public class Example_Page extends BasePage {

    public Example_Page(Page page) {
        super(page);
        addStaticLocator("h1_Text", "//h1");
    }


    @Override
    public boolean isLoaded() {
        return checkElementsExistOnThePage("h1_Text");
    }
}

