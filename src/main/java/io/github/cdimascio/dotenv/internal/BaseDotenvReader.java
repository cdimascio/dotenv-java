package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvException;

import java.io.IOException;
import java.util.List;

public abstract class BaseDotenvReader implements DotenvReader {
    protected final String directory;
    protected final String filename;

    /**
     * Creates a dotenv reader
     * @param directory the directory containing the .env file
     * @param filename the file name of the .env file e.g. .env
     */
    public BaseDotenvReader(String directory, String filename) {
        this.directory = directory;
        this.filename = filename;
    }

    public abstract List<String> read() throws DotenvException, IOException;



    protected String sanitizeDirectory() {
        String dir = directory
            .replaceAll("\\\\", "/")
            .replaceFirst("\\.env$", "")
            .replaceFirst("/$", "");
        return dir;
    }

}
