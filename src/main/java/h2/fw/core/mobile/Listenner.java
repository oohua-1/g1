package h2.fw.core.mobile;

import h2.fw.core.mobile.pages.Welcome_Page;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.util.Base64;

public class Listenner {
    private static final Logger LOGGER = LogManager.getLogger(Listenner.class);
    private final AppiumDriverManager appiumDriverManager;
    private final AppiumServerManager appiumServerManager;

    public Listenner() throws Exception {
        MobileRunner runner = new MobileRunner();
        LOGGER.debug("STARTTTTT");
        this.appiumDriverManager = new AppiumDriverManager();
        this.appiumServerManager = new AppiumServerManager();
    }


    public static void main(String[] args) throws Exception {
        Listenner listenner = new Listenner();
        Thread.sleep(100000000);
//
//        AppiumDriver driver = AppiumDriverManager.getDriver();
//        if (driver == null || driver.getSessionId() == null) {
//            driver = listenner.appiumDriverManager.startAppiumDriverInstance("Test scenario");
//        }
//
//        if (driver != null && driver.getSessionId() != null) {
//            LOGGER.info("Appium Driver is initialized and ready to interact with the app.");
//
//            // Start a new thread to handle screen recording
//            Welcome_Page welcomePage = new Welcome_Page(driver);
//            AppiumDriver finalDriver = driver;
//
//            Thread recordingThread = new Thread(() -> {
//                try {
//                    if (finalDriver instanceof CanRecordScreen) {
//                        ((CanRecordScreen) finalDriver).startRecordingScreen();
//                        Thread.sleep(16000); // Record for a bit longer than the test duration
//                        String video = ((CanRecordScreen) finalDriver).stopRecordingScreen();
//                        LOGGER.info("Screen recording stopped.");
//
//                        byte[] videoData = Base64.getDecoder().decode(video);
//                        java.nio.file.Files.write(java.nio.file.Paths.get("test-recording.mp4"), videoData);
//                        LOGGER.info("Screen recording saved to test-recording.mp4.");
//                    }
//                } catch (Exception e) {
//                    LOGGER.error("Error during screen recording: ", e);
//                }
//            });
//            recordingThread.start();
//            welcomePage.isLoaded();
//            recordingThread.join();
//
//            // If needed, terminate and reactivate the app
////            if (driver instanceof AndroidDriver) {
////                AndroidDriver androidDriver = (AndroidDriver) driver;
//////                androidDriver.terminateApp("com.saucelabs.mydemoapp.android");
//////                androidDriver.activateApp("com.saucelabs.mydemoapp.android");
////            }
//
//            // Quit the driver
//            driver.quit();
//        } else {
//            System.err.println("Appium Driver is not initialized.");
//        }
    }
}
