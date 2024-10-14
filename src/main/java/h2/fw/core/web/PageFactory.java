package h2.fw.core.web;

import com.microsoft.playwright.Page;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class PageFactory {
    private static final Map<String, Class<? extends BasePage>> pageMap = new HashMap<>();
    private static BasePage activePage;

    static {
        Reflections reflections = new Reflections("h2.fw.core.web.pages");

        for (Class<? extends BasePage> pageClass : reflections.getSubTypesOf(BasePage.class)) {
            if (!Modifier.isAbstract(pageClass.getModifiers())) {  // Exclude abstract classes
                String pageName = pageClass.getSimpleName();  // Use class name as the key
                pageMap.put(pageName, pageClass);
            }
        }
    }

    public static BasePage getInstanceOfCurrentPage(String pageName, Page playwrightPage) {
        Class<? extends BasePage> pageClass = pageMap.get(pageName);
        if (pageClass != null) {
            try {
                activePage = pageClass.getDeclaredConstructor(Page.class).newInstance(playwrightPage);
                return activePage;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create instance of " + pageName, e);
            }
        }
        throw new IllegalArgumentException("No page found with name: " + pageName);
    }


}
