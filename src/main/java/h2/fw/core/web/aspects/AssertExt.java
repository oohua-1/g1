package h2.fw.core.web.aspects;

import java.util.LinkedList;
import java.util.List;

public class AssertExt {
    public static boolean softAssertModeDefaultValue = false;
    public static boolean softAssertMode = softAssertModeDefaultValue;
    public static boolean softAssertModePreviousValue = softAssertModeDefaultValue;
    private static int countInterceptedErrors = 0;
    private static LinkedList<List<Throwable>> interceptedErrorsList = new LinkedList<>();
    private static LinkedList<List<Throwable>> realTimeErrorsList = new LinkedList<>();
    private static ThreadLocal<String> testCaseUuid = new ThreadLocal<>();
    private static ThreadLocal<Boolean> isSubstep = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<Boolean> softAssertModeDeactivating = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<Boolean> isAttachmentNeededForParent = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<Boolean> screenshotTaken = ThreadLocal.withInitial(() -> false);
    public static boolean isScreenshotTaken() {
        return screenshotTaken.get();
    }

    public static void setScreenshotTaken(boolean value) {
        screenshotTaken.set(value);
    }

    public static void clearScreenshotTaken() {
        screenshotTaken.remove();
    }
    public static boolean isAttachmentNeededForParent() {
        return isAttachmentNeededForParent.get();
    }

    public static void setAttachmentNeededForParent(boolean needed) {
        isAttachmentNeededForParent.set(needed);
    }

    public static void clearAttachmentNeededForParent() {
        isAttachmentNeededForParent.remove();
    }
    // Method to check if the current step is a substep
    public static boolean isCurrentStepSubstep() {
        return isSubstep.get();
    }

    // Methods to manage substep flag
    public static void setSubstep(boolean value) {
        isSubstep.set(value);
    }

    public static void clearSubstep() {
        isSubstep.remove();
    }

    // Methods to manage soft assert mode
    public static void setSoftAssertMode(boolean newSoftAssertMode) {
        softAssertModePreviousValue = softAssertMode;
        softAssertMode = newSoftAssertMode;
    }

    public static void setPreviousSoftAssertMode() {
        setSoftAssertMode(softAssertModePreviousValue);
    }

    public static boolean getSoftAssertMode() {
        return softAssertMode;
    }

    // Method to activate soft assert mode and clear the deactivation flag
    public static void activateSoftAssertMode() {
        softAssertMode = true;
        softAssertModeDeactivating.set(false);  // Clear the deactivation flag
    }

    // Method to deactivate soft assert mode and set the deactivation flag
    public static void deactivateSoftAssertMode() {
        softAssertMode = false;
        softAssertModeDeactivating.set(true);  // Set the deactivation flag
    }

    // Method to check if soft assert mode is deactivating
    public static boolean isSoftAssertModeDeactivating() {
        return softAssertModeDeactivating.get();
    }

    // Method to clear the deactivation flag
    public static void clearSoftAssertModeDeactivating() {
        softAssertModeDeactivating.remove();
    }

    // Methods to manage errors
    public static int getCountInterceptedErrors() {
        return countInterceptedErrors;
    }

    public static LinkedList<List<Throwable>> getInterceptedErrors() {
        return interceptedErrorsList;
    }

    public static LinkedList<List<Throwable>> getRealTimeErrors() {
        return realTimeErrorsList;
    }

    public static void clearInterceptedErrors() {
        interceptedErrorsList.clear();
        countInterceptedErrors = 0;  // Reset the error count when clearing
    }

    public static void clearRealTimeErrors() {
        realTimeErrorsList.clear();
    }

    public static void addInterceptedError(Throwable error) {
        if (interceptedErrorsList.isEmpty() || interceptedErrorsList.getLast() == null) {
            interceptedErrorsList.add(new LinkedList<>());
        }
        interceptedErrorsList.getLast().add(error);
        countInterceptedErrors++;
    }

    public static void addRealTimeError(Throwable error) {
        if (realTimeErrorsList.isEmpty() || realTimeErrorsList.getLast() == null) {
            realTimeErrorsList.add(new LinkedList<>());
        }
        realTimeErrorsList.getLast().add(error);
    }
}
