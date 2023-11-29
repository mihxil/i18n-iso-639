package org.meeuw.i18n.languages;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.meeuw.i18n.languages.binding.LanguageCodeAdapter;

/**
 * A Language with a ISO 639-3 language code.
 * 
 */
@XmlJavaTypeAdapter(LanguageCodeAdapter.class)
public class LanguageCode  implements Serializable {

    static final Map<String, LanguageCode> KNOWN;

    static {
        Map<String, LanguageCode> temp = new HashMap<>();
        try (InputStream inputStream = LanguageCode.class.getResourceAsStream("/iso-639-3_Code_Tables_20230123/iso-639-3_20230123.tab");
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            inputStreamReader.readLine(); // skipheader;
            String line = inputStreamReader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                LanguageCode found = new LanguageCode(
                    split[0],
                    split[1].length() > 0 ? split[1] : null,
                    split[2].length() > 0 ? split[2] : null,
                    split[3].length() > 0 ? split[3] : null,
                    Scope.valueOf(split[4]),
                    Type.valueOf(split[5]),
                    split[6],
                    split.length == 8 ? split[7] : null);
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


    private LanguageCode(
        String id,
        String part2B,
        String part2T,
        String part1,
        Scope scope,
        Type languageType,
        String refName,
        String comment) {
        this.id = id;
        this.part2B = part2B;
        this.part2T = part2T;
        this.part1 = part1;
        this.scope = scope;
        this.languageType = languageType;
        this.refName = refName;
        this.comment = comment;
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
     * Retrieves a {@link LanguageCode} by its three-letter identifier {@link #getId()}, or by its two letter identifier {@link #getPart1()}.
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link LanguageCode} if found.
     * @see #getCode()
     * @since 0.2
     */
    public static Optional<LanguageCode> get(String code) {
        if (code.length() == 2) {
            return getByPart1(code);
        } else {
            return getByCode(code);
        }
    }


    /**
     * Retrieves a {@link LanguageCode} by its three-letter identifier {@link #getId()}
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link LanguageCode} if found.
     */
    public static Optional<LanguageCode> getByCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(KNOWN.get(code.toLowerCase()));
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
    @javax.xml.bind.annotation.XmlValue
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
    
    private Object readResolve() {
        return get(id).orElseThrow(() -> new IllegalArgumentException("Unknown language code " + id));
    }

 
}
