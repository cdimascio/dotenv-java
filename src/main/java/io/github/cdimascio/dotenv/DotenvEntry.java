package io.github.cdimascio.dotenv;

/**
 * A key value pair representing an environment variable and its value
 */
public class DotenvEntry {

    private final String key;
    private final String value;

    /**
     * Creates a new dotenv entry using the provided key and value
     * @param key the dotenv entry name
     * @param value the dotenv entry value
     */
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

