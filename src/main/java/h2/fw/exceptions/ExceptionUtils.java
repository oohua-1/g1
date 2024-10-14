package h2.fw.exceptions;

import java.io.IOException;
import java.sql.SQLException;

public class ExceptionUtils {

    @FunctionalInterface
    public interface CheckedExceptionRunnable {
        void run() throws Exception;
    }

    public static void handleCheckedException(CheckedExceptionRunnable runnable) {
        try {
            runnable.run();
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred", e);
        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }
}
