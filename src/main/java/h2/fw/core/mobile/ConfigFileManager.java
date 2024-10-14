package h2.fw.core.mobile;

import h2.fw.utils.OverriddenVariable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static h2.fw.utils.OverriddenVariable.getOverriddenStringValue;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.text.MessageFormat.format;

public enum ConfigFileManager {
    CATEGORY("ATDTest"),
    SUITE_NAME("ATDSuiteName"),
    LISTENERS(""),
    INCLUDE_GROUPS(""),
    EXCLUDE_GROUPS(""),
    FRAMEWORK("testng"),
    MAX_RETRY_COUNT("0"),
    CAPS(determineConfigFilePath()),
    RUNNER_LEVEL("methods"),
    RUNNER("distribute");

    private static final Logger LOGGER = LogManager.getLogger(ConfigFileManager.class.getName());


    private final String value;

    ConfigFileManager(String value) {
        this.value = value;
    }

    public String get() {
        // For CAPS, return the directly stored value to avoid environment overrides
        if (this == CAPS) {
            return value;
        }
        // For other keys, allow for overriding via environment variables or system properties
        return OverriddenVariable.getOverriddenStringValue(name(), value);
    }

    public boolean isTrue() {
        return parseBoolean(get());
    }

    public int getInt() {
        return parseInt(get());
    }

    private static String determineConfigFilePath() {
        String caps = System.getProperty("CAPS");
        String platform = System.getProperty("PLATFORM");

        // Construct the full path for the JSON file
        String fullPath = "./configs/" + platform + "/" + caps + "_config.json";
        return fullPath;
    }
}