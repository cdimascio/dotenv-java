package io.github.cdimascio.dotenv;

import io.github.cdimascio.dotenv.internal.DotenvParser;
import io.github.cdimascio.dotenv.internal.DotenvReader;

import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * Builds and loads and {@link Dotenv} instance.
 * @see Dotenv#configure()
 */
public class DotenvBuilder {
    private String filename = ".env";
    private String directoryPath = "./";
    private boolean systemProperties = false;
    private boolean throwIfMissing = true;
    private boolean throwIfMalformed = true;

    /**
     * Sets the directory containing the .env file.
     * @param path the directory containing the .env file
     * @return this {@link DotenvBuilder}
     */
    public DotenvBuilder directory(String path) {
        this.directoryPath = path;
        return this;
    }
    /**
     * Sets the name of the .env file. The default is .env.
     * @param name the filename
     * @return this {@link DotenvBuilder}
     */
    public DotenvBuilder filename(String name) {
        filename = name;
        return this;
    }

    /**
     * Does not throw an exception when .env is missing.
     * @return this {@link DotenvBuilder}
     */
    public DotenvBuilder ignoreIfMissing() {
        throwIfMissing = false;
        return this;
    }

    /**
     * Does not throw an exception when .env is malformed.
     * @return this {@link DotenvBuilder}
     */
    public DotenvBuilder ignoreIfMalformed() {
        throwIfMalformed = false;
        return this;
    }

    /**
     * Sets each environment variable as system properties.
     * @return this {@link DotenvBuilder}
     */
    public DotenvBuilder systemProperties() {
        systemProperties = true;
        return this;
    }

    /**
     * Load the contents of .env into the virtual environment.
     * @return a new {@link Dotenv} instance
     * @throws DotenvException when an error occurs
     */
    public Dotenv load() throws DotenvException {
        DotenvParser reader = new DotenvParser(
            new DotenvReader(directoryPath, filename),
            throwIfMissing,
            throwIfMalformed);
        List<DotenvEntry> env = reader.parse();
        if (systemProperties) {
            env.forEach(it -> System.setProperty(it.getKey(), it.getValue()));
        }
        return new DotenvImpl(env);
    }

    static class DotenvImpl implements Dotenv {
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
                .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));

            this.setInFile =this.envVarsInFile.entrySet().stream()
                .map(it -> new DotenvEntry(it.getKey(), it.getValue()))
                .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
        }

        @Override
        public Set<DotenvEntry> entries() {
            return set;
        }

        @Override
        public Set<DotenvEntry> entries(Dotenv.Filter filter) {
            if (filter != null) return setInFile;
            return entries();
        }

        @Override
        public String get(String key) {
            String value = System.getenv(key);
            return value != null ? value : envVars.get(key);
        }

        @Override
        public String get(String key, String defaultValue) {
            String value = this.get(key);
            return value != null ? value : defaultValue;
        }
    }
}
