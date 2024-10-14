package h2.fw.core.mobile.pages;


import h2.fw.core.mobile.MobileBasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;

public class Welcome_Page extends MobileBasePage {

    public Welcome_Page(AppiumDriver driver, String platform) {
        super(driver, platform);

        // Adding locators for Android
        addLocator("viewMenu_Button", "android", "accessibilityid", "View menu");
        addLocator("productTV_Text", "android", "id", "com.saucelabs.mydemoapp.android:id/productTV");
        addLocator("logIn_Button", "android", "xpath", "//*[@resource-id='com.saucelabs.mydemoapp.android:id/itemTV' and @text='Log In']");

        // Adding locators for iOS
        addLocator("viewMenu_Button", "ios", "accessibilityid", "More");
        addLocator("productTV_Text", "ios", "accessibilityid", "AppTitle Icons");
        addLocator("logIn_Button", "ios", "iosnspredicate", "name == 'Login'");

        // Adding locators for Flutter
        addLocator("username_Input", "flutter", "flutterkey", "usernameField");
        addLocator("password_Input", "flutter", "flutterkey", "passwordField");
        addLocator("login_Button", "flutter", "flutterkey", "loginButton");
    }

    @Override
    public boolean isLoaded() {
        String normalizedPlatform = platform.startsWith("flutter") ? "flutter" : platform;

        switch (normalizedPlatform) {
            case "android":
                return checkElementsExistOnThePage("viewMenu_Button");
            case "ios":
                return checkElementsExistOnThePage("viewMenu_Button");
            case "flutter":
                return checkElementsExistOnThePage("username_Input");
            default:
                throw new UnsupportedOperationException("Unsupported platform: " + normalizedPlatform);
        }
    }

}
