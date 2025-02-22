package tests;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BasicTests {
    private final Map<String, String> envVars = new HashMap<>() {{
        put("MY_TEST_EV1", "my test ev 1");
        put("MY_TEST_EV2", "my test ev 2");
        put("WITHOUT_VALUE", "");
        put("MULTI_LINE", "hello\nworld\n# not a comment\nmulti");
        put("TWO_LINE", "hello\nworld");
        put("TRAILING_COMMENT", "value");
        put("QUOTED_VALUE", "iH4>hb_d0#_GN8d]6");
        put("MY_TEST_EV4", "my test ev 4");
        put("MULTI_LINE_WITH_SHARP", "hello\n#world");
        put("UTF8_STRING", "äöüßé😀");
    }};

    @Test
    void dotenvMalformed() {
        assertThrows(DotenvException.class, () -> Dotenv.configure().directory("./src/test/resources").load());
    }

    @Test
    void dotenvIgnoreMalformed() {
        final var dotenv = Dotenv.configure()
            .directory("./src/test/resources")
            .ignoreIfMalformed()
            .load();

        envVars.forEach((key, expected) -> assertEquals(expected, dotenv.get(key)));
        assertHostEnvVar(dotenv);
    }

    @Test
    void dotenvDuplicateVariable() {
        final var dotenv = Dotenv.configure()
            .directory("./duplicateVariable")
            .load();

        assertEquals("Overridden Again Variable", dotenv.get("MY_TEST_EV1"));
        assertEquals("Variable 2", dotenv.get("MY_TEST_EV2"));
    }

    @Test
    void dotenvFilename() {
        final var dotenv = Dotenv.configure()
            .directory("./src/test/resources")
            .filename("env")
            .ignoreIfMalformed()
            .load();

        envVars.forEach((key, expected) -> assertEquals(expected, dotenv.get(key)));
        assertHostEnvVar(dotenv);
    }

    @Test
    void resourceRelative() {
        final var dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMalformed()
            .load();

        assertEquals("my test ev 1", dotenv.get("MY_TEST_EV1"));
        assertHostEnvVar(dotenv);
    }

    @Test
    void resourceAbsoluteDir() {
        assertDirectory("/envdir","Simple Subdirectory");
    }

    @Test
    void resourceRelativeDir() {
        assertDirectory("./envdir", "Simple Subdirectory");
    }

    @Test
    void resourceUnanchoredDir() {
        assertDirectory("envdir", "Simple Subdirectory");
    }

    @Test
    void resourceAbsoluteTrailingDotDir() {
        assertDirectory("/trailingdot./envdir", "Trailing Dot Directory With Subdirectory");
    }

    @Test
    void resourceRelativeTrailingDotDir() {
        assertDirectory("./trailingdot./envdir", "Trailing Dot Directory With Subdirectory");
    }

    @Test
    void resourceUnanchoredTrailingDotDir() {
        assertDirectory("trailingdot./envdir", "Trailing Dot Directory With Subdirectory");
    }

    @Test
    void resourceCurrent() {
        final var dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        assertEquals("my test ev 1", dotenv.get("MY_TEST_EV1"));
        assertHostEnvVar(dotenv);
    }

    @Test
    void systemProperties() {
        final var dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .systemProperties()
            .load();

        assertHostEnvVar(dotenv);
        assertEquals("my test ev 1", dotenv.get("MY_TEST_EV1"));
        assertEquals("my test ev 1", System.getProperty("MY_TEST_EV1"));
        dotenv.entries().forEach(entry -> System.clearProperty(entry.getKey()));
    }

    @Test
    void noSystemProperties() {
        final var dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        assertHostEnvVar(dotenv);
        assertEquals("my test ev 1", dotenv.get("MY_TEST_EV1"));
        assertNull(System.getProperty("MY_TEST_EV1"));
    }

    @Test
    void iterateOverDotenv() {
        final var dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        for (final var e : dotenv.entries()) {
            assertEquals(dotenv.get(e.getKey()), e.getValue());
        }
    }
    
    @Test
    void iterateOverEnvVar() {
        final var dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        envVars.forEach((key, expected) -> assertEquals(expected, dotenv.get(key)));
    }

    @Test
    void dotenvMissing() {
         assertThrows(DotenvException.class, () -> Dotenv.configure().directory("/missing/.env").load());
    }

    @Test
    void dotenvIgnoreMissing() {
        final var dotenv = Dotenv.configure()
            .directory("/missing/.env")
            .ignoreIfMissing()
            .load();

        assertHostEnvVar(dotenv);
        assertNull(dotenv.get("MY_TEST_EV1"));
    }

    private void assertDirectory(final String directory, final String expected) {
        final var dotenv = Dotenv.configure()
            .directory(directory)
            .load();

        assertEquals(expected, dotenv.get("MY_TEST_EV1"));
    }

    private void assertHostEnvVar(final Dotenv env) {
        final var isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (isWindows)
            assertNotNull(env.get("PATH"));
        else {
            final var expectedHome = System.getProperty("user.home");
            final var actualHome = env.get("HOME");
            assertEquals(expectedHome, actualHome);
        }
    }
}
