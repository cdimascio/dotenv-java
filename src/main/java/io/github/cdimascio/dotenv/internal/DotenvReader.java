package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (Internal) Reads a .env file
 */
public class DotenvReader {
    private final String directory;
    private final String filename;
    private final boolean recurse;

    /**
     * Creates a dotenv reader
     * @param directory the directory containing the .env file
     * @param filename the file name of the .env file e.g. .env
     * @param recurse
     */
    public DotenvReader(String directory, String filename, boolean recurse) {
        this.directory = directory;
        this.filename = filename;
        this.recurse = recurse;
    }

    /**
     * (Internal) Reads the .env file
     * @return a list containing the contents of each line in the .env file
     * @throws DotenvException if a dotenv error occurs
     * @throws IOException if an I/O error occurs
     */
    public List<String> read() throws DotenvException, IOException {
        String dir = directory
            .replaceAll("\\\\", "/")
            .replaceFirst("\\.env$", "")
            .replaceFirst("/$", "");

        String location = dir + "/" + filename;
        String lowerLocation = location.toLowerCase();
        Path path = lowerLocation.startsWith("file:") || lowerLocation.startsWith("android.resource:")
            ? Paths.get(URI.create(location))
            : Paths.get(location);

        if (Files.exists(path)) {
            return Files
                .lines(path)
                .collect(Collectors.toList());
        } else if (recurse) {
            Path parent = path.getParent().getParent(); // get the parent of the parent of the current .env file
            while (parent != null) {
                Path fileInParent = parent.resolve(filename);  // resolve a .env file in it
                if (Files.exists(fileInParent)) {
                    return Files
                        .lines(fileInParent)
                        .collect(Collectors.toList());
                } else {
                    parent = parent.getParent();
                }
            }
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
