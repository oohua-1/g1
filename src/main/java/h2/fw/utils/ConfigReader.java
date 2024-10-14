package h2.fw.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Logger LOGGER = LogManager.getLogger(ConfigReader.class);

    private static ConfigReader instance;
    private Properties properties;

    // Private constructor to prevent instantiation
    private ConfigReader(String filePath) {
        properties = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to load properties file: " + filePath);
        }
    }

    // Public method to provide access to the singleton instance
    public static synchronized ConfigReader getInstance(String filePath) {
        if (instance == null) {
            instance = new ConfigReader(filePath);
        }
        return instance;
    }

    // Method to get a property value by key
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    // Method to get a property value by key with a default value
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    // Method to get a property value by ConfigKey
    public String getProperty(ConfigKey key) {
        return properties.getProperty(key.toString());
    }

    // Method to get a property value by ConfigKey with a default value
    public String getProperty(ConfigKey key, String defaultValue) {
        return properties.getProperty(key.toString(), defaultValue);
    }

    public void logAllProperties() {
        properties.forEach((key, value) -> {
            LOGGER.info("Config Key: " + key + " - Value: " + value);
        });
    }
}
