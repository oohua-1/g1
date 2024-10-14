package h2.fw.mobileHooks;

import h2.fw.core.mobile.AppiumDriverManager;
import h2.fw.core.mobile.AppiumServerManager;
import h2.fw.core.mobile.MobileRunner;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class MobileHooks {
    private static final Logger LOGGER = LogManager.getLogger(MobileHooks.class.getName());

    private AppiumDriverManager appiumDriverManager;
    private static MobileHooks instance;
    private AppiumDriver driver;

    public MobileHooks() throws Exception {
        LOGGER.info("STARTING MOBILE HOOKS");
        instance = this;
    }

    @After
    public void after(){
        LOGGER.info("Stopping Appium Driver...");
        appiumDriverManager.stopAppiumDriver();
        LOGGER.info("Appium Driver stopped.");
    }

    public static MobileHooks getInstance() {
        return instance;
    }

    public AppiumDriver getDriver() {
        ensureDriverIsStarted();
        return driver;
    }
    private void ensureDriverIsStarted() {
        if (driver == null || driver.getSessionId() == null) {
            try {
                LOGGER.info("Initializing Appium Driver and Server...");

                if (appiumDriverManager == null) {
                    appiumDriverManager = new AppiumDriverManager();
                }
                driver = appiumDriverManager.startAppiumDriverInstance();

                LOGGER.info("Appium Driver initialized successfully.");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Appium Driver", e);
                throw new RuntimeException("Failed to initialize Appium Driver", e);
            }
        }
    }
}
