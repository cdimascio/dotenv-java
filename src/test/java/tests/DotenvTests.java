package tests;

import io.github.cdimascio.dotenv.DotenvException;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DotenvTests {
    private Map<String, String> envVars;

    @Before
    public void setUp() {
        envVars = new HashMap<>();
        envVars.put("MY_TEST_EV1", "my test ev 1");
        envVars.put("MY_TEST_EV2", "my test ev 2");
        envVars.put("WITHOUT_VALUE", "");
    }

    @Test(expected = DotenvException.class)
    public void throwIfMalconfigured() {
        Dotenv.configure().load();
    }

    @Test(expected = DotenvException.class)
    public void load() {
        Dotenv dotenv = Dotenv.load();

        for (String envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }
    }

    @Test
    public void iteratorOverDotenv() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        dotenv
            .entries()
            .forEach(e -> assertEquals(dotenv.get(e.getKey()), e.getValue()));

        for (DotenvEntry e : dotenv.entries()) {
            assertEquals(dotenv.get(e.getKey()), e.getValue());
        }
    }

    @Test
    public void iteratorOverDotenvWithFilter() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        Set<DotenvEntry> entriesInFile = dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE);
        Set<DotenvEntry> entriesAll = dotenv.entries();
        assertTrue(entriesInFile.size() < entriesAll.size());

        for (Map.Entry<String, String> e: envVars.entrySet()) {
            assertEquals(dotenv.get(e.getKey()), e.getValue());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failToRemoveFromDotenv() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        Iterator<DotenvEntry> iter = dotenv.entries().iterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failToAddToDotenv() {

        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        dotenv.entries().add(new DotenvEntry("new", "value"));
    }

    @Test
    public void configureWithIgnoreMalformed() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        for (String envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }
    }

    @Test
    public void configureWithIgnoreMissingAndMalformed() {
        Dotenv dotenv = Dotenv.configure()
            .directory("/missing/dir")
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

        assertNotNull(dotenv.get("PATH"));
    }
}
