package h2.fw.runner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import h2.fw.exceptions.EnvironmentSetupException;
import h2.fw.utils.CommandLineExecutor;
import h2.fw.utils.CommandLineResponse;
import h2.fw.utils.SystemConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class LocalDevicesSetup {
    private static final SystemConfigManager config = SystemConfigManager.getInstance();

    private static final Logger LOGGER = LogManager.getLogger(LocalDevicesSetup.class.getName());

    private LocalDevicesSetup() {
        LOGGER.debug("LocalDevicesSetup - private constructor");
    }

    static void setupLocalExecution() {
        int numberOfDevicesForParallelExecution = setupLocalDevices().size();
        if (numberOfDevicesForParallelExecution == 0) {
            throw new EnvironmentSetupException("No devices available to run the tests");
        }
        int providedParallelCount = config.getParallel();
        if (numberOfDevicesForParallelExecution < providedParallelCount) {
            throw new EnvironmentSetupException(String.format(
                    "Fewer devices (%d) available to run the tests in parallel (Expected more " + "than: %d)",
                    numberOfDevicesForParallelExecution, providedParallelCount));
        }
    }

    // Setup and get the list of connected devices
    private static List<String> setupLocalDevices() {
        startADBServerForLocalDevice();
        List<String> deviceList = new ArrayList<>();
        try {
            deviceList = getDevices();
        } catch (IOException e) {
            throw new EnvironmentSetupException("Unable to get devices information", e);
        }
        LOGGER.info("Number of Devices connected: " + deviceList.size());
        return deviceList;
    }

    // Start ADB server
    private static void startADBServerForLocalDevice() {
        LOGGER.info("Start ADB server");
        String[] startAdbCommand = {"adb", "start-server"};
        CommandLineExecutor.execCommand(startAdbCommand);
    }

    // Get the list of connected devices using adb
    private static List<String> getDevices() throws IOException {
        List<String> deviceList = new ArrayList<>();
        String[] adbDevicesCommand = {"adb", "devices"};
        CommandLineResponse response = CommandLineExecutor.execCommand(adbDevicesCommand);
        String[] lines = response.getStdOut().split("\n");

        // Skip the first line ("List of devices attached")
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.isEmpty() && !line.contains("unauthorized")) {
                String deviceId = line.split("\\s+")[0];
                deviceList.add(deviceId);
            }
        }
        return deviceList;
    }

    // Execute an adb command on a specific device
    @NotNull
    private static String getAdbCommandOutputFromLocalDevice(String deviceId, String command, String args) throws IOException {
        String[] adbCommand = {"adb", "-s", deviceId, "shell", command, args};
        CommandLineResponse response = CommandLineExecutor.execCommand(adbCommand);
        String adbCommandOutput = response.getStdOut().replaceAll("\n$", "");
        LOGGER.info("\tadb command: '" + command + "', args: '" + args + "', ");
        LOGGER.info("\tOutput: " + adbCommandOutput);
        return adbCommandOutput;
    }

    private static int getBootedIOSSimulators() {
        String[] xcrunCommand = {"xcrun simctl list devices | grep Booted"};
        return getListOfIOSDevices(xcrunCommand);
    }

    private static int getConnectedIOSDevices() {
//        String[] xcrunCommand = {"ios", "list"};
        String[] xcrunCommand = {"idevice_id", "-l"};
        return getListOfIOSDevices(xcrunCommand);
    }
    private static int getListOfIOSDevices(String[] command) {
        int numberOfDevices = 0;
        CommandLineResponse commandLineResponse = CommandLineExecutor.execCommand(command);
        String commandOutput = commandLineResponse.getStdOut().trim();

        // Handle the output as plain text
        if (!commandOutput.isEmpty()) {
            String[] deviceIds = commandOutput.split("\n");
            numberOfDevices = deviceIds.length;
        }

        LOGGER.info(String.format("Number of connected iOS devices: %d", numberOfDevices));
        return numberOfDevices;
    }

//    private static int getListOfIOSDevices(String[] xcrunCommand, boolean isReal) {
//        int numberOfDevices = 0;
//        CommandLineResponse commandLineResponse = CommandLineExecutor.execCommand(xcrunCommand);
//        String commandOutput = commandLineResponse.getStdOut().trim();
//
//        if (isReal) {
//            JsonObject asJsonObject = JsonParser.parseString(commandOutput).getAsJsonObject();
//            JsonArray deviceList = asJsonObject.get("deviceList").getAsJsonArray();
//            numberOfDevices = deviceList.size();
//            LOGGER.info(String.format("Number of iOS real devices: %d", numberOfDevices));
//            LOGGER.debug(deviceList);
//        } else {
//            if (!commandOutput.isEmpty()) {
//                // Count only non-empty lines in the output
//                String[] lines = commandOutput.split("\n");
//                for (String line : lines) {
//                    if (!line.trim().isEmpty()) {
//                        numberOfDevices++;
//                    }
//                }
//            }
//            LOGGER.info(String.format("Number of iOS simulators: %d", numberOfDevices));
//        }
//        return numberOfDevices;
//    }


    static void setupLocalIOSExecution() {
        int numberOfRealDevicesForParallelExecution = getConnectedIOSDevices();
        int numberOfSimulatorsForParallelExecution = getBootedIOSSimulators();
        if ((numberOfSimulatorsForParallelExecution + numberOfRealDevicesForParallelExecution) == 0) {
            throw new EnvironmentSetupException("No devices available to run the tests");
        }
        int providedParallelCount = config.getParallel();
        if (providedParallelCount > 2 ){
            if (numberOfSimulatorsForParallelExecution + numberOfRealDevicesForParallelExecution < providedParallelCount + 1) {
                throw new EnvironmentSetupException(String.format(
                        "Fewer devices (%d) available to run the tests in parallel (Expected more " + "than: %d)",
                        numberOfSimulatorsForParallelExecution, providedParallelCount));
            }
        }

    }
}

