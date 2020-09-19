package io.github.cdimascio.dotenv;

import java.util.Set;


public interface Dotenv {
    enum EntriesFilter {
        DECLARED_IN_ENV_FILE
    }

    static DotenvBuilder configure() {
        return new DotenvBuilder();
    }

    static Dotenv load() {
        return new DotenvBuilder().load();
    }

    Set<DotenvEntry> entries();
    Set<DotenvEntry> entries(EntriesFilter filter);
    String get(String key);
    String get(String key, String defaultValue);
}
