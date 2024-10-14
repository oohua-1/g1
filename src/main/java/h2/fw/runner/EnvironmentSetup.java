package h2.fw.runner;

import h2.fw.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvironmentSetup {

    private static final Logger LOGGER = LogManager.getLogger(EnvironmentSetup.class);

    public static void loadConfiguration() {
        String config = System.getProperty("CONFIG");
        String platform = System.getProperty("PLATFORM");

        String configFilePath = "./configs/" + platform + "/" + config + "_config.properties";
        LOGGER.info("Loading configuration from: " + configFilePath);

        ConfigReader configReader = ConfigReader.getInstance(configFilePath);
        configReader.logAllProperties();

    }
}
