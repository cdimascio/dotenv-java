package io.github.cdimascio.dotenv;

import java.util.Set;


public interface Dotenv {
    enum EntriesFilter {
        /**
         * Filter matching only environment variables declared in the .env file
         */
        DECLARED_IN_ENV_FILE
    }

    /**
     * Configures a new {@link Dotenv} instance
     * @return a new {@link Dotenv} instance
     */
    static DotenvBuilder configure() {
        return new DotenvBuilder();
    }

    /**
     * Creates and loads a {@link Dotenv} instance with default options
     * @return a new {@link Dotenv} instance
     */
    static Dotenv load() {
        return new DotenvBuilder().load();
    }

    /**
     * Returns the set of environment variables with values
     * @return the set of {@link DotenvEntry}s for all environment variables
     */
    Set<DotenvEntry> entries();

    /**
     * Returns the set of  {@link EntriesFilter}s matching the the filter
     * @param filter the filter e.g. {@link EntriesFilter}
     * @return the set of {@link DotenvEntry}s for environment variables matching the {@link EntriesFilter}
     */
    Set<DotenvEntry> entries(EntriesFilter filter);

    /**
     * Retrieves the value of the environment variable specified by key
     * @param key the environment variable
     * @return the value of the environment variable
     */
    String get(String key);

    /**
     * Retrieves the value of the environment variable specified by key.
     * If the environment variable specified by key does not exist, then
     * the defaut value is returned
     * @param key the environment variable
     * @param defaultValue the default value to return
     * @return the value of the environment variable or default value
     */
    String get(String key, String defaultValue);
}
