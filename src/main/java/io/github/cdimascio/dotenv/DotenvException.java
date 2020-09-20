package io.github.cdimascio.dotenv;

/**
 * A dotenv exception
 */
public class DotenvException extends RuntimeException {
    public DotenvException(String message) {
        super(message);
    }

    public DotenvException(Throwable cause) {
        super(cause);
    }
}
