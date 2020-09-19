package io.github.cdimascio.dotenv;

public class DotenvEntry {
    private final String key;
    private final String value;

    public DotenvEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key+"="+value;
    }
}

