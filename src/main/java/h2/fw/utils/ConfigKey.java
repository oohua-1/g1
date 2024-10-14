package h2.fw.utils;

public enum ConfigKey {
    FEATURES_FOLDER,
    HEADLESS,
    DEV_ENDPOINT,
    SCREENSHOT_MODE,
    FULLPAGE_SCREENSHOT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
