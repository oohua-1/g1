package h2.fw.core.mobile;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PluginClI {
    private String subcommand;
    private String address;
    private String basePath;
    private int port;
    private ArrayList<String> usePlugins;
    private Plugin plugin;
    private ArrayList<Object> extraArgs;
    private boolean allowCors;
    private ArrayList<Object> allowInsecure;
    private int callbackPort;
    private boolean debugLogSpacing;
    private ArrayList<Object> denyInsecure;
    private int keepAliveTimeout;
    private boolean localTimezone;
    private String loglevel;
    private boolean logNoColors;
    private boolean logTimestamp;
    private boolean longStacktrace;
    private boolean noPermsCheck;
    private boolean relaxedSecurityEnabled;
    private boolean sessionOverride;
    private boolean strictCaps;
    private ArrayList<Object> useDrivers;
    private String tmpDir;
    private Meta meta;
    private int $loki;

    // Getters
    public String getSubcommand() {
        return subcommand;
    }

    public String getAddress() {
        return address;
    }

    public String getBasePath() {
        return basePath;
    }

    public int getPort() {
        return port;
    }

    public ArrayList<String> getUsePlugins() {
        return usePlugins;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ArrayList<Object> getExtraArgs() {
        return extraArgs;
    }

    public boolean isAllowCors() {
        return allowCors;
    }

    public ArrayList<Object> getAllowInsecure() {
        return allowInsecure;
    }

    public int getCallbackPort() {
        return callbackPort;
    }

    public boolean isDebugLogSpacing() {
        return debugLogSpacing;
    }

    public ArrayList<Object> getDenyInsecure() {
        return denyInsecure;
    }

    public int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public boolean isLocalTimezone() {
        return localTimezone;
    }

    public String getLoglevel() {
        return loglevel;
    }

    public boolean isLogNoColors() {
        return logNoColors;
    }

    public boolean isLogTimestamp() {
        return logTimestamp;
    }

    public boolean isLongStacktrace() {
        return longStacktrace;
    }

    public boolean isNoPermsCheck() {
        return noPermsCheck;
    }

    public boolean isRelaxedSecurityEnabled() {
        return relaxedSecurityEnabled;
    }

    public boolean isSessionOverride() {
        return sessionOverride;
    }

    public boolean isStrictCaps() {
        return strictCaps;
    }

    public ArrayList<Object> getUseDrivers() {
        return useDrivers;
    }

    public String getTmpDir() {
        return tmpDir;
    }

    public Meta getMeta() {
        return meta;
    }

    public int get$loki() {
        return $loki;
    }

    public String getPlatFormName() {
        return getPlugin().getDeviceFarm().getPlatform();
    }

    public boolean isCloudExecution() {
        return getPlugin().getDeviceFarm().getCloud() != null;
    }

    public String getCloudName() {
        return PluginClI.getInstance().getPlugin().getDeviceFarm()
                .getCloud().get("cloudName").textValue();
    }

    // Inner Meta class
    public static class Meta {
        private int revision;
        private long created;
        private int version;
        private long updated;

        // Getters
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

    // Inner DeviceFarm class
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeviceFarm {
        private String platform;
        private String androidDeviceType;
        private String iosDeviceType;
        private JsonNode cloud;
        private boolean skipChromeDownload;
        private JsonNode derivedDataPath;

        // Getters
        public String getPlatform() {
            return platform;
        }

        public String getAndroidDeviceType() {
            return androidDeviceType;
        }

        public String getIosDeviceType() {
            return iosDeviceType;
        }

        public JsonNode getCloud() {
            return cloud;
        }

        public boolean isSkipChromeDownload() {
            return skipChromeDownload;
        }

        public JsonNode getDerivedDataPath() {
            return derivedDataPath;
        }
    }

    // Inner Plugin class
    public static class Plugin {
        @JsonProperty("device-farm")
        @JsonAlias("deviceFarm")
        private DeviceFarm deviceFarm;

        // Getter for deviceFarm
        public DeviceFarm getDeviceFarm() {
            return deviceFarm;
        }
    }

    // Singleton instance handling without @SneakyThrows
    private static PluginClI instance;

    public static PluginClI getInstance() {
        if (instance == null) {
            try {
                PluginCliRequest plugin = new PluginCliRequest();
                instance = plugin.getCliArgs();
            } catch (Exception e) {
                e.printStackTrace(); // Replace @SneakyThrows with explicit exception handling
            }
        }
        return instance;
    }
}
