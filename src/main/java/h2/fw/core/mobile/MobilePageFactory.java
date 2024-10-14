package h2.fw.core.mobile;
import io.appium.java_client.AppiumDriver;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import io.appium.java_client.AppiumDriver;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MobilePageFactory {
    private static final Map<String, Class<? extends MobileBasePage>> pageMap = new HashMap<>();
    private static MobileBasePage activePage;

    static {
        // Scan the package containing the mobile page classes
        Reflections reflections = new Reflections("h2.fw.core.mobile.pages");

        // Register all non-abstract classes that extend MobileBasePage
        for (Class<? extends MobileBasePage> pageClass : reflections.getSubTypesOf(MobileBasePage.class)) {
            if (!Modifier.isAbstract(pageClass.getModifiers())) {  // Exclude abstract classes
                String pageName = pageClass.getSimpleName();  // Use class name as the key
                pageMap.put(pageName, pageClass);
            }
        }
    }

    public static MobileBasePage getInstanceOfCurrentPage(String pageName, AppiumDriver driver, String platform) {
        Class<? extends MobileBasePage> pageClass = pageMap.get(pageName);
        if (pageClass != null) {
            try {
                // Instantiate the page using reflection
                activePage = pageClass.getDeclaredConstructor(AppiumDriver.class, String.class).newInstance(driver, platform);
                return activePage;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create instance of " + pageName, e);
            }
        }
        throw new IllegalArgumentException("No page found with name: " + pageName);
    }
}
