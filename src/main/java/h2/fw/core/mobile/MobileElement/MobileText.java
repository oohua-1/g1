package h2.fw.core.mobile.MobileElement;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

public class MobileText extends MobileElementWrapper {
    public MobileText(AppiumDriver driver, WebElement element) {
        super(driver, element);
    }

    public void tap() {
        click();  // Or implement a specific tap action
    }
}

