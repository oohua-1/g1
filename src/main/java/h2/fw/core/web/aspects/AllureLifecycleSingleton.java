package h2.fw.core.web.aspects;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;

public class AllureLifecycleSingleton {
    private static final AllureLifecycle INSTANCE = Allure.getLifecycle();

    private AllureLifecycleSingleton() {}

    public static AllureLifecycle getInstance() {
        return INSTANCE;
    }
}
