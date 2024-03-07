package org.meeuw.i18n.languages;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Implementation of {@link LanguageCode} that {@link ISO_639#stream() produces} all ISO-639-3 codes.
 * <p>
 * Normally it makes sense to just use {@link LanguageCode}. 
 
 */
public class ISO_639_3_Code implements LanguageCode {

    final static Logger LOGGER = Logger.getLogger(LanguageCode.class.getName());


    static final Map<String, ISO_639_3_Code> KNOWN;
    
    
    static final Map<LanguageCode, List<LanguageCode>> INDIVIDUAL_LANGUAGES;
    
    static final Map<LanguageCode, List<LanguageCode>> MACRO;



    static final String DIR = "/iso-639-3_Code_Tables_20240207/";
    static {
        Map<String, List<NameRecord>> namesMap = new HashMap<>();
        try (InputStream inputStream = ISO_639_3_Code.class.getResourceAsStream(DIR + "iso-639-3_Name_Index.tab");
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            String line = inputStreamReader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                List<NameRecord> names = namesMap.computeIfAbsent(split[0], k -> new ArrayList<>());
                names.add(new NameRecord(split[1], split[2]));
                line = inputStreamReader.readLine();
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        Map<String, ISO_639_3_Code> temp = new HashMap<>();
        try (InputStream inputStream = ISO_639_3_Code.class.getResourceAsStream(DIR + "iso-639-3.tab");
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            inputStreamReader.readLine(); // skipheader;
            String line = inputStreamReader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                List<NameRecord> names = namesMap.get(split[0]);
                ISO_639_3_Code found = new ISO_639_3_Code(
                    split[0],
                    split[1].length() > 0 ? split[1] : null,
                    split[2].length() > 0 ? split[2] : null,
                    split[3].length() > 0 ? split[3] : null,
                    Scope.valueOf(split[4]),
                    Type.valueOf(split[5]),
                    split[6],
                    split.length == 8 ? split[7] : null,
                    names
                );
                temp.put(found.part3().toLowerCase(), found);
                line = inputStreamReader.readLine();
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        KNOWN = Collections.unmodifiableMap(temp);
        
        Map<LanguageCode, List<LanguageCode>> tempIndividual = new HashMap<>();
        Map<LanguageCode, List<LanguageCode>> tempMacro = new HashMap<>();
        try (InputStream inputStream = ISO_639_3_Code.class.getResourceAsStream(DIR + "iso-639-3-macrolanguages.tab");
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            inputStreamReader.readLine(); // skipheader;
            String line = inputStreamReader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                ISO_639_3_Code macro = KNOWN.get(split[0]);
                List<LanguageCode> list = tempIndividual.computeIfAbsent(macro, (m) -> new ArrayList<>());
                Optional<ISO_639_3_Code> individual = getByPart3(split[1], true, Level.FINEST);
                if (individual.isPresent()) {
                    list.add(LanguageCode.updateToEnum(individual.get()));
                    tempMacro.computeIfAbsent(individual.get(), (m) -> new ArrayList<>()).add(LanguageCode.updateToEnum(macro));
                } else {
                    LOGGER.log(Level.FINEST, "Unknown individual language: " + split[1] + " for " + macro);
                }
                line = inputStreamReader.readLine();
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        tempIndividual.replaceAll((k, v) -> Collections.unmodifiableList(v));
        tempMacro.replaceAll((k, v) -> Collections.unmodifiableList(v));
        
        INDIVIDUAL_LANGUAGES = Collections.unmodifiableMap(tempIndividual);
        MACRO = Collections.unmodifiableMap(tempMacro);
    }
    
    /**
     * A stream with all known {@link ISO_639_Code language codes}.
     * If the langauge has a 2 letter part 1 code, it will <em>not</em> be implicitly upgraded
     * to an {@link ISO_639_1_Code
     *
     * @see {@link LanguageCode#stream()} For a version that <em>does</em> upgrade
     * @return a stream of all known language codes. 
     * 
     */
     public static Stream<ISO_639_3_Code> stream() {
        return KNOWN.values()
            .stream()
            .sorted(Comparator.comparing(ISO_639_3_Code::code));
     }
    
    private static final Map<String, String> RETIRED = new HashMap<>();
    static {
        RETIRED.put("jw", "jv"); // 'Javanese is rendered as "jw" in table 1, while it is correctly given as "jv" in the other tables
        RETIRED.put("iw", "he"); // The identifier for Hebrew was changed from "iw" to "he".
        RETIRED.put("in", "id"); // The identifier for Indonesian was changed from "in" to "id".
        RETIRED.put("ji", "yi"); // The identifier for Yiddish was changed from "ji" to "yi".
    }
    
    static Optional<ISO_639_3_Code> getByPart1(String code) {
        if (code == null) {
            return Optional.empty();
        }
        code = code.toLowerCase();
        code = RETIRED.getOrDefault(code, code);
        final String finalCode = code;
        return  ISO_639_3_Code.stream()
            .filter(i -> finalCode.equals(i.part1()))
            .findFirst();
    }
    
    
    /**
     * Retrieves a {@link ISO_639_3_Code} by its three-letter identifier {@link ISO_639#getByPart3(String, boolean)} ()}
     * <p>
     * If the given code is a {@link RetiredLanguageCode retired code}, the replacement code is returned if possible. If a retired code is matched, but no single replacement is found, an empty optional is returned, and a warning is logged (using {@link java.util.logging JUL})
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     * @since 2.2
     */
    static Optional<ISO_639_3_Code> getByPart3(@Size(min = 3, max=3) String code, boolean matchRetired, Level level) {
        if (code == null) {
            return Optional.empty();
        }
        ISO_639_3_Code prop = KNOWN.get(code.toLowerCase());
        if (prop == null && matchRetired) {
            Optional<RetiredLanguageCode> retiredLanguageCode = RetiredLanguageCode.getByCode(code);
            if (retiredLanguageCode.isPresent() && retiredLanguageCode.get().retReason() != RetirementReason.N) {
                try {
                    prop = KNOWN.get(retiredLanguageCode.get().changeTo().part3());
                } catch (RetiredLanguageCode.RetirementException e) {
                    LOGGER.log(level, "Could not find single replacement for " + code + " " + e.getMessage());
                }
            }
        }
        return Optional.ofNullable(prop);
    }

    @Size(min = 3, max = 3)
    @NotNull
    private final String part3;

    private transient final String part2B;

    private transient final String part2T;


    @Size(min = 2, max = 2)
    @NotNull
    private transient final String part1;
    private transient final Scope scope;
    private transient final Type languageType;
    @NotNull
    private transient final String refName;
    private transient final String comment;

    private transient  final List<NameRecord> names;

    private ISO_639_3_Code(
        String part3,
        String part2B,
        String part2T,
        String part1,
        Scope scope,
        Type languageType,
        String refName,
        String comment,
        List<NameRecord> names
    ) {
        this.part3 = part3;
        this.part2B = part2B;
        this.part2T = part2T;
        this.part1 = part1;
        this.scope = scope;
        this.languageType = languageType;
        this.refName = refName;
        this.comment = comment;
        this.names  = Collections.unmodifiableList(names);
    }

  
    /**
     * The {@link #part1() ISO-639-1-code} if available, otherwise the {@link #part3() ISO-639-3 code}.
     *
     * @return A 2 or 3 letter language code
     * @since 0.2
     */
    @JsonValue
    public String code() {
        return part1 != null ? part1 : part3;
    }


    @Override
    public String toString() {
        return code() + " (" + refName + ")";
    }


    
    public String part3() {
        return part3;
    }

    /**
     * Equivalent 639-2 identifier of the bibliographic applications
     * code set, if there is one
     * @return bibliographic id or {@code null}
     */
    public String part2B() {
        return part2B;
    }

    /**
     * Equivalent 639-2 identifier of the terminology applications code
     * set, if there is one
     * @return terminology id or {@code null}
     */
    public String part2T() {
        return part2T;
    }

    /**
     * Equivalent 639-1 identifier, if there is one
     * @return 2 letter id or {@code null}
     */
    public String part1() {
        return part1;
    }

    public Scope scope() {
        return scope;
    }

    public Type languageType() {
        return languageType;
    }

    public String refName() {
        return refName;
    }

    public String comment() {
        return comment;
    }

    /**
     * The names (in english) of the language.
     */
    @Override
    public List<NameRecord> nameRecords() {
        return names;
    }
    
    @Size
    public NameRecord nameRecord(Locale locale) {
        if (locale.getLanguage().equals("en")) {
            return names.get(0);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public List<LanguageCode> macroLanguages() {
        return MACRO.getOrDefault(this, Collections.emptyList());
    }

    @Override
    public List<LanguageCode> individualLanguages() {
        return INDIVIDUAL_LANGUAGES.getOrDefault(this, Collections.emptyList());
    }

    private Object readResolve() {
        return LanguageCode.get(part3()).orElse(this);
    }
    
}
