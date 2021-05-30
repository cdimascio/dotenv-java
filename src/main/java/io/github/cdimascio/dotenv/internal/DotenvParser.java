package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvEntry;
import io.github.cdimascio.dotenv.DotenvException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

/**
 * (Internal) Parses .env file
 */
public class DotenvParser {
    private final DotenvReader reader;
    private final boolean throwIfMissing;
    private final boolean throwIfMalformed;

    private final Function<String, Boolean> isWhiteSpace = s -> matches("^\\s*$", s); // ^\s*${'$'}
    private final Function<String, Boolean> isComment = s -> s.startsWith("#") || s.startsWith("////");
    private final Function<String, Boolean> isQuoted = s -> s.startsWith("\"") && s.endsWith("\"");
    private final Function<String, DotenvEntry> parseLine = s -> matchEntry("^\\s*([\\w.\\-]+)\\s*(=)\\s*(.*)?\\s*$", s); // ^\s*([\w.\-]+)\s*(=)\s*(.*)?\s*$

    /**
     * Creates a dotenv parser
     * @param reader the dotenv reader
     * @param throwIfMissing if true, throws when the .env file is missing
     * @param throwIfMalformed if true, throws when the .env file is malformed
     */
    public DotenvParser(DotenvReader reader, boolean throwIfMissing, boolean throwIfMalformed) {
        this.reader = reader;
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
            if (isWhiteSpace.apply(l) || isComment.apply(l) || isBlank(l)) continue;

            DotenvEntry entry = parseLine.apply(l);
            if (entry == null) {
                if (throwIfMalformed) throw new DotenvException("Malformed entry "+ l);
                continue;
            }
            String key = entry.getKey();
            String value = normalizeValue(entry.getValue());
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
        return isQuoted.apply(tr)
            ? tr.substring(1, value.length() -1)
            : tr;
    }

    private static boolean matches(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    private static DotenvEntry matchEntry(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        boolean result = matcher.matches();
        if (!result || matcher.groupCount() < 3) return null;
        return new DotenvEntry(matcher.group(1), matcher.group(3));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
