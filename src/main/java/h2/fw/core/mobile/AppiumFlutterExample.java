package h2.fw.core.mobile;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.flutter.android.FlutterAndroidDriver;
import io.github.ashwith.flutter.FlutterElement;
import io.github.ashwith.flutter.FlutterFinder;
import org.devicefarm.FlutterBy;
import org.devicefarm.FlutterCommands;
import org.json.JSONArray;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.*;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.remote.http.HttpRequest;
public class AppiumFlutterExample {
    public static void main(String[] args) {
        DesiredCapabilities caps = new DesiredCapabilities();
        AppiumDriver driver = null;

        // Set your desired capabilities with appium prefix
        caps.setCapability("platformName", "ios");
        caps.setCapability("appium:udid", "BBC1746B-0229-4C94-B70C-69A550EE0E3C");
        caps.setCapability("appium:app", "/Volumes/hoangha/flutter/NewFlutter/demo_app/build/ios/iphonesimulator/Runner.app");
        caps.setCapability("appium:automationName", "Flutter");

        try {


            // Initialize the Appium driver
            driver = new AppiumDriver(new URL("http://0.0.0.0:31337/wd/hub"), caps);
            FlutterFinder finder = new FlutterFinder(driver);
            WebElement element = finder.byValueKey("roleDropdown");
            element.getText();
//            finder.byToolTip()
//            Previous month'

        } catch (MalformedURLException e) {
            System.out.println("Invalid URL for Appium server: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    public static Set<String> getAvailableContexts(String sessionId) throws Exception {
        URL url = new URL("http://0.0.0.0:4723/session/" + sessionId + "/contexts");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        JSONArray jsonArray = new JSONArray(content.toString());
        Set<String> contextSet = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            contextSet.add(jsonArray.getString(i));
        }
        return contextSet;
    }

}