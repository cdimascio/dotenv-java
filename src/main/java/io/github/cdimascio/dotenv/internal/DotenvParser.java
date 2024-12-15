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
        final var entries = new ArrayList<DotenvEntry>();
  
        var currentEntry = "";
        for (final var line : lines) {
            if (currentEntry.equals("") && (isWhiteSpace.test(line) || isComment.test(line) || isBlank(line)))
                continue;

            currentEntry += line;

            final var entry = parseLine.apply(currentEntry);
            if (entry == null) {
                if (throwIfMalformed)
                    throw new DotenvException("Malformed entry " + currentEntry);
                currentEntry = "";
                continue;
            }

            var value = entry.getValue();
            if (QuotedStringValidator.startsWithQuote(value) && !QuotedStringValidator.endsWithQuote(value)) {
                currentEntry += "\n";
                continue;
            }
            if (!QuotedStringValidator.isValid(entry.getValue())) {
                if (throwIfMalformed)
                    throw new DotenvException("Malformed entry, unmatched quotes " + line);
                currentEntry = "";
                continue;
            }
            final var key = entry.getKey();
            value = QuotedStringValidator.stripQuotes(entry.getValue());
            entries.add(new DotenvEntry(key, value));
            currentEntry = "";
        }

        return entries;
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
            if (isNotQuoted(s)) {
                return true;
            }
            if (doesNotStartAndEndWithQuote(s)) {
                return false;
            }

            return !hasUnescapedQuote(s); // No unescaped quotes found
        }
        private static boolean hasUnescapedQuote(final String s) {
            boolean hasUnescapedQuote = false;
            // remove start end quote
            var content = s.substring(1, s.length() - 1);
            var quotePattern = Pattern.compile("\"");
            var matcher = quotePattern.matcher(content);

            // Check for unescaped quotes
            while (matcher.find()) {
                int quoteIndex = matcher.start();
                // Check if the quote is escaped
                if (quoteIndex == 0 || content.charAt(quoteIndex - 1) != '\\') {
                    hasUnescapedQuote = true; // unescaped quote found
                }
            }
            return hasUnescapedQuote;
        }
        private static boolean doesNotStartAndEndWithQuote(final String s) {
            return s.length() == 1 || !(startsWithQuote(s) && endsWithQuote(s));
        }
        private static boolean endsWithQuote(final String s) {
            return s.endsWith("\"");
        }
        private static boolean startsWithQuote(final String s) {
            return s.startsWith("\"");
        }
        private static boolean isNotQuoted(final String s) {
            return !startsWithQuote(s) && !endsWithQuote(s);
        }
        private static String stripQuotes(String input) {
            var tr = input.trim();
            return isQuoted.test(tr) ? tr.substring(1, input.length() - 1) : tr;
        }
    }
}

