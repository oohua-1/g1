package h2.fw.exceptions;

public class CommandLineExecutorException extends RuntimeException {
    public CommandLineExecutorException(String message) {
        super(message);
    }

    public CommandLineExecutorException(String message, Exception ex) {
        super(message, ex);
    }
}
