package h2.fw.runner;

public class GlobalContext {
    private static final GlobalContext INSTANCE = new GlobalContext();

    private String runnerType;
    private String browser;

    private GlobalContext() {}

    public static GlobalContext getInstance() {
        return INSTANCE;
    }

    public void setRunnerType(String runnerType) {
        this.runnerType = runnerType;
    }

    public String getRunnerType() {
        return runnerType;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getBrowser() {
        return browser;
    }
}
