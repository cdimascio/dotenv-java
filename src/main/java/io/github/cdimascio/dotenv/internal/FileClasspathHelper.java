package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Classpath helper
 */
public class FileClasspathHelper {
    static List<String> loadFileFromClasspath(String location) {
        InputStream inputStream = ClasspathHelper.getInputStream(location);
        return readLines(inputStream);
    }

    public static List<String> readLines(InputStream inputStream) {
        var list = new LinkedList<String>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                list.add(line);
            }
            return list;
        } catch (Exception e) {
        }
        throw new DotenvException("could not read file content");
    }
}
