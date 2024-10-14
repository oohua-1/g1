package h2.fw.runner.mobile;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

@CucumberOptions(
        plugin = {
                "pretty",
        },
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class CucumberMobileParallelWithTestNGRunnerTest extends AbstractTestNGCucumberTests {
        private static ThreadLocal<String> threadMobileOs = new ThreadLocal<>();

        @BeforeMethod(alwaysRun = true)
        @Parameters({"mobileOs"})
        public void setup(@Optional("android") String mobileOs) {
                if (mobileOs == null || mobileOs.isEmpty()) {
                        mobileOs = "android";  // Set the default browser to "chromium" if no parameter is provided
                }
                threadMobileOs.set(mobileOs);
        }

        public static String getThreadMobileOs() {
                return threadMobileOs.get();
        }


}