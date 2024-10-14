package h2.fw.stepdefinitions;

import h2.fw.core.mobile.MobileBasePage;
import h2.fw.core.mobile.MobileElement.MobileButton;
import h2.fw.core.mobile.MobileElement.MobileElementWrapper;
import h2.fw.core.mobile.MobileElement.MobileText;
import h2.fw.core.mobile.MobilePageFactory;
import h2.fw.mobileHooks.MobileHooks;
import h2.fw.utils.SystemConfigManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.ashwith.flutter.FlutterElement;
import io.github.ashwith.flutter.FlutterFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.devicefarm.FlutterBy;
import org.devicefarm.FlutterCommands;
import org.devicefarm.models.ScrollOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class MobileBaseStep {
    private static final Logger LOGGER = LogManager.getLogger(MobileBaseStep.class.getName());
    private static final int DEFAULT_TIMEOUT = 30;
    protected AppiumDriver driver;  // The AppiumDriver object
    private MobileBasePage activePageInstance;  // The instance of a specific page class like WelcomePage
    private final MobileHooks hooks;
    private final SystemConfigManager systemConfigManager = SystemConfigManager.getInstance();
    public MobileBaseStep(MobileHooks hooks) {
        this.hooks = hooks;
        this.driver = null;  // Initially null, will be set upon first driver initialization
    }

    public MobileHooks getHooks() {
        return hooks;
    }

    public void ensureDriverIsStarted() {
        if (driver == null || driver.getSessionId() == null) {
            driver = hooks.getDriver();  // This triggers the driver to start if it hasn't already
        }
    }

    public AppiumDriver getActiveDriver() {
        ensureDriverIsStarted();
        return this.driver;
    }

    public void closeDriver() {
        driver = null;
        activePageInstance = null;  // Clear the active page instance
    }

    // Method to set the active page instance based on the page name
    public void setActivePageInstance(String pageName, String platform) {
        LOGGER.info("Setting active page instance for: " + pageName);
        activePageInstance = MobilePageFactory.getInstanceOfCurrentPage(pageName, driver, platform);

        if (activePageInstance == null) {
            LOGGER.error("Failed to set active page instance for: " + pageName);
            throw new IllegalStateException("Unable to set the active page instance. Page not found: " + pageName);
        }
    }

    // Method to get the active page instance
    public MobileBasePage getActivePageInstance() {
        if (activePageInstance == null) {
            throw new IllegalStateException("No active page instance is set. Make sure to call setActivePageInstance() first.");
        }
        return activePageInstance;
    }

    // Method to get the element's Locator by field name (handles both static and dynamic)
    public Object getElementLocator(String fieldName, String... dynamicValues) {
        return activePageInstance.getLocator(fieldName, dynamicValues); // Get the locator from the active page
    }

    public WebElement findElementDirectly(String strategy, String locatorValue) {
        switch (strategy.toLowerCase()) {
            case "accessibilityid":
                return driver.findElement(AppiumBy.accessibilityId(locatorValue));
            case "id":
                return driver.findElement(AppiumBy.id(locatorValue));
            case "xpath":
                return driver.findElement(AppiumBy.xpath(locatorValue));
            case "classname":
                return driver.findElement(AppiumBy.className(locatorValue));
            case "iosnspredicate":
                return driver.findElement(AppiumBy.iOSNsPredicateString(locatorValue));
            case "iosclasschain":
                return driver.findElement(AppiumBy.iOSClassChain(locatorValue));
            case "androiduiautomator":
                return driver.findElement(AppiumBy.androidUIAutomator(locatorValue));
            case "flutterkey":
                FlutterFinder flutterFinder = new FlutterFinder(driver);
                return flutterFinder.byValueKey(locatorValue);
            case "fluttertext":
                flutterFinder = new FlutterFinder(driver);
                return flutterFinder.byText(locatorValue);
            default:
                throw new IllegalArgumentException("Unsupported locator strategy: " + strategy);
        }
    }

    public MobileElementWrapper getElement(String fieldName, String... dynamicValues) {
        return getElement(fieldName, DEFAULT_TIMEOUT * 1000, dynamicValues); // Default timeout is 30 seconds
    }
    public MobileElementWrapper getElement(String fieldName, int timeout, String... dynamicValues) {
        Object locator = getElementLocator(fieldName, dynamicValues);
        WebElement element;

        try {
            if (locator instanceof By) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout / 1000));  // Timeout in seconds
                element = wait.until(ExpectedConditions.visibilityOfElementLocated((By) locator));  // Wait until visible for standard By locators
            } else if (locator instanceof FlutterElement) {
                scrollToElement(fieldName, dynamicValues);  // Scroll to the element first
                element = waitForFlutterElement((FlutterElement) locator, timeout);  // Wait until visible for FlutterElement
            } else {
                throw new RuntimeException("Unsupported locator type: " + locator.getClass().getName());
            }
        } catch (Exception e) {
            LOGGER.error("Element not found or not interactable: " + fieldName, e);
            throw new RuntimeException("Element not found or not visible after scrolling: " + fieldName, e);
        }

        // Return appropriate MobileElementWrapper subclass based on the element type
        if (element != null) {
            if (fieldName.endsWith("_Button")) {
                return new MobileButton(driver, element);
            } else if (fieldName.endsWith("_Input") || fieldName.endsWith("_Text")) {
                return new MobileText(driver, element);
            } else {
                return new MobileElementWrapper(driver, element);
            }
        } else {
            throw new RuntimeException("Element not found or not displayed: " + fieldName);
        }
    }
    private WebElement waitForFlutterElement(FlutterElement flutterElement, int timeout) {
        try {
            // Use the flutter:waitFor command to wait until the Flutter element is visible and interactable
            driver.executeScript("flutter:waitFor", flutterElement.getId(), timeout);
            return flutterElement;  // Return the element if it's visible
        } catch (Exception e) {
            throw new RuntimeException("Flutter element not visible after waiting: " + flutterElement.getId(), e);
        }
    }
    private void scrollToElement(String fieldName, String... dynamicValues) {
        String platform = systemConfigManager.getMobileOs();

        if (platform.equalsIgnoreCase("android")) {
            scrollToElementAndroid(fieldName, dynamicValues);
        } else if (platform.equalsIgnoreCase("ios")) {
            scrollToElementIOS(fieldName, dynamicValues);
        } else if (platform.startsWith("flutter")) {
            scrollToElementFlutter(fieldName, dynamicValues);
        } else {
            throw new UnsupportedOperationException("Scrolling not supported for platform: " + platform);
        }
    }


    private void scrollToElementAndroid(String fieldName, String... dynamicValues) {
        String locator = String.format("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(%s)",
                ((By) getElementLocator(fieldName, dynamicValues)).toString());
        driver.findElement(AppiumBy.androidUIAutomator(locator));
    }

    private void scrollToElementIOS(String fieldName, String... dynamicValues) {
        RemoteWebElement element = (RemoteWebElement) getElementLocator(fieldName, dynamicValues);
        HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("element", element.getId());
        scrollObject.put("toVisible", "true");
        driver.executeScript("mobile:scroll", scrollObject);
    }
    private void scrollToElementFlutter(String fieldName, String... dynamicValues) {
        FlutterElement flutterElement = (FlutterElement) getElementLocator(fieldName, dynamicValues);

        if (flutterElement == null) {
            throw new RuntimeException("Locator for field '" + fieldName + "' not found.");
        }

        // Define the parameters for scrolling, e.g., alignment and timeout
        Map<String, Object> scrollParams = new HashMap<>();
        scrollParams.put("alignment", 0.1);  // Adjust alignment as needed
        scrollParams.put("timeout", 30000);  // Set timeout to 30 seconds

        // Execute the scroll command using the 'id' of the FlutterElement
        try {
            driver.executeScript("flutter:scrollIntoView", flutterElement.getId(), scrollParams);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while scrolling to the element: " + fieldName, e);
        }
    }

//    private void scrollToElementFlutter(String fieldName, String... dynamicValues) {
//
//        FlutterBy.FlutterLocator flutterLocator = (FlutterBy.FlutterLocator) getElementLocator(fieldName, dynamicValues);
//
//        ScrollOptions scrollOptions = new ScrollOptions(flutterLocator, ScrollOptions.ScrollDirection.DOWN);
//
//        WebElement element = FlutterCommands.scrollTillVisible(driver, scrollOptions);
//
//        if (element == null || !element.isDisplayed()) {
//            throw new RuntimeException("Element not found or not visible after scrolling: " + fieldName);
//        }
//    }


    //    public MobileElementWrapper getElement(String fieldName, int timeout, String... dynamicValues) {
//        Object locator = getElementLocator(fieldName, dynamicValues);
//        WebElement element;
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout / 1000));  // Timeout in seconds
//
//        try {
//            if (locator instanceof By) {
//                element = wait.until(ExpectedConditions.visibilityOfElementLocated((By) locator));  // Wait until visible
//            } else if (locator instanceof FlutterBy.FlutterLocator) {
//                element = wait.until(driver -> driver.findElement((FlutterBy.FlutterLocator) locator));  // Wait until visible for Flutter
//            } else {
//                throw new RuntimeException("Unsupported locator type: " + locator.getClass().getName());
//            }
//        } catch (Exception e) {
//            LOGGER.info("Element not immediately visible, attempting to scroll: " + fieldName);
//            scrollToElement(fieldName, dynamicValues);  // Scroll to the element
//
//            // Retry finding the element after scrolling
//            try {
//                if (locator instanceof By) {
//                    element = wait.until(ExpectedConditions.visibilityOfElementLocated((By) locator));  // Retry for By locators
//                } else if (locator instanceof FlutterBy.FlutterLocator) {
//                    element = wait.until(driver -> driver.findElement((FlutterBy.FlutterLocator) locator));  // Retry for Flutter locators
//                } else {
//                    throw new RuntimeException("Unsupported locator type after scrolling: " + locator.getClass().getName());
//                }
//            } catch (Exception ex) {
//                throw new RuntimeException("Element not found or not visible after scrolling: " + fieldName, ex);
//            }
//        }
//        // Return appropriate MobileElementWrapper subclass based on the element type
//        if (element != null && element.isDisplayed()) {
//            if (fieldName.endsWith("_Button")) {
//                return new MobileButton(driver, element);
//            } else if (fieldName.endsWith("_Input") || fieldName.endsWith("_Text")) {
//                return new MobileText(driver, element);
//            } else {
//                return new MobileElementWrapper(driver, element);
//            }
//        } else {
//            throw new RuntimeException("Element not found or not displayed: " + fieldName);
//        }
//    }


}
