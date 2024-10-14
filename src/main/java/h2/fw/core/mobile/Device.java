package h2.fw.core.mobile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {
    private String name;
    private String udid;
    private String state;
    private String sdk;
    private String platform;  // Field to match `getPlatform()`
    private int wdaLocalPort;
    private Meta meta;
    private int mjpegServerPort;
    private boolean busy;
    private boolean realDevice;
    private String deviceType;
    private Object capability;
    private String platformVersion;
    private String deviceName;
    private String host;
    private int totalUtilizationTimeMilliSec;
    private String derivedDataPath;
    private boolean offline;
    private int sessionStartTime;

    // Getters
    public String getName() {
        return name;
    }

    public String getUdid() {
        return udid;
    }

    public String getState() {
        return state;
    }

    public String getSdk() {
        return sdk;
    }

    public String getPlatform() {
        return platform;
    }

    public int getWdaLocalPort() {
        return wdaLocalPort;
    }

    public Meta getMeta() {
        return meta;
    }

    public int getMjpegServerPort() {
        return mjpegServerPort;
    }

    public boolean isBusy() {
        return busy;
    }

    public boolean isRealDevice() {
        return realDevice;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public Object getCapability() {
        return capability;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getHost() {
        return host;
    }

    public int getTotalUtilizationTimeMilliSec() {
        return totalUtilizationTimeMilliSec;
    }

    public String getDerivedDataPath() {
        return derivedDataPath;
    }

    public boolean isOffline() {
        return offline;
    }

    public int getSessionStartTime() {
        return sessionStartTime;
    }

    public static class Meta {
        private int revision;
        private long created;
        private int version;
        private long updated;

        // Getters for Meta
        public int getRevision() {
            return revision;
        }

        public long getCreated() {
            return created;
        }

        public int getVersion() {
            return version;
        }

        public long getUpdated() {
            return updated;
        }
    }
}
