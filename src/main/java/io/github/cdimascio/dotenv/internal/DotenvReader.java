package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvException;

import java.io.IOException;
import java.util.List;

public interface DotenvReader {
    /**
     * Reads the .env file
     * @return a list containing the contents of each line in the .env file
     * @throws DotenvException if a dotenv error occurs
     * @throws IOException if an I/O error occurs
     */
    List<String> read() throws DotenvException, IOException;
}
