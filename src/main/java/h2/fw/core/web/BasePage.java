package h2.fw.core.web;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.HashMap;
import java.util.Map;

public abstract class BasePage {
    protected Page page;
    protected Map<String, String> locatorTemplates = new HashMap<>(); // Store locator templates (for dynamic locators)
    protected Map<String, Locator> staticLocators = new HashMap<>(); // Store resolved static locators
    protected Map<String, Map<String, String>> childLocators = new HashMap<>(); // Child locators relative to their parents
    protected Map<String, String> childToParentMap = new HashMap<>(); // Map to link child fields to their parent locators

    public BasePage(Page page) {
        this.page = page;
    }

    // Abstract method to be implemented by each specific page class to verify if the page is loaded
    public abstract boolean isLoaded();

    // Method to add a static locator
    public void addStaticLocator(String fieldName, String locatorString) {
        staticLocators.put(fieldName, page.locator(locatorString));
    }

    // Method to add a dynamic locator template
    public void addDynamicLocatorTemplate(String fieldName, String locatorTemplate) {
        locatorTemplates.put(fieldName, locatorTemplate);
    }

    // Method to get a Locator object, resolving dynamic locators if necessary
    public Locator getLocator(String fieldName, String... dynamicValues) {
        if (staticLocators.containsKey(fieldName)) {
            return staticLocators.get(fieldName);
        } else if (locatorTemplates.containsKey(fieldName)) {
            String locatorString = String.format(locatorTemplates.get(fieldName), (Object[]) dynamicValues);
            return page.locator(locatorString);
        } else {
            throw new IllegalArgumentException("No locator found for field: " + fieldName);
        }
    }

    // Method to add child locators relative to a parent locator
    public void addChildLocator(String parentFieldName, String childFieldName, String relativeXpath) {
        childLocators.computeIfAbsent(parentFieldName, k -> new HashMap<>()).put(childFieldName, relativeXpath);
        childToParentMap.put(childFieldName, parentFieldName);
    }

    // Method to get a child locator, resolving the full path dynamically
    public Locator getChildLocator(String childFieldName, String... dynamicValues) {
        String parentFieldName = childToParentMap.get(childFieldName);
        if (parentFieldName == null) {
            throw new IllegalArgumentException("No parent found for child locator: " + childFieldName);
        }

        String parentLocatorString = staticLocators.containsKey(parentFieldName)
                ? staticLocators.get(parentFieldName).toString().replace("Locator@", "")
                : locatorTemplates.get(parentFieldName);

        String relativeLocatorTemplate = childLocators.getOrDefault(parentFieldName, new HashMap<>()).get(childFieldName);
        if (relativeLocatorTemplate == null) {
            throw new IllegalArgumentException("No child locator found for field: " + childFieldName);
        }

        String relativeLocator = String.format(relativeLocatorTemplate, (Object[]) dynamicValues);
        String fullLocator = parentLocatorString + relativeLocator;

        return page.locator("xpath=" + fullLocator);
    }

    // Method to check if elements exist on the page using locator names
    public boolean checkElementsExistOnThePage(String... locatorNames) {
        for (String name : locatorNames) {
            Locator locator = getLocator(name);
            if (!locator.isVisible()) {
                return false;
            }
        }
        return true;
    }
}
