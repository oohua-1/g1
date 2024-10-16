package h2.fw.core.mobile;

import static h2.fw.core.mobile.ConfigFileManager.RUNNER;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestNGListener;
import org.testng.ITestResult;
import org.testng.TestNGException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class Helpers {
    private static final Logger LOGGER = LogManager.getLogger(Helpers.class.getName());

    protected String getRemoteAppiumManagerPort(String host) {
        String serverPort = Capabilities.getInstance()
                .getRemoteAppiumManangerPort(host);
        if (serverPort == null) {
            return "4567";
        } else {
            return serverPort;
        }
    }

    protected String getStatus(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                return "Pass";
            case ITestResult.FAILURE:
                return "Fail";
            case ITestResult.SKIP:
                return "Skip";
            default:
                return "Unknown";
        }
    }

    protected void queueBeforeInvocationListeners(IInvokedMethod iInvokedMethod,
                                                  ITestResult iTestResult,
                                                  List<ITestNGListener> listeners) {
        for (ITestNGListener listener : listeners) {
            //Lets filter out only IInvokedMethodListener instances.
            if (listener instanceof IInvokedMethodListener) {
                ((IInvokedMethodListener) listener).beforeInvocation(iInvokedMethod, iTestResult);
            }
        }
    }


    protected void queueAfterInvocationListener(IInvokedMethod iInvokedMethod,
                                                ITestResult iTestResult,
                                                List<ITestNGListener> listeners) {
        for (ITestNGListener listener : listeners) {
            //Lets filter out only IInvokedMethodListener instances.
            if (listener instanceof IInvokedMethodListener) {
                ((IInvokedMethodListener) listener).afterInvocation(iInvokedMethod, iTestResult);
            }
        }
    }

    protected List<ITestNGListener> initialiseListeners() {
        List<ITestNGListener> listeners = new LinkedList<>();
        String file = "META-INF/services/listeners.txt";
        InputStream stream = getStream(file);
        if (stream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    listeners.add(instantiate(line));
                }
            } catch (IOException e) {
                throw new TestNGException(e);
            }
        }
        return listeners;
    }

    private static InputStream getStream(String file) {
        InputStream stream;
        stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (stream == null) {
            LOGGER.info("Custom Listener not found!!");
        }
        return stream;
    }

    private static ITestNGListener instantiate(String className) {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("Please provide a valid class name");
        }
        try {
            Class<?> clazz = Class.forName(className);
            if (!ITestNGListener.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(className
                        + " does not implement a TestNG listener");
            }
            return (ITestNGListener) clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new TestNGException(e);
        }
    }

    public String getHostMachineIpAddress() {
        String localHost = null;
        try {
            localHost = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            LOGGER.error("Unable to get IP address of host", e);
        }
        if (localHost.contains("/")) {
            return localHost.split("/")[1].trim();
        } else {
            return localHost.trim();
        }
    }

    public boolean isRetry(ITestResult iTestResult) {
        if (iTestResult.getMethod().getRetryAnalyzer(iTestResult) != null) {
            return iTestResult.getMethod().getRetryAnalyzer(iTestResult).retry(iTestResult);
        }
        return false;
    }

    public String getCurrentTestClassName() {
        String runner = RUNNER.get();
        if (runner.equalsIgnoreCase("distribute")) {
            return AppiumParallelMethodTestListener.getTestMethod().getRealClass().getSimpleName();
        } else {
            return AppiumParallelTestListener.getTestMethod().getRealClass().getSimpleName();
        }
    }

    public String getCurrentTestMethodName() {
        String runner = RUNNER.get();
        if (runner.equalsIgnoreCase("distribute")) {
            return AppiumParallelMethodTestListener.getTestMethod().getMethodName();
        } else {
            return AppiumParallelTestListener.getTestMethod().getMethodName();
        }
    }

}
