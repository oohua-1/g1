package h2.fw.core.mobile;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.github.ashwith.flutter.FlutterElement;
import io.github.ashwith.flutter.FlutterFinder;
import org.devicefarm.FlutterBy;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

//public abstract class MobileBasePage {
//    protected AppiumDriver driver;
//    protected String platform;
//    protected Map<String, Map<String, By>> locators = new HashMap<>(); // Store locators by field name and platform
//    protected Map<String, String> locatorTemplates = new HashMap<>();  // Store dynamic locator templates
//
//    public MobileBasePage(AppiumDriver driver, String platform) {
//        this.driver = driver;
//        this.platform = platform;
//    }
//
//    // Abstract method to be implemented by each specific page class to verify if the page is loaded
//    public abstract boolean isLoaded();
//
//    // Method to add a locator for a specific platform and strategy
//    public void addLocator(String fieldName, String platform, String strategy, String locatorValue) {
//        // Normalize platform to 'flutter' if it starts with 'flutter'
//        if (platform.startsWith("flutter")) {
//            platform = "flutter";
//        }
//        locators.computeIfAbsent(fieldName, k -> new HashMap<>()).put(platform, getBy(strategy, locatorValue));
//    }
//
//    // Method to add a dynamic locator template
//    public void addDynamicLocatorTemplate(String fieldName, String locatorTemplate) {
//        locatorTemplates.put(fieldName, locatorTemplate);
//    }
//
//    // Utility method to convert a strategy and value into a By object
//    private By getBy(String strategy, String locatorValue) {
//        switch (strategy.toLowerCase()) {
//            case "accessibilityid":
//                return AppiumBy.accessibilityId(locatorValue);
//            case "id":
//                return AppiumBy.id(locatorValue);
//            case "xpath":
//                return AppiumBy.xpath(locatorValue);
//            case "classname":
//                return AppiumBy.className(locatorValue);
//            case "iosnspredicate":
//                return AppiumBy.iOSNsPredicateString(locatorValue);
//            case "iosclasschain":
//                return AppiumBy.iOSClassChain(locatorValue);
//            case "androiduiautomator":
//                return AppiumBy.androidUIAutomator(locatorValue);
//            case "flutterkey":
//                return FlutterBy.key(locatorValue);  // Use FlutterBy for Flutter locators
//            case "fluttertext":
//                return FlutterBy.text(locatorValue);  // Use FlutterBy for Flutter locators
//            // Add more strategies as needed
//            default:
//                throw new IllegalArgumentException("Unsupported locator strategy: " + strategy);
//        }
//    }
//    public Object getLocator(String fieldName, String... dynamicValues) {
//        // Normalize platform to 'flutter' if it starts with 'flutter'
//        String normalizedPlatform = platform.startsWith("flutter") ? "flutter" : platform;
//
//        if (locators.containsKey(fieldName) && locators.get(fieldName).containsKey(normalizedPlatform)) {
//            return locators.get(fieldName).get(normalizedPlatform);
//        } else if (locatorTemplates.containsKey(fieldName)) {
//            String dynamicLocator = String.format(locatorTemplates.get(fieldName), (Object[]) dynamicValues);
//
//            if (normalizedPlatform.equals("flutter")) {
//                // Handle dynamic locators for Flutter (if supported)
//                throw new UnsupportedOperationException("Dynamic locators are not supported for Flutter.");
//            } else {
//                // Assuming dynamic locators are based on XPath for non-Flutter platforms
//                return By.xpath(dynamicLocator);
//            }
//        } else {
//            throw new IllegalArgumentException("No locator found for field: " + fieldName + " on platform: " + normalizedPlatform);
//        }
//    }
//
//
//    // Method to check if elements exist on the page using locator names
//// Method to check if elements exist on the page using locator names
//    public boolean checkElementsExistOnThePage(String... locatorNames) {
//        for (String name : locatorNames) {
//            Object locator = getLocator(name);  // Get the locator, which could be By or FlutterLocator
//
//            WebElement element;
//
//            try {
//                if (locator instanceof By) {
//                    element = driver.findElement((By) locator);
//                } else if (locator instanceof FlutterBy.FlutterLocator) {
//                    element = driver.findElement((FlutterBy.FlutterLocator) locator);
//                } else {
//                    throw new RuntimeException("Unsupported locator type: " + locator.getClass().getName());
//                }
//
//                // If the element is not displayed, return false
//                if (!element.isDisplayed()) {
//                    return false;
//                }
//            } catch (NoSuchElementException e) {
//                return false;  // Return false if the element is not found
//            }
//        }
//        return true;  // Return true if all elements are found and displayed
//    }
//}
public abstract class MobileBasePage {
    protected AppiumDriver driver;
    protected String platform;

    // Change the type from By to Object to support multiple types of locators
    protected Map<String, Map<String, Object>> locators = new HashMap<>(); // Store locators by field name and platform
    protected Map<String, String> locatorTemplates = new HashMap<>();  // Store dynamic locator templates

    public MobileBasePage(AppiumDriver driver, String platform) {
        this.driver = driver;
        this.platform = platform;
    }

    // Abstract method to be implemented by each specific page class to verify if the page is loaded
    public abstract boolean isLoaded();

    // Method to add a locator for a specific platform and strategy
    public void addLocator(String fieldName, String platform, String strategy, String locatorValue) {
        // Normalize platform to 'flutter' if it starts with 'flutter'
        if (platform.startsWith("flutter")) {
            platform = "flutter";
        }

        // Retrieve the correct locator (either By or FlutterElement) based on the platform and strategy
        Object locator = getBy(strategy, locatorValue);

        // Store the locator in the map, associating it with the field name and platform
        locators.computeIfAbsent(fieldName, k -> new HashMap<>()).put(platform, locator);
    }

    // Method to add a dynamic locator template
    public void addDynamicLocatorTemplate(String fieldName, String locatorTemplate) {
        locatorTemplates.put(fieldName, locatorTemplate);
    }


    private Object getBy(String strategy, String locatorValue) {
        FlutterFinder flutterFinder = null;

        switch (strategy.toLowerCase()) {
            case "accessibilityid":
                return AppiumBy.accessibilityId(locatorValue);
            case "id":
                return AppiumBy.id(locatorValue);
            case "xpath":
                return AppiumBy.xpath(locatorValue);
            case "classname":
                return AppiumBy.className(locatorValue);
            case "iosnspredicate":
                return AppiumBy.iOSNsPredicateString(locatorValue);
            case "iosclasschain":
                return AppiumBy.iOSClassChain(locatorValue);
            case "androiduiautomator":
                return AppiumBy.androidUIAutomator(locatorValue);
            // For Flutter locators
            case "flutterkey":
                if (flutterFinder == null) flutterFinder = new FlutterFinder(driver);
                return flutterFinder.byValueKey(locatorValue);
            case "fluttertext":
                if (flutterFinder == null) flutterFinder = new FlutterFinder(driver);
                return flutterFinder.byText(locatorValue);
            case "fluttertype":
                if (flutterFinder == null) flutterFinder = new FlutterFinder(driver);
                return flutterFinder.byType(locatorValue);
            case "fluttertooltip":
                if (flutterFinder == null) flutterFinder = new FlutterFinder(driver);
                return flutterFinder.byToolTip(locatorValue);
            case "fluttersemanticslabel":
                if (flutterFinder == null) flutterFinder = new FlutterFinder(driver);
                return flutterFinder.bySemanticsLabel(locatorValue);
            // Add more strategies as needed
            default:
                throw new IllegalArgumentException("Unsupported locator strategy: " + strategy);
        }
    }
    public Object getLocator(String fieldName, String... dynamicValues) {
        // Normalize platform to 'flutter' if it starts with 'flutter'
        String normalizedPlatform = platform.startsWith("flutter") ? "flutter" : platform;

        if (locators.containsKey(fieldName) && locators.get(fieldName).containsKey(normalizedPlatform)) {
            return locators.get(fieldName).get(normalizedPlatform);
        } else if (locatorTemplates.containsKey(fieldName)) {
            String dynamicLocator = String.format(locatorTemplates.get(fieldName), (Object[]) dynamicValues);

            if (normalizedPlatform.equals("flutter")) {
                throw new UnsupportedOperationException("Dynamic locators are not supported for Flutter.");
            } else {
                return By.xpath(dynamicLocator);
            }
        } else {
            throw new IllegalArgumentException("No locator found for field: " + fieldName + " on platform: " + normalizedPlatform);
        }
    }
    // Method to check if elements exist on the page using locator names
    public boolean checkElementsExistOnThePage(String... locatorNames) {
        for (String name : locatorNames) {
            Object locator = getLocator(name);  // Get the locator, which could be By or FlutterElement

            try {
                if (locator instanceof By) {
                    // Handle standard locators
                    WebElement element = driver.findElement((By) locator);
                    if (!element.isDisplayed()) {
                        return false;
                    }
                } else if (locator instanceof FlutterElement) {
                    // Handle Flutter locators
                    FlutterElement flutterElement = (FlutterElement) locator;
                    try {
                        driver.executeScript("flutter:waitFor", flutterElement, 10000);  // Wait for up to 10 seconds
                    } catch (Exception e) {
                        return false;  // Return false if the element is not found or not interactable
                    }
                } else {
                    throw new RuntimeException("Unsupported locator type: " + locator.getClass().getName());
                }
            } catch (NoSuchElementException e) {
                return false;  // Return false if the element is not found
            }
        }
        return true;  // Return true if all elements are found and displayed
    }


}
