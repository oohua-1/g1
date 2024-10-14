package h2.fw.core.web.aspects;

import com.epam.reportportal.service.Launch;

public class LaunchManager {

    // Singleton instance of Launch
    private static Launch launchInstance;

    // Private constructor to prevent instantiation
    private LaunchManager() { }

    // Method to set the Launch instance
    public static void setLaunch(Launch launch) {
        launchInstance = launch;
    }

    // Method to retrieve the current Launch instance
    public static Launch getLaunch() {
        return launchInstance;
    }

    // Method to check if Launch is initialized
    public static boolean isInitialized() {
        return launchInstance != null;
    }
}
