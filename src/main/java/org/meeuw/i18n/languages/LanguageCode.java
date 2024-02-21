package org.meeuw.i18n.languages;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.meeuw.i18n.languages.jaxb.LanguageCodeAdapter;

/**
 * A language with a ISO 639-3 language code (of three letters). Also, aware of the ISO-630-1 2 letter codes if they exist.
 *<p>
 * Annotated with {@link XmlJavaTypeAdapter}, so it will automatically be marshalled and unmarshalled in XML's. 
 * <p>
 * Also annotated with jackon annotation, to be marshalled and unmarshalled in JSON as the code.
 *<p>
 * This class is immutable and can be used as a key in maps.
 */
@XmlJavaTypeAdapter(LanguageCodeAdapter.class)
public class LanguageCode implements Serializable, Comparable<LanguageCode> {

    private final static Logger LOGGER = Logger.getLogger(LanguageCode.class.getName());


    static final Map<String, LanguageCode> KNOWN;

    static final String DIR = "/iso-639-3_Code_Tables_20240207/";
    static {
        Map<String, String[]> names = new HashMap<>();
        try (InputStream inputStream = LanguageCode.class.getResourceAsStream(DIR + "iso-639-3_Name_Index.tab");
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            String line = inputStreamReader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                names.put(split[0], new String[] {split[1], split[2]});
                line = inputStreamReader.readLine();
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        Map<String, LanguageCode> temp = new HashMap<>();
        try (InputStream inputStream = LanguageCode.class.getResourceAsStream(DIR + "iso-639-3.tab");
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            inputStreamReader.readLine(); // skipheader;
            String line = inputStreamReader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                String[] name = names.get(split[0]);
                LanguageCode found = new LanguageCode(
                    split[0],
                    split[1].length() > 0 ? split[1] : null,
                    split[2].length() > 0 ? split[2] : null,
                    split[3].length() > 0 ? split[3] : null,
                    Scope.valueOf(split[4]),
                    Type.valueOf(split[5]),
                    split[6],
                    split.length == 8 ? split[7] : null,
                    name[0],
                    name[1]
                );
                temp.put(found.getId().toLowerCase(), found);
                line = inputStreamReader.readLine();
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        KNOWN = Collections.unmodifiableMap(temp);

    }

    @Size(min = 3, max = 3)
    @NotNull
    private final String id;

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

    private transient  final String name;
    private transient  final String invertedName;

    private LanguageCode(
        String id,
        String part2B,
        String part2T,
        String part1,
        Scope scope,
        Type languageType,
        String refName,
        String comment,
        String name,
        String invertedName
    ) {
        this.id = id;
        this.part2B = part2B;
        this.part2T = part2T;
        this.part1 = part1;
        this.scope = scope;
        this.languageType = languageType;
        this.refName = refName;
        this.comment = comment;
        this.name  = name;
        this.invertedName = invertedName;
    }

    /**
     * A stream with all known {@link LanguageCode}
     *
     * @return a stream of all known language codes.
     */
    public static Stream<LanguageCode> stream() {
        return KNOWN.values().stream();
    }

    /**
     * Retrieves a {@link LanguageCode} by its three-letter identifier {@link #getId()} (using {@link #getByCode(String)}, or by its two letter identifier {@link #getPart1()}.
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link LanguageCode} if found.
     * @see #getCode()
     * @see #getByPart1(String)
     * @see #getByCode(String)
     * @see #languageCode(String)
     * @since 0.2
     */
    public static Optional<LanguageCode> get(String code, boolean matchRetired) {
        if (code.length() == 2) {
            return getByPart1(code);
        } else {
            return getByCode(code, matchRetired);
        }
    }
    
    public static Optional<LanguageCode> get(String code) {
        return get(code, true);
    }

    /**
     * As {@link #get(String)}, but throws an {@link IllegalArgumentException} if not found.
     *
     * @return The {@link LanguageCode} if found
     * @throws IllegalArgumentException if not found
     */
    @JsonCreator
    public static LanguageCode languageCode(String code) {
        return get(code).orElseThrow(() -> new IllegalArgumentException("Unknown language code " + code));
    }


    /**
     * Retrieves a {@link LanguageCode} by its three-letter identifier {@link #getId()}
     *
     * If the given code is a {@link RetiredLanguageCode retired code}, the replacement code is returned if possible. If a retired code is matched, but no single replacement is found, an empty optional is returned, and a warning is logged (using {@link java.util.logging JUL})
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link LanguageCode} if found.
     * @since 2.2
     */
    public static Optional<LanguageCode> getById(@Size(min = 3, max=3) String code, boolean matchRetired) {
        if (code == null) {
            return Optional.empty();
        }
        LanguageCode prop = KNOWN.get(code.toLowerCase());
        if (prop == null && matchRetired){
            Optional<RetiredLanguageCode> retiredLanguageCode = RetiredLanguageCode.getByCode(code);
            if (retiredLanguageCode.isPresent() && retiredLanguageCode.get().getRetReason() != RetirementReason.N) {
                try {
                    prop = retiredLanguageCode.get().getChangeTo();
                } catch (RetiredLanguageCode.RetirementException e) {
                    LOGGER.log(Level.WARNING, "Could not find single replacement for " + code + " " + e.getMessage());
                }
            }
        }

        return Optional.ofNullable(prop);
    }


    /**
     * Defaulting version of {@link #getById(String, boolean)}, matching retired codes too.
     * @deprecated Confusing, since not matching like {@link #getCode()}
     * @see #getById(String,) 
     */
    @Deprecated
    public static Optional<LanguageCode> getByCode(@Size(min = 3, max=3) String code) {
        return getById(code);
    }
    
      /**
     * Defaulting version of {@link #getById(String, boolean)}, matching retired codes too.
     * @deprecated Confusing, since not matching like {@link #getCode()}
     * @see #getById(String,) 
     */
    @Deprecated
    public static Optional<LanguageCode> getById(@Size(min = 3, max=3) String code) {
        return getById(code, true);
    }




    /**
     * Retrieves a {@link LanguageCode} by its Part1 code {@link #getPart1()}
     *
     * @param code A 2 letter language code
     * @return An optional containing the {@link LanguageCode} if found.
     */
    public static Optional<LanguageCode> getByPart1(String code) {
        if (code == null) {
            return Optional.empty();
        }
        final String lowerCode = code.toLowerCase();
        return KNOWN.values().stream().filter(i -> lowerCode.equals(i.getPart1())).findFirst();
    }

    /**
     * Retrieves a {@link LanguageCode} by its Part2B  ('bibliographic') code {@link #getPart2B()}
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link LanguageCode} if found.
     */
    public static Optional<LanguageCode> getByPart2B(String code) {
        if (code == null) {
            return Optional.empty();
        }
        final String lowerCode = code.toLowerCase();
        return KNOWN.values().stream().filter(i -> lowerCode.equals(i.getPart2B())).findFirst();
    }


    /**
     * Retrieves a {@link LanguageCode} by its Part2T ('terminology') code {@link #getPart2T()}
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link LanguageCode} if found.
     */
    public static Optional<LanguageCode> getByPart2T(String code) {
        if (code == null) {
            return Optional.empty();
        }
        final String lowerCode = code.toLowerCase();
        return KNOWN.values().stream().filter(i -> lowerCode.equals(i.getPart2T())).findFirst();
    }


    /**
     * the ISO-639-1-code if available, otherwise the ISO-639-3 code.
     *
     * @return A 2 or 3 letter language code
     * @since 0.2
     */
    @JsonValue
    public String getCode() {
        return part1 != null ? part1 : id;
    }


    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", LanguageCode.class.getSimpleName() + "[", "]").add("id='" + id + "'");
        if (part2B != null) {
            joiner.add("part2B='" + part2B + "'");
        }
        if (part2T != null) {
            joiner.add("part2T='" + part2T + "'");
        }
        if (part1 != null) {
            joiner.add("part1='" + part1 + "'");
        }
        joiner.add("scope='" + scope + "'")
            .add("languageType='" + languageType + "'")
            .add("refName='" + refName + "'");
        if (comment != null) {
            joiner.add("comment='" + comment + "'");
        }
        return joiner.toString();
    }


    /**
     * The three-letter 639-3 identifier
     * @return The three-letter 639-3 identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Equivalent 639-2 identifier of the bibliographic applications
     * code set, if there is one
     * @return bibliographic id or {@code null}
     */
    public String getPart2B() {
        return part2B;
    }

    /**
     * Equivalent 639-2 identifier of the terminology applications code
     * set, if there is one
     * @return terminology id or {@code null}
     */
    public String getPart2T() {
        return part2T;
    }

    /**
     * Equivalent 639-1 identifier, if there is one
     * @return 2 letter id or {@code null}
     */
    public String getPart1() {
        return part1;
    }

    public Scope getScope() {
        return scope;
    }

    public Type getLanguageType() {
        return languageType;
    }

    public String getRefName() {
        return refName;
    }

    public String getComment() {
        return comment;
    }

    /**
     * The name (in english) of the language.
     */
    public String getName() {
        return name;
    }

    /**
     * Sometimes the name of the language starts with something like 'eastern' or so. This returns the name with these prefixes postfixed, so this is the natural name to <em>sort</em> languages on.
     */
    public String getInvertedName() {
        return invertedName;
    }
    
    public Locale toLocale() {
        return new Locale(getCode());
    }

    @Override
    public int compareTo(LanguageCode o) {
        return invertedName.compareTo(o.invertedName);
    }

    private Object readResolve() {
        return get(id).orElse(this);
    }


}
