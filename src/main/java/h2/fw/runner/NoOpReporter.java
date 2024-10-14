package h2.fw.runner;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.util.List;

public class NoOpReporter implements IReporter {

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
    }
}
