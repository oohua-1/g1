package h2.fw.core.mobile.MobileElement;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

public class MobileButton extends MobileElementWrapper {
    public MobileButton(AppiumDriver driver, WebElement element) {
        super(driver, element);
    }

    public void tap() {
        click();  // Or implement a specific tap action
    }
}

