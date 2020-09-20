package io.github.cdimascio.dotenv;

/**
 * A key value pair representing an environment variable and its value
 */
public class DotenvEntry {
    /**
     * A dotenv entry filter
     */
    public enum Filter {
        /**
         * Filter matching only environment variables declared in the .env file
         */
        DECLARED_IN_ENV_FILE
    }

    private final String key;
    private final String value;

    public DotenvEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key for the {@link DotenvEntry}
     * @return the key for the {@link DotenvEntry}
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the value for the {@link DotenvEntry}
     * @return the value for the {@link DotenvEntry}
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key+"="+value;
    }
}

