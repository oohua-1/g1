package h2.fw.exceptions;

public class EnvironmentSetupException extends RuntimeException {
    public EnvironmentSetupException(String message) {
        super(message);
    }

    public EnvironmentSetupException(String message, Exception ex) {
        super(message, ex);
    }
}
