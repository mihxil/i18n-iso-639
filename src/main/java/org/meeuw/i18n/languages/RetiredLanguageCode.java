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
 * The main goal of this class is to be used in {@link ISO_639#getByPart3(String)}, which will return the unretired language code if possible.
 *
 */
@SuppressWarnings("DataFlowIssue")
public class RetiredLanguageCode implements Serializable, LanguageCode {

    static final Map<String, RetiredLanguageCode> KNOWN;

    static {
        Map<String, RetiredLanguageCode> map = new HashMap<>();
        try (InputStream inputStream = ISO_639_Code.class.getResourceAsStream(ISO_639_3_Code.DIR + "iso-639-3_Retirements.tab");
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
     * A stream with all known {@link ISO_639_Code}
     *
     * @return a stream of all known language codes.
     */
    public static Stream<RetiredLanguageCode> stream() {
        return KNOWN.values().stream();
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String part3() {
        return code;
    }

    @Override
    public String part2B() {
        return null;
    }

    @Override
    public String part2T() {
        return null;
    }

    @Override
    public String part1() {
        return null;
    }

    public static Optional<RetiredLanguageCode> getByCode(String code) {
        return Optional.ofNullable(KNOWN.get(code));
    }

    public RetirementReason retReason() {
        return retReason;
    }

    /**
     * @return the language code to which this language was changed, or null if it doesn't exist
     * @throws RetirementException If the {@link #retRemedy()} should be inspected by a human.
     */
    public LanguageCode changeTo() throws RetirementException{
        if (retReason == RetirementReason.N) {
            return null;
        }
        if (changeTo == null) {
            throw new RetirementException("Remedy for " + code + ": " + retRemedy);
        }
        return ISO_639.getByPart3(changeTo).orElseThrow();
    }

    public String retRemedy() {
        return retRemedy;
    }

    public LocalDate effective() {
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
                joiner.add("changeTo='" + changeTo().code() + "'");
            } catch (RetirementException ignored) {

            }
        }
        if (retRemedy != null) {
            joiner.add("retRemedy='" + retRemedy + "'");
        }

        return joiner.add("effective=" + effective)
            .toString();
    }

    @Override
    public Scope scope() {
        return Scope.I;
    }

    @Override
    public Type languageType() {
        return null;
    }

    @Override
    public String refName() {
        return refName;
    }

    @Override
    public String comment() {
        return retRemedy;
    }


    @Override
    public List<NameRecord> nameRecords() {
        return Arrays.asList(new NameRecord(refName));
    }

    @Override
    public List<LanguageCode> macroLanguages() {
        return Collections.emptyList();
    }

    @Override
    public List<LanguageCode> individualLanguages() {
        return Collections.emptyList();
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
