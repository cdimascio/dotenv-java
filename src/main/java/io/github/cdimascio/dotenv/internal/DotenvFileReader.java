package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvException;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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
        String dir = sanitizeDirectory();

        String location = dir + "/" + filename;
        String lowerLocation = location.toLowerCase();

        File file = lowerLocation.startsWith("file:") || lowerLocation.startsWith("android.resource:")
            ? new File(URI.create(location))
            : new File(location);

        try {
            var classpathLocation = file.exists() ? file.getPath() : location.replaceFirst("^\\./", "/");
            return ClasspathHelper
                .loadFileFromClasspath(classpathLocation)
                .collect(Collectors.toList());
        } catch (DotenvException e) {
            Path cwd = FileSystems.getDefault().getPath(".").toAbsolutePath().normalize();
            String cwdMessage = !file.isAbsolute() ? "(working directory: " + cwd + ")" : "";
            e.addSuppressed(new DotenvException("Could not find " + file.getPath() + " on the file system " + cwdMessage));
            throw e;
        }
    }


}
