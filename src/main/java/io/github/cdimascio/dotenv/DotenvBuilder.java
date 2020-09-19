package io.github.cdimascio.dotenv;

import io.github.cdimascio.dotenv.internal.DotenvParser;
import io.github.cdimascio.dotenv.internal.DotenvReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

public class DotenvBuilder {
    private String filename = ".env";
    private String directoryPath = "./";
    private boolean systemProperties = false;
    private boolean throwIfMissing = true;
    private boolean throwIfMalformed = true;

    /**
     * Sets the directory containing the .env file
     * @param path The path
     */
    public DotenvBuilder directory(String path) {
        this.directoryPath = path;
        return this;
    }
    /**
     * Sets the name of the .env file. The default is .env
     * @param name The filename
     */
    public DotenvBuilder filename(String name) {
        filename = name;
        return this;
    }

    /**
     * Do not throw an exception when .env is missing
     */
    public DotenvBuilder ignoreIfMissing() {
        throwIfMissing = false;
        return this;
    }

    /**
     * Do not throw an exception when .env is malformed
     */
    public DotenvBuilder ignoreIfMalformed() {
        throwIfMalformed = false;
        return this;
    }

    /**
     * Adds environment variables into system properties
     */
    public DotenvBuilder systemProperties() {
        systemProperties = true;
        return this;
    }

    /**
     * Load the contents of .env into the virtual environment
     */
    public DotenvImpl load() throws DotenvException {
        DotenvParser reader = new DotenvParser(
            new DotenvReader(directoryPath, filename),
            throwIfMissing,
            throwIfMalformed);
        var env = reader.parse();
        if (systemProperties) {
            env.forEach(it -> System.setProperty(it.getKey(), it.getValue()));
        }
        return new DotenvImpl(env);
    }

    public static class DotenvImpl implements Dotenv {
        private final Map<String, String> envVars;
        private final Set<DotenvEntry> set;
        private final Set<DotenvEntry> setInFile;
        private final Map<String, String> envVarsInFile;
        public DotenvImpl(List<DotenvEntry> envVars) {
            this.envVarsInFile = envVars.stream().collect(toMap(DotenvEntry::getKey, DotenvEntry::getValue));
            this.envVars = new HashMap<>(this.envVarsInFile);
            System.getenv().forEach(this.envVars::put);

            this.set =this.envVars.entrySet().stream()
                .map(it -> new DotenvEntry(it.getKey(), it.getValue()))
                .collect(toUnmodifiableSet());

            this.setInFile =this.envVarsInFile.entrySet().stream()
                .map(it -> new DotenvEntry(it.getKey(), it.getValue()))
                .collect(toUnmodifiableSet());
        }

        @Override
        public Set<DotenvEntry> entries() {
            return set;
        }

        @Override
        public Set<DotenvEntry> entries(EntriesFilter filter) {
            if (filter != null) return setInFile;
            return entries();
        }

        @Override
        public String get(String key) {
            var value = System.getenv(key);
            return value != null ? value : envVars.get(key);
        }

        @Override
        public String get(String key, String defaultValue) {
            var value = this.get(key);
            return value != null ? value : defaultValue;
        }
    }
}
