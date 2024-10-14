package h2.fw.core.mobile;

public enum MobilePlatform {

    IOS("IOS"),
    ANDROID("ANDROID"),
    WINDOWS("WINDOWS");

    public final String platformName;
    MobilePlatform(String platformName) {
        this.platformName = platformName;
    }

}
