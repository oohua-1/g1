package h2.fw.core.mobile;

import h2.fw.runner.mobile.CucumberMobileParallelWithTestNGRunnerTest;
import h2.fw.runner.mobile.TestRunnerUtilMobile;
import h2.fw.utils.SystemConfigManager;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static h2.fw.core.mobile.ConfigFileManager.CAPS;

public class DesiredCapabilityBuilder {
    private static final SystemConfigManager systemConfig = SystemConfigManager.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(DesiredCapabilityBuilder.class.getName());
    private String projectBaseDir;

    public DesiredCapabilities buildDesiredCapability(String capabilityFilePath) {
        String platform = TestRunnerUtilMobile.getMobileOs();
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        JSONObject platFormCapabilities;
        JSONObject fullCapabilities;

        if (CAPS.get().equalsIgnoreCase(capabilityFilePath)) {
            LOGGER.info("Capabilities file is not specified. Using default capabilities file");
            fullCapabilities = Capabilities.getInstance().getCapabilities();
        } else {
            LOGGER.info("Capabilities file is specified. Using specified capabilities file: " + capabilityFilePath);
            fullCapabilities = Capabilities.getInstance().createInstance(capabilityFilePath);
        }

        // Store the base directory of the config file
        projectBaseDir = findProjectBaseDir(Paths.get(capabilityFilePath));

        platFormCapabilities = fullCapabilities.getJSONObject(platform);
        JSONObject finalPlatformCapabilities = platFormCapabilities;

        // Add the "appium:" prefix to all keys except for certain standard ones and those prefixed with "df:"
        platFormCapabilities.keySet().forEach(key -> {
            String prefixedKey = key;

            if (!key.equalsIgnoreCase("platformName") && !key.equalsIgnoreCase("browserName") &&
                    !key.equalsIgnoreCase("version") && !key.equalsIgnoreCase("acceptInsecureCerts") &&
                    !key.startsWith("df:")) {
                prefixedKey = "appium:" + key;
            }

            desiredCapabilities.setCapability(prefixedKey, finalPlatformCapabilities.get(key));
        });

        desiredCapabilities.setCapability("appium:app", getAppPathInCapabilities(platform, fullCapabilities));

        return desiredCapabilities;
    }


    private String findProjectBaseDir(Path startingPath) {
        Path currentPath = startingPath.getParent();
        while (currentPath != null) {
            if (new File(currentPath.toFile(), ".git").exists() ||
                    new File(currentPath.toFile(), "build.gradle.kts").exists()) {
                return currentPath.toString();
            }
            currentPath = currentPath.getParent();
        }
        return startingPath.getParent().toString(); // Fallback to the parent directory if marker not found
    }

    private String getAppPathInCapabilities(String platform, JSONObject fullCapabilities) {
        String appPath = null;
        if (fullCapabilities.getJSONObject(platform).has("app")) {
            Object app = fullCapabilities.getJSONObject(platform).get("app");
            if ((PluginClI.getInstance().getPlugin().getDeviceFarm().getCloud() == null)
                    && !(new UrlValidator()).isValid(app.toString())) {
                Path path = Paths.get(app.toString());
                if (!path.isAbsolute()) {
                    path = Paths.get(projectBaseDir, app.toString());
                }
                appPath = path.normalize().toAbsolutePath().toString();
            } else {
                appPath = app.toString();
            }
        }
        return appPath;
    }
}
