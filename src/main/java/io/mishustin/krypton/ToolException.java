package io.mishustin.krypton;

public class ToolException extends RuntimeException {

    public ToolException(String message) {
        super(message);
    }

    public ToolException(Throwable cause) {
        super(cause);
    }
}
