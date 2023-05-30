package io.github.cdimascio.dotenv;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DotenvReaderTests {

    @Test
    void verifyDotenvFileReader() {
        DotenvBuilder builder = Mockito.mock(DotenvBuilder.class);
        Mockito.when(builder.pathsApiAvailable()).thenReturn(false);
        final var dotenv = Dotenv.configure().ignoreIfMalformed().load();
        final String value = dotenv.get("MY_TEST_EV2");
        assertEquals("my test ev 2", value);
    }

    @Test
    void verifyDotenvPathReader() {
        DotenvBuilder builder = Mockito.mock(DotenvBuilder.class);
        Mockito.when(builder.pathsApiAvailable()).thenReturn(true);
        final var dotenv = Dotenv.configure().ignoreIfMalformed().load();
        final String value = dotenv.get("MY_TEST_EV1");
        assertEquals("my test ev 1", value);
    }

}
