package h2.fw.core.web.pages;

import com.microsoft.playwright.Page;
import h2.fw.core.web.BasePage;

public class Login_Page extends BasePage{
    public Login_Page(Page page){
        super(page);
        addStaticLocator("h1_Text", "//h2[normalize-space()='Test login']");
        addStaticLocator("userName_Input", "//input[@id='username']");
        addStaticLocator("password_Input", "//input[@id='password']");
        addStaticLocator("submit_Button", "//button[@id='submit']");
        addStaticLocator("error_Text", "//div[@id='error']");

    }
    @Override
    public boolean isLoaded() {
        return checkElementsExistOnThePage("h1_Text");
    }
}
