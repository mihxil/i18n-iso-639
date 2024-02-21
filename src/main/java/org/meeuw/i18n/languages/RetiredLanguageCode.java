package org.meeuw.i18n.languages;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class represents a language code that has been retired.
 *<p>
 * @see <a href="https://iso639-3.sil.org/code_tables/deprecated_codes/data">https://iso639-3.sil.org/code_tables/deprecated_codes/data</a>
 * <p>
 * The main goal of this class is to be used in {@link LanguageCode#getByCode(String)}, which will return the unretired language code if possible.
 *
 */
public class RetiredLanguageCode implements Serializable {

    static final Map<String, RetiredLanguageCode> KNOWN;

    static {
        Map<String, RetiredLanguageCode> map = new HashMap<>();
        try (InputStream inputStream = LanguageCode.class.getResourceAsStream(LanguageCode.DIR + "iso-639-3_Retirements.tab");
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            inputStreamReader.readLine(); // first line is a header
            String line = inputStreamReader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                map.put(split[0],
                    new RetiredLanguageCode(
                        split[0],
                        split[1],
                    RetirementReason.valueOf(split[2]),
                        split[3].isEmpty() ? null : split[3],
                        split[4].isEmpty() ? null : split[4],
                        LocalDate.parse(split[5])
                    ));
                line = inputStreamReader.readLine();
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        KNOWN = Collections.unmodifiableMap(map);
    }

    private final String code;

    private transient final String refName;

    private transient final RetirementReason retReason;

    private transient final String changeTo;

    private transient final String retRemedy;

    private transient final LocalDate effective;


    private RetiredLanguageCode(String code, String refName, RetirementReason retReason, String changeTo, String retRemedy, LocalDate effective) {
        this.code = code;
        this.refName = refName;
        this.retReason = retReason;
        this.changeTo = changeTo;
        this.retRemedy = retRemedy;
        this.effective = effective;
    }

    /**
     * A stream with all known {@link LanguageCode}
     *
     * @return a stream of all known language codes.
     */
    public static Stream<RetiredLanguageCode> stream() {
        return KNOWN.values().stream();
    }

    public static Optional<RetiredLanguageCode> getByCode(String code) {
        return Optional.ofNullable(KNOWN.get(code));
    }

    public String getCode() {
        return code;
    }

    public String getRefName() {
        return refName;
    }

    public RetirementReason getRetReason() {
        return retReason;
    }


    /**
     * @return the language code to which this language was changed, or null if it doesn't exist
     * @throws RetirementException If the {@link #getRetRemedy()} should be inspected by a human.
     */
    public LanguageCode getChangeTo() throws RetirementException{
        if (retReason == RetirementReason.N) {
            return null;
        }
        if (changeTo == null) {
            throw new RetirementException("Remedy for " + code + ": " + retRemedy);
        }
        return LanguageCode.getById(changeTo).orElseThrow();
    }

    public String getRetRemedy() {
        return retRemedy;
    }

    public LocalDate getEffective() {
        return effective;
    }

    @Override
    public String toString() {
        StringJoiner joiner =  new StringJoiner(", ", RetiredLanguageCode.class.getSimpleName() + "[", "]")
            .add("code='" + code + "'")
            .add("refName='" + refName + "'")
            .add("retReason=" + retReason);
        if (changeTo != null) {
            try {
                joiner.add("changeTo='" + getChangeTo().getCode() + "'");
            } catch (RetirementException e) {

            }
        }
        if (retRemedy != null) {
            joiner.add("retRemedy='" + retRemedy + "'");
        }

        return joiner.add("effective=" + effective)
            .toString();
    }

    public static class RetirementException extends Exception {
        public RetirementException(String message) {
            super(message);
        }
    }


    private Object readResolve() {
        return getByCode(code).orElse(this);
    }
}
