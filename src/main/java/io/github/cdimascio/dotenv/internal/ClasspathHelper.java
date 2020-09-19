package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvException;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

public class ClasspathHelper {
    static Stream<String> loadFileFromClasspath(String location) {
        Class<ClasspathHelper> loader = ClasspathHelper.class;
        var inputStream = loader.getResourceAsStream(location);
        if (inputStream == null) {
            inputStream = loader.getResourceAsStream(location);
        }
        if (inputStream == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(location);
        }

        if (inputStream == null) {
            throw new DotenvException("Could not find "+location+" on the classpath");
        }
        var scanner = new Scanner(inputStream, "utf-8");
        var lines = new ArrayList<String>();
        while (scanner.hasNext()) {
            lines.add(scanner.nextLine());
        }
        return lines.stream();
    }
}
