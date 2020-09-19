package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DotenvReader {
    private final String directory;
    private final String filename;

    public DotenvReader(String directory, String filename) {
        this.directory = directory;
        this.filename = filename;
    }

    public List<String> read() throws DotenvException, IOException {
        var dir = directory
            .replaceAll("\\\\", "/")
            .replaceFirst("\\.env$", "")
            .replaceFirst("/$", "");

        var location = dir + "/" + filename;
        var lowerLocation = location.toLowerCase();
        var path = lowerLocation.startsWith("file:") || lowerLocation.startsWith("android.resource:")
            ? Paths.get(URI.create(location))
            : Paths.get(location);

        if (Files.exists(path)) {
            return Files
                .lines(path)
                .collect(Collectors.toList());
        }

        try {
            return ClasspathHelper
                .loadFileFromClasspath(location.replaceFirst("./", "/"))
                .collect(Collectors.toList());
        } catch (DotenvException e) {
            var cwd = FileSystems.getDefault().getPath(".").toAbsolutePath().normalize();
            var cwdMessage = !path.isAbsolute() ? "(working directory: " + cwd + ")" : "";
            e.addSuppressed(new DotenvException("Could not find " + path + " on the file system " + cwdMessage));
            throw e;
        }
    }
}
