package h2.fw.core.web.pages;

import com.microsoft.playwright.Page;
import h2.fw.core.web.BasePage;

public class LoginSauceLab_Page extends BasePage {
    public LoginSauceLab_Page(Page page){
        super(page);
        addStaticLocator("h1_Text", "(//div[@class='login_container']//div)[1]");
        addStaticLocator("user_Input", "//input[@data-test='username']");
        addStaticLocator("password_Input", "//input[@type='password']");
        addStaticLocator("login_Button", "//input[@type='submit']");



    }
    @Override
    public boolean isLoaded() {
        return false;
    }
}
