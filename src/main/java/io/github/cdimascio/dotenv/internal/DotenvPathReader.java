package io.github.cdimascio.dotenv.internal;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import io.github.cdimascio.dotenv.DotenvException;

/**
 * (Internal) Reads a .env file
 */
public class DotenvPathReader extends BaseDotenvReader {
    /**
     * Creates a dotenv reader based on the Path and Files
     *
     * @param directory the directory containing the .env file
     * @param filename the file name of the .env file e.g. .env
     */
    public DotenvPathReader(String directory, String filename) {
        super(directory, filename);
    }

    /**
     * (Internal) Reads the .env file
     * @return a list containing the contents of each line in the .env file
     * @throws DotenvException if a dotenv error occurs
     * @throws IOException if an I/O error occurs
     */
    public List<String> read() throws DotenvException, IOException {
        String dir = sanitizeDirectory();

        String location = dir + "/" + filename;
        String lowerLocation = location.toLowerCase();

        Path path = lowerLocation.startsWith("file:") || lowerLocation.startsWith("android.resource:")
            ? Paths.get(URI.create(location))
            : Paths.get(location);

        if (Files.exists(path)) {
            return Files.readAllLines(path);
        }

        try {
            return ClasspathHelper
                .loadFileFromClasspath(location.replaceFirst("^\\./", "/"))
                .collect(Collectors.toList());
        } catch (DotenvException e) {
            Path cwd = FileSystems.getDefault().getPath(".").toAbsolutePath().normalize();
            String cwdMessage = !path.isAbsolute() ? "(working directory: " + cwd + ")" : "";
            e.addSuppressed(new DotenvException("Could not find " + path + " on the file system " + cwdMessage));
            throw e;
        }
    }
}
