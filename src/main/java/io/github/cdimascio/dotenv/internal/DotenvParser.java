package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvEntry;
import io.github.cdimascio.dotenv.DotenvException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

/**
 * (Internal) Parses .env file
 */
public class DotenvParser {

    private static final Pattern WHITE_SPACE_REGEX = Pattern.compile("^\\s*$"); // ^\s*${'$'}

    // The follow regex matches key values.
    // It supports quoted values surrounded by single or double quotes
    // -  Single quotes: ['][^']*[']
    //    The above regex snippet matches a value wrapped in single quotes.
    //    The regex snippet does not match internal single quotes. This is present to allow the trailing comment to include single quotes
    // -  Double quotes: same logic as single quotes
    // It ignore trailing comments
    // - Trailing comment: \s*(#.*)?$
    //   The above snippet ignore spaces, the captures the # and the trailing comment
    private static final Pattern DOTENV_ENTRY_REGEX = Pattern.compile("^\\s*([\\w.\\-]+)\\s*(=)\\s*(['][^']*[']|[\"][^\"]*[\"]|[^#]*)?\\s*(#.*)?$"); //"^\\s*([\\w.\\-]+)\\s*(=)\\s*([^#]*)?\\s*(#.*)?$"); // ^\s*([\w.\-]+)\s*(=)\s*([^#]*)?\s*(#.*)?$

    private final DotenvReader reader;
    private final boolean ignoreEmtpy;
    private final boolean throwIfMissing;
    private final boolean throwIfMalformed;

    private final Predicate<String> isWhiteSpace = s -> matches(WHITE_SPACE_REGEX, s);
    private final Predicate<String> isComment = s -> s.startsWith("#") || s.startsWith("////");
    private final Predicate<String> isQuoted = s -> s.startsWith("\"") && s.endsWith("\"");
    private final Function<String, DotenvEntry> parseLine = s -> matchEntry(DOTENV_ENTRY_REGEX, s);

    /**
     * Creates a dotenv parser
     * @param reader the dotenv reader
     * @param ignoreEmpty if true, returns `null` for empty or blank values
     * @param throwIfMissing if true, throws when the .env file is missing
     * @param throwIfMalformed if true, throws when the .env file is malformed
     */
    public DotenvParser(DotenvReader reader, boolean ignoreEmpty, boolean throwIfMissing, boolean throwIfMalformed) {
        this.reader = reader;
        this.ignoreEmtpy = ignoreEmpty;
        this.throwIfMissing = throwIfMissing;
        this.throwIfMalformed = throwIfMalformed;
    }

    /**
     * (Internal) parse the .env file
     * @return a list of DotenvEntries
     * @throws DotenvException if an error is encountered during the parse
     */
    public List<DotenvEntry> parse() throws DotenvException {
        List<DotenvEntry> entries = new ArrayList<>();
        for (String line : lines()) {
            String l = line.trim();
            if (isWhiteSpace.test(l) || isComment.test(l) || isBlank(l)) continue;

            DotenvEntry entry = parseLine.apply(l);
            if (entry == null) {
                if (throwIfMalformed) throw new DotenvException("Malformed entry " + l);
                continue;
            }
            String key = entry.getKey();
            String value = normalizeValue(entry.getValue());
            if (value.isEmpty() && ignoreEmtpy) {
                continue;
            }
            entries.add(new DotenvEntry(key, value));
        }
        return entries;
    }

    private List<String> lines() throws DotenvException {
        try {
            return reader.read();
        } catch (DotenvException e) {
            if (throwIfMissing) throw e;
            return emptyList();
        } catch (IOException e) {
            throw new DotenvException(e);
        }
    }

    private String normalizeValue(String value) {
        String tr = value.trim();
        return isQuoted.test(tr)
            ? tr.substring(1, value.length() - 1)
            : tr;
    }

    private static boolean matches(Pattern regex, String text) {
        Matcher matcher = regex.matcher(text);
        return matcher.matches();
    }

    private static DotenvEntry matchEntry(Pattern regex, String text) {
        Matcher matcher = regex.matcher(text);
        boolean result = matcher.matches();
        if (!result || matcher.groupCount() < 3) return null;
        return new DotenvEntry(matcher.group(1), matcher.group(3));
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
