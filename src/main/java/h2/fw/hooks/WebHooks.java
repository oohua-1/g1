package h2.fw.hooks;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import h2.fw.core.web.EvaluationManager;
import h2.fw.core.web.PlaywrightFactory;
import h2.fw.core.web.api.RestfulComponent;
import h2.fw.core.web.api.WSComponents;
import h2.fw.core.web.sql.SQLManager;
import h2.fw.runner.*;
import h2.fw.runner.web.CucumberNoParallelWithTestNGRunnerTest;
import h2.fw.runner.web.CucumberParallelWithTestNGRunnerTest;
import h2.fw.runner.web.JunitRunner;
import h2.fw.utils.SystemConfigManager;
import io.cucumber.java.After;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WebHooks {
    private static final Logger LOGGER = LogManager.getLogger(WebHooks.class.getName());

    private final PlaywrightFactory playwrightFactory;
    private BrowserContext browserContext;
    private Page page;
    private final EvaluationManager evaluationManager;  // Injected EvaluationManager instance
    private final RestfulComponent restfulComponent;  // Injected RestfulComponent instance
    private final WSComponents wsComponents;
    private static WebHooks instance;

    public WebHooks(PlaywrightFactory playwrightFactory) {
        this.playwrightFactory = playwrightFactory;
        this.evaluationManager = EvaluationManager.getInstance();  // Use singleton instance
        this.restfulComponent = new RestfulComponent(WSComponents.getInstance());  // Initialize RestfulComponent
        this.wsComponents = WSComponents.getInstance();
        instance = this;

    }



    @After
    public void after() {

        playwrightFactory.stopBrowser();
        SQLManager.getInstance().closeAllConnections();
    }


    private String getBrowser() {
        String runnerType = GlobalContext.getInstance().getRunnerType();
        String browserName = null;

        if ("JUnit".equals(runnerType)) {
            browserName = JunitRunner.getThreadBrowser();
        } else if ("TestNG".equals(runnerType) && SystemConfigManager.getInstance().isParallelScenario()) {
            browserName = CucumberParallelWithTestNGRunnerTest.getThreadBrowser();
        } else if ("TestNG".equals(runnerType) && !SystemConfigManager.getInstance().isParallelScenario()) {
            browserName = CucumberNoParallelWithTestNGRunnerTest.getThreadBrowser();
        }

        if (browserName == null) {
            throw new RuntimeException("No browser found for the current test runner");
        }

        return browserName;
    }


    private void ensureBrowserIsStarted() {
        if (page == null || browserContext == null) {
//            String browserName = JunitRunner.getThreadBrowser();
            String browserName = getBrowser();
            LOGGER.info("Starting browser on demand: " + browserName);
            playwrightFactory.startBrowser(browserName);
            page = playwrightFactory.getPage();
            browserContext = playwrightFactory.getBrowserContext();
        }
    }

    public void resetBrowserState() {
        page = null;
        browserContext = null;
        playwrightFactory.stopBrowser();
    }

    public Page getPage() {
        ensureBrowserIsStarted();  // Lazy initialization of the browser
        return page;
    }

    public BrowserContext getBrowserContext() {
        ensureBrowserIsStarted();  // Lazy initialization of the browser
        return browserContext;
    }
    public static WebHooks getInstance() {
        return instance;
    }

    public EvaluationManager getEvaluationManager() {
        return evaluationManager;
    }

    public RestfulComponent getRestfulComponent() {
        return restfulComponent;
    }

    public WSComponents getWsComponents() {
        return wsComponents;
    }


//    // Static method to get the singleton instance of Hooks
//    public static synchronized WebHooks getInstance(PlaywrightFactory playwrightFactory) {
//        if (instance == null) {
//            instance = new WebHooks(playwrightFactory);
//        }
//        return instance;
//    }

}