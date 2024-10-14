package h2.fw.runner.web;

import io.cucumber.junit.platform.engine.Cucumber;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.*;

@Test
@CucumberOptions(
        plugin = {
                "pretty", "h2.fw.Report.CucumberPlatformScenarioReporterListener"
        },
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class CucumberNoParallelWithTestNGRunnerTest extends AbstractTestNGCucumberTests {
        private static ThreadLocal<String> threadBrowser = new ThreadLocal<>();

        @Override
        @DataProvider(parallel = false)
        public Object[][] scenarios() {
                return super.scenarios();
        }

        @BeforeMethod(alwaysRun = true)
        @Parameters({"browser"})
        public void setup(@Optional("chrome") String browser) {
                if (browser == null || browser.isEmpty()) {
                        browser = "chrome";
                }
                threadBrowser.set(browser);
        }

        public static String getThreadBrowser() {
                return threadBrowser.get();
        }


}