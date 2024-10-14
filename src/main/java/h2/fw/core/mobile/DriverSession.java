package h2.fw.core.mobile;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class DriverSession {
    @JsonProperty("appium:adbExecTimeout")
    @JsonAlias({"adbExecTimeout"})
    public int adbExecTimeout;
    @JsonProperty("appium:app")
    @JsonAlias({"app"})
    public String app;
    @JsonProperty("appium:appPackage")
    @JsonAlias({"appPackage"})
    public String appPackage;
    @JsonProperty("appium:automationName")
    @JsonAlias({"automationName"})
    public String automationName;
    @JsonProperty("appium:chromeDriverPort")
    @JsonAlias({"chromeDriverPort"})
    public String chromeDriverPort;
    @JsonProperty("appium:deviceApiLevel")
    @JsonAlias({"deviceApiLevel"})
    public int deviceApiLevel;
    @JsonProperty("appium:deviceManufacturer")
    @JsonAlias({"deviceManufacturer"})
    public String deviceManufacturer;
    @JsonProperty("appium:deviceModel")
    @JsonAlias({"deviceModel"})
    public String deviceModel;
    @JsonProperty("appium:deviceName")
    @JsonAlias({"deviceName"})
    public String deviceName;
    @JsonProperty("appium:deviceScreenDensity")
    @JsonAlias({"deviceScreenDensity"})
    public String deviceScreenDensity;
    @JsonProperty("appium:deviceScreenSize")
    @JsonAlias({"deviceScreenSize"})
    public String deviceScreenSize;
    @JsonProperty("appium:deviceUDID")
    @JsonAlias({"deviceUDID"})
    public String deviceUDID;
    @JsonProperty("appium:mjpegServerPort")
    @JsonAlias({"mjpegServerPort"})
    public String mjpegServerPort;
    @JsonProperty("appium:platformVersion")
    @JsonAlias({"platformVersion"})
    public String platformVersion;
    @JsonProperty("appium:systemPort")
    @JsonAlias({"systemPort"})
    public String systemPort;
    @JsonProperty("appium:udid")
    @JsonAlias({"udid"})
    public String udid;
    public String platformName;

    public DriverSession() {
    }

    public int getAdbExecTimeout() {
        return this.adbExecTimeout;
    }

    public String getApp() {
        return this.app;
    }

    public String getAppPackage() {
        return this.appPackage;
    }

    public String getAutomationName() {
        return this.automationName;
    }

    public String getChromeDriverPort() {
        return this.chromeDriverPort;
    }

    public int getDeviceApiLevel() {
        return this.deviceApiLevel;
    }

    public String getDeviceManufacturer() {
        return this.deviceManufacturer;
    }

    public String getDeviceModel() {
        return this.deviceModel;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public String getDeviceScreenDensity() {
        return this.deviceScreenDensity;
    }

    public String getDeviceScreenSize() {
        return this.deviceScreenSize;
    }

    public String getDeviceUDID() {
        return this.deviceUDID;
    }

    public String getMjpegServerPort() {
        return this.mjpegServerPort;
    }

    public String getPlatformVersion() {
        return this.platformVersion;
    }

    public String getSystemPort() {
        return this.systemPort;
    }

    public String getUdid() {
        return this.udid;
    }

    public String getPlatformName() {
        return this.platformName;
    }

    @JsonProperty("appium:adbExecTimeout")
    @JsonAlias({"adbExecTimeout"})
    public void setAdbExecTimeout(int adbExecTimeout) {
        this.adbExecTimeout = adbExecTimeout;
    }

    @JsonProperty("appium:app")
    @JsonAlias({"app"})
    public void setApp(String app) {
        this.app = app;
    }

    @JsonProperty("appium:appPackage")
    @JsonAlias({"appPackage"})
    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    @JsonProperty("appium:automationName")
    @JsonAlias({"automationName"})
    public void setAutomationName(String automationName) {
        this.automationName = automationName;
    }

    @JsonProperty("appium:chromeDriverPort")
    @JsonAlias({"chromeDriverPort"})
    public void setChromeDriverPort(String chromeDriverPort) {
        this.chromeDriverPort = chromeDriverPort;
    }

    @JsonProperty("appium:deviceApiLevel")
    @JsonAlias({"deviceApiLevel"})
    public void setDeviceApiLevel(int deviceApiLevel) {
        this.deviceApiLevel = deviceApiLevel;
    }

    @JsonProperty("appium:deviceManufacturer")
    @JsonAlias({"deviceManufacturer"})
    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    @JsonProperty("appium:deviceModel")
    @JsonAlias({"deviceModel"})
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    @JsonProperty("appium:deviceName")
    @JsonAlias({"deviceName"})
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @JsonProperty("appium:deviceScreenDensity")
    @JsonAlias({"deviceScreenDensity"})
    public void setDeviceScreenDensity(String deviceScreenDensity) {
        this.deviceScreenDensity = deviceScreenDensity;
    }

    @JsonProperty("appium:deviceScreenSize")
    @JsonAlias({"deviceScreenSize"})
    public void setDeviceScreenSize(String deviceScreenSize) {
        this.deviceScreenSize = deviceScreenSize;
    }

    @JsonProperty("appium:deviceUDID")
    @JsonAlias({"deviceUDID"})
    public void setDeviceUDID(String deviceUDID) {
        this.deviceUDID = deviceUDID;
    }

    @JsonProperty("appium:mjpegServerPort")
    @JsonAlias({"mjpegServerPort"})
    public void setMjpegServerPort(String mjpegServerPort) {
        this.mjpegServerPort = mjpegServerPort;
    }

    @JsonProperty("appium:platformVersion")
    @JsonAlias({"platformVersion"})
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    @JsonProperty("appium:systemPort")
    @JsonAlias({"systemPort"})
    public void setSystemPort(String systemPort) {
        this.systemPort = systemPort;
    }

    @JsonProperty("appium:udid")
    @JsonAlias({"udid"})
    public void setUdid(String udid) {
        this.udid = udid;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }
}
