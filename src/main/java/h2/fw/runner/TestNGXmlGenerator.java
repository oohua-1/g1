package h2.fw.runner;

import h2.fw.core.mobile.Device;
import h2.fw.core.mobile.Devices;
import h2.fw.core.mobile.MobileRunner;
import h2.fw.utils.ConfigReader;
import h2.fw.utils.FeatureFileFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestNGXmlGenerator {
    private static final Logger LOGGER = LogManager.getLogger(TestNGXmlGenerator.class.getName());
    ConfigReader configReader = ConfigReader.getInstance(null);
    private static String gluePathsWeb = "h2.fw.stepdefinitions, h2.fw.hooks";
    private static String gluePathsMobile = "h2.fw.stepdefinitions, h2.fw.mobileHooks";
    public XmlSuite generateSingleBrowserSuite(String browser, String features, int parallel, String tag, boolean isParallelScenario) {

        String runnerClassName = isParallelScenario ? "h2.fw.runner.web.CucumberParallelWithTestNGRunnerTest" : "h2.fw.runner.web.CucumberNoParallelWithTestNGRunnerTest";
        XmlSuite suite = new XmlSuite();
        suite.setName("Single Browser " + browser + features);
        String featurePath = FeatureFileFinder.getInstance().getFeatureFilePath(features);
        // Set DataProvider thread count for parallel execution within the same test
        suite.setDataProviderThreadCount(parallel);

        XmlTest test = new XmlTest(suite);
        test.setName(browser + " Test");
        // Set browser parameter
        test.addParameter("browser", browser);
        // Set feature and glue parameters
        test.addParameter("cucumber.features", featurePath);
        test.addParameter("cucumber.glue", gluePathsWeb);
        if (!tag.isEmpty()) {
            test.addParameter("cucumber.filter.tags", tag);
        }

        // Add the test class
        XmlClass testClass = new XmlClass(runnerClassName);
        test.setXmlClasses(List.of(testClass));
        test.setThreadCount(1); // Ensure no parallelism at the test level

        printXmlSuite(suite);

        return suite;
    }
    public XmlSuite generateSingleMobileSuite(String features, int parallel, String tag, boolean isParallelScenario, String mobileOs) {
        String runnerClassName = isParallelScenario ? "h2.fw.runner.mobile.CucumberMobileParallelWithTestNGRunnerTest" : "h2.fw.runner.mobile.CucumberMobileNoParallelWithTestNGRunnerTest";
        XmlSuite suite = new XmlSuite();
        suite.setName("Single Device " + mobileOs + features);
        String featurePath = FeatureFileFinder.getInstance().getFeatureFilePath(features);

        suite.setDataProviderThreadCount(parallel);
        List<Device> devices = Devices.getConnectedDevices();

        XmlTest test = new XmlTest(suite);
        test.setName(mobileOs + " Test");
        // Set browser parameter
        test.addParameter("mobileOs", mobileOs);
        // Set feature and glue parameters
        test.addParameter("cucumber.features", featurePath);
        test.addParameter("cucumber.glue", gluePathsMobile);
        if (!tag.isEmpty()) {
            test.addParameter("cucumber.filter.tags", tag);
        }

        // Add the test class
        XmlClass testClass = new XmlClass(runnerClassName);
        test.setXmlClasses(List.of(testClass));
        test.setThreadCount(1);

        printXmlSuite(suite);

        return suite;
    }
    private void printXmlSuite(XmlSuite suite) {
        LOGGER.info("XML Suite:\n" + suite.toXml());
    }
    public XmlSuite generateMultiBrowserSuite(List<String> browserList, String features,int parallel, String tag, boolean isParallelScenario) {
        String runnerClassName = isParallelScenario ? "h2.fw.runner.web.CucumberParallelWithTestNGRunnerTest" : "h2.fw.runner.web.CucumberNoParallelWithTestNGRunnerTest";
        XmlSuite suite = new XmlSuite();
        suite.setName("Multi Browser Test Suite " + features);
        String featurePath = FeatureFileFinder.getInstance().getFeatureFilePath(features);

        suite.setDataProviderThreadCount(parallel);
        suite.setParallel(XmlSuite.ParallelMode.TESTS);
        suite.setThreadCount(browserList.size());

        for (String browser : browserList) {
            XmlTest test = new XmlTest(suite);
            test.setName(browser + " Test");

            // Set browser parameter
            test.addParameter("browser", browser);
            // Set feature and glue parameters
            test.addParameter("cucumber.features", featurePath);
            test.addParameter("cucumber.glue", gluePathsWeb);
            if (!tag.isEmpty()) {
                test.addParameter("cucumber.filter.tags", tag);
            }
            // Add the test class
            XmlClass testClass = new XmlClass(runnerClassName);
            test.setXmlClasses(List.of(testClass));

            // Ensure no thread-count is set at the test level
            test.setThreadCount(1); // Set to 1 to avoid parallel execution at the test level
            test.setParallel(XmlSuite.ParallelMode.NONE); // Disable parallelism at the test level
        }
        printXmlSuite(suite);

        return suite;
    }
//    public XmlSuite generateMultiMobileSuite(List<String> mobileOsList, String features, int parallel, String tag, boolean isParallelScenario) {
//        String runnerClassName = isParallelScenario ? "h2.fw.runner.mobile.CucumberMobileParallelWithTestNGRunnerTest" : "h2.fw.runner.mobile.CucumberMobileNoParallelWithTestNGRunnerTest";
//
//        XmlSuite suite = new XmlSuite();
//        suite.setName("Multi Mobile Test Suite " + features);
//        String featurePath = FeatureFileFinder.getInstance().getFeatureFilePath(features);
//
//        suite.setDataProviderThreadCount(parallel);
//        suite.setParallel(XmlSuite.ParallelMode.TESTS);
//        suite.setThreadCount(mobileOsList.size());
//
//        for (String mobile : mobileOsList) {
//            XmlTest test = new XmlTest(suite);
//            test.setName(mobile + " Test");
//
//            // Set browser parameter
//            test.addParameter("mobileOs", mobile);
//            // Set feature and glue parameters
//            test.addParameter("cucumber.features", featurePath);
//            test.addParameter("cucumber.glue", gluePathsMobile);
//            if (!tag.isEmpty()) {
//                test.addParameter("cucumber.filter.tags", tag);
//            }
//            // Add the test class
//            XmlClass testClass = new XmlClass(runnerClassName);
//            test.setXmlClasses(List.of(testClass));
//
//            // Ensure no thread-count is set at the test level
//            test.setThreadCount(1); // Set to 1 to avoid parallel execution at the test level
//            test.setParallel(XmlSuite.ParallelMode.NONE); // Disable parallelism at the test level
//        }
//        printXmlSuite(suite);
//
//        return suite;
//    }
    public XmlSuite generateMultiMobileSuite(List<String> mobileOsList, String features, int parallel, String tag, boolean isParallelScenario) {
        String runnerClassName = isParallelScenario ? "h2.fw.runner.mobile.CucumberMobileParallelWithTestNGRunnerTest" : "h2.fw.runner.mobile.CucumberMobileNoParallelWithTestNGRunnerTest";

        XmlSuite suite = new XmlSuite();
        suite.setName("Multi Mobile Test Suite " + features);
        String featurePath = FeatureFileFinder.getInstance().getFeatureFilePath(features);

        // Get all connected devices
        List<Device> allDevices = Devices.getConnectedDevices();

        // Create a total list of devices to match the thread count
        List<Device> totalFilteredDevices = new ArrayList<>();

        for (String mobileOs : mobileOsList) {
            // Filter devices by platform
            List<Device> filteredDevices = allDevices.stream()
                    .filter(device -> mobileOs.equalsIgnoreCase(device.getPlatform()))
                    .collect(Collectors.toList());

            // Add these filtered devices to the total list
            totalFilteredDevices.addAll(filteredDevices);

            // Loop over each filtered device to create a separate XmlTest
            for (Device device : filteredDevices) {
                XmlTest test = new XmlTest(suite);
                test.setName(mobileOs + " " + device.getUdid() + " Test");

                // Set device-specific parameters
                test.addParameter("mobileOs", mobileOs);
                test.addParameter("deviceName", device.getName());
                test.addParameter("udid", device.getUdid());
                // Set feature and glue parameters
                test.addParameter("cucumber.features", featurePath);
                test.addParameter("cucumber.glue", gluePathsMobile);
                if (!tag.isEmpty()) {
                    test.addParameter("cucumber.filter.tags", tag);
                }

                // Add the test class
                XmlClass testClass = new XmlClass(runnerClassName);
                test.setXmlClasses(List.of(testClass));

                // Ensure each test runs in its own thread
                test.setThreadCount(1); // Set to 1 to ensure only one device per test
                test.setParallel(XmlSuite.ParallelMode.NONE); // Disable parallelism within each test
            }
        }

        // Set the thread count to match the total number of filtered devices across all platforms
        suite.setThreadCount(totalFilteredDevices.size());
        suite.setDataProviderThreadCount(parallel);

        printXmlSuite(suite);

        return suite;
    }

}
