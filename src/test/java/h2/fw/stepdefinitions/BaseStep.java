package h2.fw.stepdefinitions;


import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import h2.fw.core.web.BasePage;
import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.HtmlElement.Button;
import h2.fw.core.web.HtmlElement.DropdownList;
import h2.fw.core.web.HtmlElement.TextInput;
import h2.fw.core.web.HtmlElement.TypifiedElement;
import h2.fw.core.web.PageFactory;
import h2.fw.core.web.api.RestfulComponent;
import h2.fw.core.web.api.WSComponents;
import h2.fw.hooks.WebHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class BaseStep {
    private static final Logger LOGGER = LogManager.getLogger(BaseStep.class.getName());

    protected Page playwrightPage;  // The Playwright Page object
    private BasePage activePageInstance;  // The instance of a specific page class like Browser_Page
    private Map<String, Page> pageMap = new HashMap<>();  // Map to associate page names with Page instances
    protected BrowserContext playwrightContext;
    private Page defaultPage;  // Store the default page (e.g., the first loaded page)
    private Map<String, BasePage> basePageMap = new HashMap<>();  // Map to associate page names with BasePage instances
    private final WebHooks hooks;
    public BaseStep(WebHooks hooks) {
        this.hooks = hooks;  // Store the Hooks instance
        this.defaultPage = null; // Initially null, will be set upon first page load

    }

    public WebHooks getHooks() {
        return hooks;
    }
    public void ensureBrowserIsStarted() {
        if (playwrightPage == null || playwrightContext == null) {
            playwrightPage = hooks.getPage();  // This triggers the browser to start if it hasn't already
            playwrightContext = hooks.getBrowserContext();
        }
    }

    public Page getActivePlaywrightPage() {
        return this.playwrightPage;
    }
    public void closeBrowser() {
        hooks.resetBrowserState();  // Reset browser state in hooks
        playwrightPage = null;
        playwrightContext = null;
        defaultPage = null;  // Clear the default page reference
        activePageInstance = null;  // Clear the active page instance

        // Clear the maps that hold page and base page instances
        pageMap.clear();
        basePageMap.clear();
    }


    public EvaluationManager getEvaluationManager() {
        return hooks.getEvaluationManager();
    }

    public RestfulComponent getRestfulComponent() {
        return hooks.getRestfulComponent();
    }

    public WSComponents getWsComponents() {
        return hooks.getWsComponents();
    }

    public WebHooks getWebHooks() {
        return hooks;
    }


    public void setActivePlaywrightPage(Page page) {
        this.playwrightPage = page;
    }


    public void clickToAndWaitForNewPage(String fieldName, String newPageName) {
        TypifiedElement element = getElement(fieldName);

        Page newPage = playwrightContext.waitForPage(() -> {
            element.click();
        });

        if (newPage != null) {
            this.playwrightPage = newPage; // / Set the new tab as the active page
            pageMap.put(newPageName, newPage);
            setActivePageInstance(newPageName);  // Set the active page instance for the new tab
        } else {
            throw new RuntimeException("No new page was opened.");
        }
    }

    public void switchToPage(String pageName) {
        setActivePageInstance(pageName);  // Set the correct BasePage instance based on the page name
    }

    public void switchBackToDefaultPage() {
        if (this.defaultPage != null) {
            this.playwrightPage = this.defaultPage;
            for (Map.Entry<String, Page> entry : pageMap.entrySet()) {
                if (entry.getValue().equals(this.defaultPage)) {
                    setActivePageInstance(entry.getKey());
                    break;
                }
            }
        } else {
            throw new IllegalStateException("No default page has been set.");
        }
    }
    public BasePage getActivePageInstance(){
        return this.activePageInstance;
    }

    public void setActivePageInstance(String pageName) {
        LOGGER.info("Setting active page instance for: " + pageName);

        if (!pageMap.containsKey(pageName)) {
            this.activePageInstance = PageFactory.getInstanceOfCurrentPage(pageName, playwrightPage);

            if (this.defaultPage == null) {
                this.defaultPage = playwrightPage;  // Store the first page as the default
            }

            pageMap.put(pageName, playwrightPage);
            basePageMap.put(pageName, this.activePageInstance);
        } else {
            this.playwrightPage = pageMap.get(pageName);
            LOGGER.info("Using existing page instance for: " + pageName);

            // Retrieve or recreate the BasePage instance
            if (basePageMap.containsKey(pageName)) {
                this.activePageInstance = basePageMap.get(pageName);
            } else {
                this.activePageInstance = PageFactory.getInstanceOfCurrentPage(pageName, playwrightPage);
                basePageMap.put(pageName, this.activePageInstance);
            }
        }

        if (this.activePageInstance == null) {
            LOGGER.error("Failed to set active page instance for: " + pageName);
            throw new IllegalStateException("Unable to set the active page instance. Page not found: " + pageName);
        }
    }

    public boolean isLocatorExistsWithTimeout(Locator locator, int timeout) {
        int interval = 500;  // 500 milliseconds
        int maxAttempts = timeout / interval;

        for (int i = 0; i < maxAttempts; i++) {
            try {
                locator.waitFor(new Locator.WaitForOptions().setTimeout(interval));
                return true; // Locator found
            } catch (TimeoutError e) {
                // If not found, continue to the next attempt
            }
        }

        throw new RuntimeException("Element not found after " + timeout + " ms");
    }


    // Method to get the element's Locator by field name (handles both static and dynamic)
    public Locator getElementLocator(String fieldName, String... dynamicValues) {
        return activePageInstance.getLocator(fieldName, dynamicValues); // Get the locator from the active page
    }

    // Method to get the full XPath for a child element based on its relative XPath
    public Locator getChildLocator(String childFieldName, String... dynamicValues) {
        return activePageInstance.getChildLocator(childFieldName, dynamicValues);
    }

    // Method to get the element with the default timeout
    public TypifiedElement getElement(String fieldName, String... dynamicValues) {
        return getElement(fieldName, 30000, dynamicValues);
    }

    public TypifiedElement getElement(String fieldName, int timeout, String... dynamicValues) {
        Locator locator;

        // Determine if the element is a child element (e.g., a cell)
        if (fieldName.endsWith("Cell")) {
            locator = getChildLocator(fieldName, dynamicValues);
        } else {
            locator = getElementLocator(fieldName, dynamicValues);
        }

        boolean isExist = isLocatorExistsWithTimeout(locator, timeout);

        if (isExist) {
            // If the element exists, determine the type and return the appropriate TypifiedElement
            if (fieldName.endsWith("_Button")) {
                return new Button(playwrightPage, locator);
            } else if (fieldName.endsWith("_Input") || fieldName.endsWith("_Text") || fieldName.contains("Cell")) {
                return new TextInput(playwrightPage, locator);
            } else if (fieldName.endsWith("_Dropdown")) {
                return new DropdownList(playwrightPage, locator);
            } else {
                throw new IllegalArgumentException("Unsupported element type for field: " + fieldName);
            }
        } else {
            throw new RuntimeException("Element not found: " + fieldName);
        }
    }
    public void closePage(String pageName) {
        Page pageToClose = pageMap.get(pageName);

        if (pageToClose != null) {
            // Close the page (tab)
            pageToClose.close();

            pageMap.remove(pageName);
            basePageMap.remove(pageName);

            if (pageToClose == playwrightPage) {
                switchBackToDefaultPage();
            }
        } else {
            throw new IllegalArgumentException("Page not found with name: " + pageName);
        }
    }

}
