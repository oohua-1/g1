package h2.fw.core.mobile.MobileElement;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

public class MobileElementWrapper {
    protected WebElement element;
    protected AppiumDriver driver;

    public MobileElementWrapper(AppiumDriver driver, WebElement element) {
        this.driver = driver;
        this.element = element;
    }

    public void click() {
        element.click();
    }

    public void sendKeys(String value) {
        element.sendKeys(value);
    }

    public void clear() {
        element.clear();
    }

    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    public String getText() {
        return element.getText();
    }

    public void tap() {
        element.click();  // Alternatively, use a more specific tap action if needed
    }

    public void doubleTap() {
        // Implement double tap if needed, using TouchAction or other Appium gestures
    }

    public void longPress() {
        // Implement long press if needed, using TouchAction or other Appium gestures
    }

    // Add more methods as needed to encapsulate common interactions
}
