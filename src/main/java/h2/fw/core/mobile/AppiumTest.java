package h2.fw.core.mobile;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AppiumTest {
    public static void main(String[] args) {
        // Define Desired Capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:app", "/Users/habeo/Downloads/h2_fw/temp/mda-2.0.2-23.apk");
        caps.setCapability("appium:appPackage", "com.saucelabs.mydemoapp.android");
        caps.setCapability("appium:appActivity", ".view.activities.SplashActivity");
        caps.setCapability("appium:autoGrantPermissions", true);
        caps.setCapability("appium:deviceName", "android");
        caps.setCapability("appium:recordVideo", true);
        caps.setCapability("appium:dontStopAppOnReset", true);
        caps.setCapability("appium:enablePerformanceLogging", true);
        caps.setCapability("appium:eventTimings", true);
        caps.setCapability("appium:newCommandTimeout", 12000);
        caps.setCapability("appium:noSign", true);
        caps.setCapability("appium:platformName", "android");
        caps.setCapability("appium:printPageSourceOnFindFailure", true);
        caps.setCapability("appium:skipUnlock", true);
        caps.setCapability("appium:setAllowInvisibleElements", true);
        caps.setCapability("appium:adbExecTimeout", 45000);
        caps.setCapability("appium:appWaitDuration", 45000);
        caps.setCapability("df:recordVideo", true);
        caps.setCapability("df:build", "New2");

        // Initialize the Appium Driver
        AndroidDriver driver = null;
        try {
            driver = new AndroidDriver(new URL("http://0.0.0.0:31337/wd/hub"), caps);

            // Define the parameters for setting session status
            Map<String, Object> statusParams = new HashMap<>();
            statusParams.put("status", "passed"); // or "failed"

            // Define the parameters for setting session name
            Map<String, Object> nameParams = new HashMap<>();
            nameParams.put("name", "testname"); // Ensure this is a string

            // Execute the scripts
            driver.executeScript("devicefarm: setSessionStatus", statusParams);
            driver.executeScript("devicefarm: setSessionName", nameParams);


            // Your test code here
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
