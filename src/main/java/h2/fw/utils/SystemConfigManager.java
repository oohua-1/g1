package h2.fw.utils;


public class SystemConfigManager {
    private static SystemConfigManager instance;
    private final String feature;
    private final String folder;
    private final String platform;
    private final String browser;
    private final int parallel;
    private final boolean debug;
    private final boolean isParallelScenario;
    private final boolean CI;
    private final String tag;
    private final String mobileOs;

    private SystemConfigManager() {
        this.feature = System.getProperty("FEATURE");
        this.platform = System.getProperty("PLATFORM");
        this.folder = System.getProperty("FOLDER");
        this.browser = System.getProperty("BROWSER");
        this.mobileOs = System.getProperty("MOBILE_OS");
        this.parallel = Integer.parseInt(System.getProperty("PARALLEL"));
        this.debug = Boolean.parseBoolean(System.getProperty("DEBUG"));
        boolean defaultParallelScenario = Boolean.parseBoolean(System.getProperty("PARALLEL_SCENARIO"));
        if (this.platform.contains("web")){
            this.isParallelScenario = (this.parallel > 1) || defaultParallelScenario;
        }else {
            this.isParallelScenario = defaultParallelScenario;
        }
        this.tag = System.getProperty("TAG");
        this.CI = Boolean.parseBoolean(System.getProperty("CI"));
    }

    public static SystemConfigManager getInstance() {
        if (instance == null) {
            instance = new SystemConfigManager();
        }
        return instance;
    }

    public String getFeature() {
        return feature;
    }

    public String getFolder() {
        return folder;
    }
    public String getPlatform() {
        return platform;
    }

    public String getBrowser() {
        return browser;
    }

    public String getTag() {
        return tag;
    }
    public String getMobileOs(){
        return mobileOs;
    }
    public boolean getCI() {
        return CI;
    }

    public int getParallel() {
        return parallel;
    }
    public boolean isParallelScenario() {
        return isParallelScenario;
    }

    public boolean isDebug() {
        return debug;
    }
}
