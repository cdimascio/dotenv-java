package tests;

import io.github.cdimascio.dotenv.DotenvException;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BasicTests {
    private Map<String, String> envVars = new HashMap<>() {{
        put("MY_TEST_EV1", "my test ev 1");
        put("MY_TEST_EV2", "my test ev 2");
        put("WITHOUT_VALUE", "");
        put("MULTI_LINE", "hello\\nworld");
    }};

    @Test(expected = DotenvException.class)
    public void dotenvMalformed() {
        Dotenv.configure()
            .directory("./src/test/resources")
            .load();
    }

    @Test
    public void dotenvIgnoreMalformed() {
        var dotenv = Dotenv.configure()
            .directory("./src/test/resources")
            .ignoreIfMalformed()
            .load();

        envVars.forEach((key, expected) -> {
            var actual = dotenv.get(key);
            assertEquals(expected, actual);
        });

        assertHostEnvVar(dotenv);
    }

    @Test
    public void dotenvFilename() {
        var dotenv = Dotenv.configure()
            .directory("./src/test/resources")
            .filename("env")
            .ignoreIfMalformed()
            .load();

        envVars.forEach((key, expected) -> {
            var actual = dotenv.get(key);
            assertEquals(expected, actual);
        });

        assertHostEnvVar(dotenv);
    }

    @Test
    public void resourceRelative() {
        var dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMalformed()
            .load();
        assertEquals("my test ev 1", dotenv.get("MY_TEST_EV1"));

        assertHostEnvVar(dotenv);
    }

    @Test
    public void resourceCurrent() {
        var dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();
        assertEquals("my test ev 1", dotenv.get("MY_TEST_EV1"));

        assertHostEnvVar(dotenv);
    }

    @Test
    public void systemProperties() {
        var dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .systemProperties()
            .load();

        assertHostEnvVar(dotenv);
        assertEquals("my test ev 1", dotenv.get("MY_TEST_EV1"));
        assertEquals("my test ev 1", System.getProperty("MY_TEST_EV1"));
        dotenv.entries().forEach(entry -> System.clearProperty(entry.getKey()));
    }

    @Test
    public void noSystemProperties() {
        var dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        assertHostEnvVar(dotenv);
        assertEquals("my test ev 1", dotenv.get("MY_TEST_EV1"));
        assertNull(System.getProperty("MY_TEST_EV1"));
    }

    @Test
    public void iterateOverDotenv() {
        var dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        for (var e : dotenv.entries()) {
            assertEquals(dotenv.get(e.getKey()), e.getValue());
        }
    }

    @Test(expected = DotenvException.class)
    public void dotenvMissing() {
        Dotenv.configure()
            .directory("/missing/.env")
            .load();
    }

    @Test
    public void dotenvIgnoreMissing() {
        var dotenv = Dotenv.configure()
            .directory("/missing/.env")
            .ignoreIfMissing()
            .load();

        assertHostEnvVar(dotenv);

        assertNull(dotenv.get("MY_TEST_EV1"));
    }

    private void assertHostEnvVar(Dotenv env) {
        var isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (isWindows) {
            var path = env.get("PATH");
            assertNotNull(path);
        } else {
            var expectedHome = System.getProperty("user.home");
            var actualHome = env.get("HOME");
            assertEquals(expectedHome, actualHome);
        }
    }
}
