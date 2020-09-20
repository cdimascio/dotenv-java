package io.github.cdimascio.dotenv;

/**
 * Signals that dotenv exception of some sort has occurred.
 */
public class DotenvException extends RuntimeException {
    public DotenvException(String message) {
        super(message);
    }

    public DotenvException(Throwable cause) {
        super(cause);
    }
}
