package h2.fw.core.web.aspects;
public class ConditionalBlockManager {
    private static ThreadLocal<Boolean> isConditionalBlockActive = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<Boolean> isIfBlockSatisfied = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<Boolean> isElseBlockExecuting = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<Boolean> isIfBlockFailed = ThreadLocal.withInitial(() -> false);

    // Start a conditional block
    public static void startConditionalBlock() {
        isConditionalBlockActive.set(true);
        isIfBlockSatisfied.set(false);
        isElseBlockExecuting.set(false);
        isIfBlockFailed.set(false);
    }

    // End a conditional block
    public static void endConditionalBlock() {
        isConditionalBlockActive.set(false);
        isIfBlockSatisfied.set(false);
        isElseBlockExecuting.set(false);
        isIfBlockFailed.set(false);
    }

    // Check if conditional block is active
    public static boolean isConditionalBlockActive() {
        return isConditionalBlockActive.get();
    }

    // Mark that the 'if' condition was satisfied
    public static void setIfBlockSatisfied(boolean value) {
        isIfBlockSatisfied.set(value);
    }

    // Check if 'if' block was satisfied
    public static boolean isIfBlockSatisfied() {
        return isIfBlockSatisfied.get();
    }

    // Mark if the 'if' block has failed
    public static void setIfBlockFailed(boolean value) {
        isIfBlockFailed.set(value);
    }

    // Check if the 'if' block has failed
    public static boolean isIfBlockFailed() {
        return isIfBlockFailed.get();
    }

    // Set whether we are executing the else block
    public static void setElseBlockExecuting(boolean value) {
        isElseBlockExecuting.set(value);
    }

    // Check if we are in the 'else' block execution
    public static boolean isElseBlockExecuting() {
        return isElseBlockExecuting.get();
    }

    // Clear all thread-local variables (used after a block ends)
    public static void clearState() {
        isConditionalBlockActive.remove();
        isIfBlockSatisfied.remove();
        isElseBlockExecuting.remove();
        isIfBlockFailed.remove();
    }
}
