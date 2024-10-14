package h2.fw.runner.web;

import h2.fw.runner.TestNGXmlGenerator;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.util.List;

public class ParallelTestNGRunner {

    public void run(List<String> browsers, String path, int parallel, String tag, boolean isParallelScenario) {
        TestNGXmlGenerator xmlGenerator = new TestNGXmlGenerator();
        XmlSuite suite = xmlGenerator.generateMultiBrowserSuite(browsers, path, parallel, tag, isParallelScenario);
        TestNG testng = new TestNG();
        testng.setXmlSuites(List.of(suite));
        testng.run();
    }
}
