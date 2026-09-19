package tests;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuotedValuesTests {
    @TempDir
    Path directory;

    private Dotenv load(String contents) throws IOException {
        Files.writeString(directory.resolve("test.env"), contents);
        return Dotenv.configure().directory(directory.toString()).filename("test.env").load();
    }

    @Test
    void removesMatchingQuotes() throws IOException {
        final var dotenv = load("PLAIN=value\nSINGLE='value'\nDOUBLE=\"value\"\nEMPTY=''\n");

        assertEquals("value", dotenv.get("PLAIN"));
        assertEquals("value", dotenv.get("SINGLE"));
        assertEquals("value", dotenv.get("DOUBLE"));
        assertEquals("", dotenv.get("EMPTY"));
    }

    @Test
    void preservesSingleQuotedContents() throws IOException {
        final var dotenv = load("SPACES=  '  café  '  \n"
            + "HASH='value#part' # trailing comment\n"
            + "DOUBLE_INSIDE='say \"hello\"'\n"
            + "BACKSLASH='literal\\nvalue'\n");

        assertEquals("  café  ", dotenv.get("SPACES"));
        assertEquals("value#part", dotenv.get("HASH"));
        assertEquals("say \"hello\"", dotenv.get("DOUBLE_INSIDE"));
        assertEquals("literal\\nvalue", dotenv.get("BACKSLASH"));
    }

    @Test
    void preservesUnquotedApostrophes() throws IOException {
        final var dotenv = load("APOSTROPHE=it's unchanged\nTRAILING=parents'\n");

        assertEquals("it's unchanged", dotenv.get("APOSTROPHE"));
        assertEquals("parents'", dotenv.get("TRAILING"));
    }
}
