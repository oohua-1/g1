package h2.fw.core.mobile;


import org.testng.*;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class AppiumParallelTestListener extends Helpers
        implements IClassListener, IInvokedMethodListener, ISuiteListener, ITestListener {

    private TestLogger testLogger;
    private AppiumServerManager appiumServerManager;
    private AppiumDriverManager appiumDriverManager;
    private static ThreadLocal<ITestNGMethod> currentMethods = new ThreadLocal<>();
    List<ITestNGListener> iTestNGListeners;

    private static ThreadLocal<String> currentDeviceID = new ThreadLocal<>();

    public AppiumParallelTestListener() {
        testLogger = new TestLogger();
        appiumServerManager = new AppiumServerManager();
        appiumDriverManager = new AppiumDriverManager();
        iTestNGListeners = initialiseListeners();
    }


    /*
     * Handle Skipif annotation
     * SendResults to ATD service if required
     */
    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult testResult) {
        currentMethods.set(iInvokedMethod.getTestMethod());
        SkipIf annotation = iInvokedMethod.getTestMethod().getConstructorOrMethod().getMethod()
                .getAnnotation(SkipIf.class);
        if (annotation != null && AppiumDriverManager.getDriver().getCapabilities()
                .getCapability("platformName")
                .toString().equalsIgnoreCase(annotation.platform())) {
            throw new SkipException("Skipped because property was set to :::"
                    + annotation.platform());
        }
        queueBeforeInvocationListeners(iInvokedMethod, testResult, iTestNGListeners);
//        if (!iInvokedMethod.isTestMethod() && AppiumDriverManager.getDriver() == null) {
//            appiumDriverManager.startAppiumDriverInstanceWithUDID(
//                    iInvokedMethod.getTestMethod().getMethodName(), currentDeviceID.get());
//        }

    }

    /*
     * Send results to ATD service if required
     * Stop Appium Driver after method invocation completed
     */
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        SessionContext.remove(Thread.currentThread().getId());
        queueAfterInvocationListener(method, testResult, iTestNGListeners);
    }


    /*
     * Starts Appium Server before each test suite
     */
    @Override
    public void onStart(ISuite iSuite) {
    }

    /*
     * Stops Appium Server after each test suite
     */
    @Override
    public void onFinish(ISuite iSuite) {
        try {
            appiumServerManager.destroyAppiumNode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Allocates device for test execution
     */
    @Override
    public void onBeforeClass(ITestClass testClass) {
        try {
            String device = testClass.getXmlClass().getAllParameters().get("device");
            currentDeviceID.set(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Deallocated device post test execution
     */
    @Override
    public void onAfterClass(ITestClass iTestClass) {
        // deviceAllocationManager.freeDevice();
    }

    /*
     * Sets description for each test method with platform and Device UDID allocated to it.
     * Starts Driver instance for execution
     */
    @Override
    public void onTestStart(ITestResult iTestResult) {
        try {
            testLogger.startDeviceLogAndVideoCapture(iTestResult);
            TestExecutionContext testExecutionContext =
                    new TestExecutionContext(iTestResult.getTestName());
            testExecutionContext.addTestState("appiumDriver", AppiumDriverManager.getDriver());
            testExecutionContext.addTestState("deviceId",
                    AppiumDeviceManager.getAppiumDevice().getUdid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onTestSuccess(ITestResult iTestResult) {

    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onTestFailure(ITestResult iTestResult) {

    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onTestSkipped(ITestResult iTestResult) {

    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onStart(ITestContext iTestContext) {

    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onFinish(ITestContext iTestContext) {
        SessionContext.setReportPortalLaunchURL(iTestContext);
        appiumDriverManager.stopAppiumDriver();

    }

    public static ITestNGMethod getTestMethod() {
        return checkNotNull(currentMethods.get(),
                "Did you forget to register the %s listener?",
                AppiumParallelMethodTestListener.class.getName());
    }
}
