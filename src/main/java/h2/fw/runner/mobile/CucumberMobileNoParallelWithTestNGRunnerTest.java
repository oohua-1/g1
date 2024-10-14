package h2.fw.runner.mobile;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

@CucumberOptions(
        plugin = {
                "pretty",
        },
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class CucumberMobileNoParallelWithTestNGRunnerTest extends AbstractTestNGCucumberTests {
        private static ThreadLocal<String> threadMobileOs = new ThreadLocal<>();

        @Override
        @DataProvider(parallel = false)
        public Object[][] scenarios() {
                return super.scenarios();
        }

        @BeforeMethod(alwaysRun = true)
        @Parameters({"mobileOs"})
        public void setup(@Optional("android") String mobileOs) {
                if (mobileOs == null || mobileOs.isEmpty()) {
                        mobileOs = "android";
                }
                threadMobileOs.set(mobileOs);
        }

        public static String getThreadMobileOs() {
                return threadMobileOs.get();
        }


}