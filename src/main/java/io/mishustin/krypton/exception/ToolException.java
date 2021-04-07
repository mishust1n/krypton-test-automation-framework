package io.mishustin.krypton.exception;

public class ToolException extends RuntimeException {

    public ToolException(String message) {
        super(message);
    }

    public ToolException(Throwable cause) {
        super(cause);
    }
}
