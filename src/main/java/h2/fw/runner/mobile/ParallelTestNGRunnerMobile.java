package h2.fw.runner.mobile;

import h2.fw.runner.TestNGXmlGenerator;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.util.List;

public class ParallelTestNGRunnerMobile {

    public void run(List<String> mobileOs, String feature, int parallel, String tag, boolean isParallelScenario) {
        TestNGXmlGenerator xmlGenerator = new TestNGXmlGenerator();
        XmlSuite suite = xmlGenerator.generateMultiMobileSuite(mobileOs, feature, parallel, tag, isParallelScenario);
        TestNG testng = new TestNG();
        testng.setXmlSuites(List.of(suite));
        testng.run();
    }
}
