package h2.fw.core.web.pages;

import com.microsoft.playwright.Page;
import h2.fw.core.web.BasePage;

public class SuccessFull_Page extends BasePage{
    public SuccessFull_Page(Page page){
        super(page);
        addStaticLocator("h1_Text", "h1");

    }
    @Override
    public boolean isLoaded() {
        return checkElementsExistOnThePage("h1_Text");
    }
}
