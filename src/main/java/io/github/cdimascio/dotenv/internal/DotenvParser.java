package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvEntry;
import io.github.cdimascio.dotenv.DotenvException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
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
    private final boolean throwIfMissing;
    private final boolean throwIfMalformed;

    private static final Predicate<String> isWhiteSpace = s -> matches(WHITE_SPACE_REGEX, s);
    private static final Predicate<String> isComment = s -> s.startsWith("#") || s.startsWith("////");
    private static final Predicate<String> isQuoted = s -> s.length() > 1 && s.startsWith("\"") && s.endsWith("\"");
    private final Function<String, DotenvEntry> parseLine = s -> matchEntry(DOTENV_ENTRY_REGEX, s);

    /**
     * Creates a dotenv parser
     *
     * @param reader           the dotenv reader
     * @param throwIfMissing   if true, throws when the .env file is missing
     * @param throwIfMalformed if true, throws when the .env file is malformed
     */
    public DotenvParser(final DotenvReader reader, final boolean throwIfMissing, final boolean throwIfMalformed) {
        this.reader = reader;
        this.throwIfMissing = throwIfMissing;
        this.throwIfMalformed = throwIfMalformed;
    }

    /**
     * (Internal) parse the .env file
     *
     * @return a list of DotenvEntries
     * @throws DotenvException if an error is encountered during the parse
     */
    public List<DotenvEntry> parse() throws DotenvException {
        final var lines = lines();
        final var entries = new ArrayList<DotenvEntry>(lines.size());
        for (final var line : lines) {
            addNewEntry(entries, line.trim());
        }

        return entries;
    }

    private void addNewEntry(final List<DotenvEntry> entries, final String line) {
        if (isWhiteSpace.test(line) || isComment.test(line) || isBlank(line))
            return;

        final var entry = parseLine.apply(line);
        if (entry == null) {
            if (throwIfMalformed)
                throw new DotenvException("Malformed entry " + line);
            return;
        }

        if (!QuotedStringValidator.isValid(entry.getValue())) {
            if (throwIfMalformed)
                throw new DotenvException("Malformed entry, unmatched quotes " + line);
            return;
        }
        final var key = entry.getKey();
        final var value = QuotedStringValidator.stripQuotes(entry.getValue());
        entries.add(new DotenvEntry(key, value));
    }

    private List<String> lines() throws DotenvException {
        try {
            return reader.read();
        } catch (DotenvException e) {
            if (throwIfMissing)
                throw e;
            return emptyList();
        } catch (IOException e) {
            throw new DotenvException(e);
        }
    }

    private static boolean matches(final Pattern regex, final String text) {
        return regex.matcher(text).matches();
    }

    private static DotenvEntry matchEntry(final Pattern regex, final String text) {
        final var matcher = regex.matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 3)
            return null;

        return new DotenvEntry(matcher.group(1), matcher.group(3));
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Internal: Validates quoted strings
     */
    private static class QuotedStringValidator {
        private static boolean isValid(String input) {
            final var s = input.trim();
            if (!s.startsWith("\"") && !s.endsWith("\"")) {
                // not quoted, its valid
                return true;
            }
            if (input.length() == 1 || !(s.startsWith("\"") && s.endsWith("\""))) {
                // doesn't start and end with quote
                return false;
            }
            // remove start end quote
            var content = s.substring(1, s.length() - 1);
            var quotePattern = Pattern.compile("\"");
            var matcher = quotePattern.matcher(content);

            // Check for unescaped quotes
            while (matcher.find()) {
                int quoteIndex = matcher.start();
                // Check if the quote is escaped
                if (quoteIndex == 0 || content.charAt(quoteIndex - 1) != '\\') {
                    return false; // unescaped quote found
                }
            }
            return true; // No unescaped quotes found
        }
        private static String stripQuotes(String input) {
            var tr = input.trim();
            return isQuoted.test(tr) ? tr.substring(1, input.length() - 1) : tr;
        }
    }
}

