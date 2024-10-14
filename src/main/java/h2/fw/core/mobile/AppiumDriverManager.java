package h2.fw.core.mobile;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import h2.fw.runner.mobile.CucumberMobileParallelWithTestNGRunnerTest;
import h2.fw.runner.mobile.TestRunnerUtilMobile;
import h2.fw.utils.ConfigReader;
import h2.fw.utils.SystemConfigManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import io.appium.java_client.flutter.ios.FlutterIOSDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.windows.WindowsDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import static h2.fw.core.mobile.ConfigFileManager.CAPS;

public class AppiumDriverManager {
    private static ThreadLocal<AppiumDriver> appiumDriver = new ThreadLocal<>();
    private static final Logger LOGGER = LogManager.getLogger(AppiumDriverManager.class.getName());
    private static final ConfigReader configReader = ConfigReader.getInstance(null);
    public static AppiumDriver getDriver() {
        return appiumDriver.get();
    }
    private static final SystemConfigManager systemConfigManager = SystemConfigManager.getInstance();

    protected static void setDriver(AppiumDriver driver) {
        String allCapabilities = driver.getCapabilities().getCapabilityNames().stream()
                .map(key -> String.format("%n\t%s:: %s", key,
                        driver.getCapabilities().getCapability(key)))
                .collect(Collectors.joining(""));
        LOGGER.info(String.format("AppiumDriverManager: Created AppiumDriver with capabilities: %s",
                allCapabilities));
        appiumDriver.set(driver);
    }

    private AppiumDriver initialiseDriver(DesiredCapabilities desiredCapabilities) throws MalformedURLException, JsonProcessingException {
        String allCapabilities = desiredCapabilities.getCapabilityNames().stream()
                .map(key -> String.format("%n\t%s:: %s", key,
                        desiredCapabilities.getCapability(key)))
                .collect(Collectors.joining(""));

        LOGGER.info(String.format("Initialise Driver with Capabilities: %s",
                allCapabilities));
        String remoteWDHubIP = configReader.getProperty("APPIUM_SERVER");
        return createAppiumDriver(desiredCapabilities, remoteWDHubIP);
    }

    private AppiumDriver createAppiumDriver(DesiredCapabilities desiredCapabilities,
                                            String remoteWDHubIP) throws MalformedURLException, JsonProcessingException {
        AppiumDriver currentDriverSession;
        String mobilePlatform = TestRunnerUtilMobile.getMobileOs();
        switch (mobilePlatform) {
            case "ios" :
                currentDriverSession = new IOSDriver(new URL(remoteWDHubIP),
                        desiredCapabilities);
                break;
            case "flutter-android":
                currentDriverSession = new FlutterAndroidDriver(new URL(remoteWDHubIP),
                        desiredCapabilities);
                break;
            case "flutter-ios":
                currentDriverSession = new FlutterIOSDriver(new URL(remoteWDHubIP),
                        desiredCapabilities);
                break;
            case "android":
                currentDriverSession = new AndroidDriver(new URL(remoteWDHubIP),
                        desiredCapabilities);
                break;
            case "windows":
                currentDriverSession = new WindowsDriver(new URL(remoteWDHubIP),
                        desiredCapabilities);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mobilePlatform);
        }
        setDriver(currentDriverSession);
        Capabilities currentDriverSessionCapabilities = currentDriverSession.getCapabilities();
        LOGGER.info("Session Created for "
                + mobilePlatform
                + "\n\tSession Id: " + currentDriverSession.getSessionId()
                + "\n\tUDID: " + currentDriverSessionCapabilities.getCapability("udid"));
        String json = new Gson().toJson(currentDriverSessionCapabilities.asMap());
        DriverSession driverSessions = (new ObjectMapper().readValue(json, DriverSession.class));
        AppiumDeviceManager.setDevice(driverSessions);
        return currentDriverSession;
    }

    // Should be used by Cucumber as well
    public AppiumDriver startAppiumDriverInstance() throws MalformedURLException, JsonProcessingException {
        return startAppiumDriverInstance(buildDesiredCapabilities(CAPS.get()));
    }

    public AppiumDriver startAppiumDriverInstance(String capabilityFilePath) throws MalformedURLException, JsonProcessingException {
        return startAppiumDriverInstance(buildDesiredCapabilities(capabilityFilePath));
    }

    public AppiumDriver startAppiumDriverInstance(DesiredCapabilities desiredCapabilities) throws MalformedURLException, JsonProcessingException {
//        LOGGER.info(String.format("startAppiumDriverInstance for %s using capability file: %s",
//                testMethodName, CAPS.get()));
        LOGGER.info("startAppiumDriverInstance");
        AppiumDriver currentDriverSession =
                initialiseDriver(desiredCapabilities);
        AppiumDriverManager.setDriver(currentDriverSession);
        return currentDriverSession;
    }

    public void startAppiumDriverInstanceWithUDID(String deviceUDID) throws MalformedURLException, JsonProcessingException {
//        LOGGER.info(String.format("startAppiumDriverInstance for %s using capability file: %s",
//                testMethodName, CAPS.get()));
        LOGGER.info("startAppiumDriverInstance");
        DesiredCapabilities desiredCapabilities = buildDesiredCapabilities(CAPS.get());
        desiredCapabilities.setCapability("appium:udids", deviceUDID);
        AppiumDriver currentDriverSession =
                initialiseDriver(desiredCapabilities);
        AppiumDriverManager.setDriver(currentDriverSession);
    }

    private DesiredCapabilities buildDesiredCapabilities(String capabilityFilePath) {
        if (new File(capabilityFilePath).exists()) {
            return new DesiredCapabilityBuilder()
                    .buildDesiredCapability(capabilityFilePath);
        } else {
            throw new RuntimeException("Capability file not found");
        }
    }

    public void stopAppiumDriver() {
        if (AppiumDriverManager.getDriver() != null
                && AppiumDriverManager.getDriver().getSessionId() != null) {
            LOGGER.info("Session Deleting ---- "
                    + AppiumDriverManager.getDriver().getSessionId() + "---"
                    + AppiumDriverManager.getDriver().getCapabilities().getCapability("udid"));
            AppiumDriverManager.getDriver().quit();
        }
    }
}