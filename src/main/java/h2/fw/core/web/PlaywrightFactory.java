package h2.fw.core.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import h2.fw.runner.TestRunnerMain;
import h2.fw.utils.ConfigReader;
import h2.fw.utils.SystemConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//
//public class PlaywrightFactory {
//    private static final Logger LOGGER = LogManager.getLogger(PlaywrightFactory.class.getName());
//
//    private static final ConcurrentHashMap<Long, Browser> browserMap = new ConcurrentHashMap<>();
//    private static final ConcurrentHashMap<Long, BrowserContext> browserContextMap = new ConcurrentHashMap<>();
//    private static final ConcurrentHashMap<Long, Page> pageMap = new ConcurrentHashMap<>();
//
//    public PlaywrightFactory() {
//    }
//    ConfigReader configReader = ConfigReader.getInstance(null);
//    public void startBrowser(String browserName) {
//        long threadId = Thread.currentThread().getId();
//        if (!browserMap.containsKey(threadId)) {
//            LOGGER.info("Thread ID: " + threadId + " - Starting browser: " + browserName);
//            boolean headless = Boolean.parseBoolean(configReader.getProperty("HEADLESS", "false"));
//
//            try {
//                Browser browser;
//                Playwright playwright = Playwright.create();
//
//                switch (browserName.toLowerCase()) {
//                    case "chrome" -> {
//                        try {
//                            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
//                        } catch (Exception e) {
//                            LOGGER.error("Failed to launch Chrome browser", e);
//                            throw new RuntimeException("Failed to launch Chrome browser", e);
//                        }
//                    }
//                    case "chromestable" -> {
//                        try {
//                            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(headless));
//                        } catch (Exception e) {
//                            LOGGER.error("Failed to launch Chrome Stable browser", e);
//                            throw new RuntimeException("Failed to launch Chrome Stable browser", e);
//                        }
//                    }
//                    case "msedge", "edge" -> {
//                        try {
//                            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("msedge").setHeadless(headless));
//                        } catch (Exception e) {
//                            LOGGER.error("Failed to launch Edge browser", e);
//                            throw new RuntimeException("Failed to launch Edge browser", e);
//                        }
//                    }
//                    case "firefox" -> {
//                        try {
//                            browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless));
//                        } catch (Exception e) {
//                            LOGGER.error("Failed to launch Firefox browser", e);
//                            throw new RuntimeException("Failed to launch Firefox browser", e);
//                        }
//                    }
//                    case "safari" -> {
//                        try {
//                            browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless));
//                        } catch (Exception e) {
//                            LOGGER.error("Failed to launch Safari browser", e);
//                            throw new RuntimeException("Failed to launch Safari browser", e);
//                        }
//                    }
//                    default -> throw new IllegalStateException("Unsupported browser: " + browserName);
//                }
//
////                BrowserContext context = browser.newContext(new Browser.NewContextOptions().setViewportSize((int) screenSize.getWidth(), (int) screenSize.getHeight()));
//                BrowserContext context = browser.newContext(new Browser.NewContextOptions()
//                        .setViewportSize(1920, 1080)); // Or use another large size
//                Page page = context.newPage();
//
//                browserMap.put(threadId, browser);
//                browserContextMap.put(threadId, context);
//                pageMap.put(threadId, page);
//
//            } catch (Exception e) {
//                LOGGER.error("Error during browser setup", e);
//                throw new RuntimeException("Failed to start browser: " + browserName, e);
//            }
//        } else {
//            LOGGER.info("Thread ID: " + threadId + " - Browser already started: " + browserName);
//        }
//    }
//
//    // Getters to access the Playwright objects for the current thread
//    public BrowserContext getBrowserContext() {
//        return browserContextMap.get(Thread.currentThread().getId());
//    }
//
//    public Page getPage() {
//        return pageMap.get(Thread.currentThread().getId());
//    }
//
//    // Clean up browser instances for the current thread
//    public void stopBrowser() {
//        long threadId = Thread.currentThread().getId();
//        BrowserContext context = browserContextMap.remove(threadId);
//        Page page = pageMap.remove(threadId);
//        Browser browser = browserMap.remove(threadId);
//
//        if (page != null) {
//            page.close();
//            LOGGER.info("Thread ID: " + threadId + " - Page closed.");
//        }
//        if (context != null) {
//            context.close();
//            LOGGER.info("Thread ID: " + threadId + " - Browser context closed.");
//        }
//        if (browser != null) {
//            browser.close();
//            LOGGER.info("Thread ID: " + threadId + " - Browser context closed.");
//
//        }
//
//    }
//}

import com.microsoft.playwright.*;
import org.yaml.snakeyaml.Yaml;

import java.util.concurrent.ConcurrentHashMap;

public class PlaywrightFactory {
    private static final Logger LOGGER = LogManager.getLogger(PlaywrightFactory.class.getName());
    public static String userName, accessKey;
    private static final ConcurrentHashMap<Long, Playwright> playwrightMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Browser> browserMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, BrowserContext> browserContextMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Page> pageMap = new ConcurrentHashMap<>();
    public static final String USER_DIR = "user.dir";
    ConfigReader configReader = ConfigReader.getInstance(null);

    private String getUserDir() {
        return System.getProperty(USER_DIR);
    }

    public PlaywrightFactory() {
    }
    private static Map<String, Object> convertYamlFileToMap(File yamlFile) {
        try (InputStream inputStream = Files.newInputStream(yamlFile.toPath())) {
            Yaml yaml = new Yaml();
            return yaml.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Malformed browserstack.yml file - %s.", e));
        }
    }
    private String getBrowserStackCapabilities(Map<String, Object> browserStackYamlMap, String browserName) {
        try {
            userName = System.getenv("BROWSERSTACK_USERNAME") != null
                    ? System.getenv("BROWSERSTACK_USERNAME")
                    : (String) browserStackYamlMap.get("userName");

            accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY") != null
                    ? System.getenv("BROWSERSTACK_ACCESS_KEY")
                    : (String) browserStackYamlMap.get("accessKey");

            Map<String, String> capabilities = new HashMap<>();
            capabilities.put("browserstack.user", userName);
            capabilities.put("browserstack.key", accessKey);
            capabilities.put("browserstack.source", "java-playwright-browserstack:sample-sdk:v1.0");
            capabilities.put("browser", browserName);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonCaps = objectMapper.writeValueAsString(capabilities);

            return URLEncoder.encode(jsonCaps, "utf-8");
        } catch (Exception e) {
            LOGGER.error("Failed to prepare BrowserStack capabilities", e);
            throw new RuntimeException("Error in JSON conversion", e);
        }
    }



    public void startBrowser(String browserName) {
        File yamlFile = new File(getUserDir() + "/browserstack.yml");
        Map<String, Object> browserStackYamlMap = convertYamlFileToMap(yamlFile);
        String caps = getBrowserStackCapabilities(browserStackYamlMap, browserName);

        long threadId = Thread.currentThread().getId();
        if (!browserMap.containsKey(threadId)) {
            LOGGER.info("Thread ID: " + threadId + " - Starting browser: " + browserName);
            boolean headless = Boolean.parseBoolean(configReader.getProperty("HEADLESS", "false"));
            String wsEndpoint = "wss://cdp.browserstack.com/playwright?caps=" + caps;
            boolean isCI = SystemConfigManager.getInstance().getCI();
            if (isCI){
                LOGGER.info(caps);
            }
            try {
                Playwright playwright = Playwright.create();
                playwrightMap.put(threadId, playwright);

                Browser browser;
                BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                        .setHeadless(headless)
                        .setArgs(Arrays.asList(
                                "--disable-gpu",
                                "--no-sandbox",
                                "--disable-gl-drawing-for-tests",
                                "--disable-dev-shm-usage",
                                "--disable-extensions",
                                "--disable-infobars",
                                "--disable-popup-blocking",
                                "--disable-features=NetworkService,NetworkServiceInProcess",
                                "--enable-automation",
                                "--mute-audio",
                                "--disable-background-networking",
                                "--disable-default-apps",
                                "--disable-sync"
                        ));
                switch (browserName.toLowerCase()) {
                    case "chrome" -> {
                        if (isCI) {

                            browser = playwright.chromium().connect(wsEndpoint); // Replace with your actual URL
                        } else {
                            browser = playwright.chromium().launch(launchOptions);
                        }
                    }
                    case "chromestable" -> {
                        if (isCI) {
                            browser = playwright.chromium().connect(wsEndpoint); // Replace with your actual URL
                        } else {
                            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(headless));
                        }
                    }
                    case "msedge", "edge" -> {
                        if (isCI) {
                            browser = playwright.chromium().connect(wsEndpoint); // Replace with your actual URL
                        } else {
                            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("msedge").setHeadless(headless));
                        }
                    }
                    case "firefox" -> {
                        if (isCI) {
                            browser = playwright.firefox().connect(wsEndpoint); // Replace with your actual URL
                        } else {
                            browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless));
                        }
                    }
                    case "safari" -> {
                        if (isCI) {
                            browser = playwright.webkit().connect(wsEndpoint); // Replace with your actual URL
                        } else {
                            browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless));
                        }
                    }
                    default -> throw new IllegalStateException("Unsupported browser: " + browserName);
                }

                BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                        .setViewportSize(1920, 1080)); // Set viewport size or use default
                Page page = context.newPage();

                browserMap.put(threadId, browser);
                browserContextMap.put(threadId, context);
                pageMap.put(threadId, page);

            } catch (Exception e) {
                LOGGER.error("Error during browser setup", e);
                throw new RuntimeException("Failed to start browser: " + browserName, e);
            }
        } else {
            LOGGER.info("Thread ID: " + threadId + " - Browser already started: " + browserName);
        }
    }

    // Getters to access the Playwright objects for the current thread
    public BrowserContext getBrowserContext() {
        return browserContextMap.get(Thread.currentThread().getId());
    }

    public Page getPage() {
        return pageMap.get(Thread.currentThread().getId());
    }

    // Clean up browser instances for the current thread
    public void stopBrowser() {
        long threadId = Thread.currentThread().getId();
        BrowserContext context = browserContextMap.remove(threadId);
        Page page = pageMap.remove(threadId);
        Browser browser = browserMap.remove(threadId);
        Playwright playwright = playwrightMap.remove(threadId);

        if (page != null) {
            page.close();
            LOGGER.info("Thread ID: " + threadId + " - Page closed.");
        }
        if (context != null) {
            context.close();
            LOGGER.info("Thread ID: " + threadId + " - Browser context closed.");
        }
        if (browser != null) {
            browser.close();
            LOGGER.info("Thread ID: " + threadId + " - Browser closed.");
        }
        if (playwright != null) {
            playwright.close();
            LOGGER.info("Thread ID: " + threadId + " - Playwright closed.");
        }
    }
}
