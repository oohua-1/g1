package h2.fw.runner.mobile;

import h2.fw.runner.NoOpReporter;
import h2.fw.runner.TestNGXmlGenerator;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.util.List;

public class SingleTestNGRunnerMobile {

    public void run(String path, int parallel, String tag, boolean isParallelScenario, String mobileOs) {
        TestNGXmlGenerator xmlGenerator = new TestNGXmlGenerator();
        XmlSuite suite = xmlGenerator.generateSingleMobileSuite(path,  parallel, tag, isParallelScenario, mobileOs);

        TestNG testng = new TestNG();
        testng.setXmlSuites(List.of(suite));

        // Disable default TestNG listeners
        testng.setUseDefaultListeners(false);
        // Ensure no other listeners are added
        testng.getTestListeners().clear();
        testng.getSuiteListeners().clear();
        testng.getTestListeners().removeIf(listener -> listener instanceof AllureTestNg);
        testng.addListener(new NoOpReporter());

//        String tempDir = System.getProperty("java.io.tmpdir");
//        testng.setOutputDirectory(tempDir);

        testng.run();
//        FileUtils.deleteDirectory(new File("target/allure-results"));

//        FileUtils.deleteDirectory(new File(tempDir, "test-output"));
//        FileUtils.deleteDirectory(new File(tempDir, "allure-results"));

    }
}
