package tests;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.github.cdimascio.dotenv.DotenvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DotenvTests {
    private Map<String, String> envVars;

    @BeforeEach
    void setUp() {
        envVars = new HashMap<>();
        envVars.put("MY_TEST_EV1", "my test ev 1");
        envVars.put("MY_TEST_EV2", "my test ev 2");
        envVars.put("WITHOUT_VALUE", "");
    }

    @Test
    void throwIfMalconfigured() {
        assertThrows(DotenvException.class, () -> Dotenv.configure().load());
    }

    @Test
    void load() {
        assertThrows(DotenvException.class, () -> {
            final var dotenv = Dotenv.load();
            envVars.keySet().forEach(envName -> assertEquals(envVars.get(envName), dotenv.get(envName)));
        });
    }

    @Test
    void iteratorOverDotenv() {
        final var dotenv = Dotenv.configure().ignoreIfMalformed().load();

        dotenv
            .entries()
            .forEach(e -> assertEquals(dotenv.get(e.getKey()), e.getValue()));

        for (final var e : dotenv.entries()) {
            assertEquals(dotenv.get(e.getKey()), e.getValue());
        }
    }

    @Test
    void iteratorOverDotenvWithFilter() {
        final var dotenv = Dotenv.configure().ignoreIfMalformed().load();

        final var entriesInFile = dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE);
        final var entriesAll = dotenv.entries();
        assertTrue(entriesInFile.size() < entriesAll.size());

        for (final var e: envVars.entrySet()) {
            assertEquals(dotenv.get(e.getKey()), e.getValue());
        }
    }

    @Test
    void failToRemoveFromDotenv() {
        final var dotenv = Dotenv.configure().ignoreIfMalformed().load();
        assertThrows(UnsupportedOperationException.class, () -> iterateEntries(dotenv));
    }

    private static void iterateEntries(final Dotenv dotenv) {
        final var iter = dotenv.entries().iterator();

        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }

    @Test
    void failToAddToDotenv() {
        final var dotenv = Dotenv.configure().ignoreIfMalformed().load();
        final var entries = dotenv.entries();
        assertThrows(UnsupportedOperationException.class, () -> entries.add(new DotenvEntry("new", "value")));
    }

    @Test
    void configureWithIgnoreMalformed() {
        final var dotenv = Dotenv.configure().ignoreIfMalformed().load();
        for (final var envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }
    }

    @Test
    void configureWithIgnoreMissingAndMalformed() {
        final var dotenv = Dotenv.configure()
            .directory("/missing/dir")
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

        assertNotNull(dotenv.get("PATH"));
    }

        @Test
    void malformedWithUncloseQuote() {
        final var dotenv = Dotenv.configure()
            .directory("/unclosed.quote")
            .ignoreIfMalformed()
            .load();

        assertNull(dotenv.get("FOO"));
        assertNull(dotenv.get("BAZ"), "baz");
    }
}
