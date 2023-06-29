package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * (Internal) Reads a .env file
 */
public class DotenvFileReader extends BaseDotenvReader {
    /**
     * Creates a dotenv reader bases on File api
     * @param directory the directory containing the .env file
     * @param filename the file name of the .env file e.g. .env
     */
    public DotenvFileReader(String directory, String filename) {
        super(directory, filename);
    }

    /**
     * (Internal) Reads the .env file
     * @return a list containing the contents of each line in the .env file
     * @throws DotenvException if a dotenv error occurs
     */
    public List<String> read() throws DotenvException {
        final String dir = sanitizeDirectory();

        final String location = dir + "/" + filename;
        final String lowerLocation = location.toLowerCase();

        try {
            final File file = lowerLocation.startsWith("file:") || lowerLocation.startsWith("android.resource:")
            ? new File(URI.create(location))
            : new File(location);

            if (file.exists()) {
                return FileClasspathHelper.readLines(new FileInputStream(file));
            }
            var classpathLocation = file.exists() ? file.getPath() : location.replaceFirst("^\\./", "/");
            return new ArrayList<>(FileClasspathHelper
                .loadFileFromClasspath(classpathLocation));
        } catch (DotenvException | FileNotFoundException e) {
            e.addSuppressed(new DotenvException("Could not find " + location + " on the file system "));
            throw new DotenvException(e);
        }
    }




}
