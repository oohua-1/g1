package h2.fw.core.mobile;

import java.io.IOException;

public class AppiumDeviceManager {

    private static ThreadLocal<DriverSession> appiumDevice = new ThreadLocal<>();


    public static DriverSession getAppiumDevice() {
        return appiumDevice.get();
    }

    protected static void setDevice(DriverSession device) {
        appiumDevice.set(device);
    }


    public static MobilePlatform getMobilePlatform() {
        return MobilePlatform.valueOf(PluginClI.getInstance().getPlatFormName().toUpperCase());
    }

    public static boolean isPlatform(MobilePlatform expectedPlatform) throws IOException {
        return AppiumDeviceManager.getMobilePlatform().equals(expectedPlatform);
    }
}
