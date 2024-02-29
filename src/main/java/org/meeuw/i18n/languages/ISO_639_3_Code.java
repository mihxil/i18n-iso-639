package org.meeuw.i18n.languages;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * A language with a ISO 639-3 language code (of three letters). Also, aware of the ISO-630-1 2 letter codes if they exist.
 *<p>
 * Annotated with {@link XmlJavaTypeAdapter}, so it will automatically be marshalled and unmarshalled in XML's. 
 * <p>
 * Also annotated with jackson annotation, to be marshalled and unmarshalled in JSON as the code.
 *<p>
 * This class is immutable and can be used as a key in maps.
 */
public class ISO_639_3_Code implements LanguageCode {

    final static Logger LOGGER = Logger.getLogger(LanguageCode.class.getName());


    static final Map<String, ISO_639_3_Code> KNOWN;

    static final String DIR = "/iso-639-3_Code_Tables_20240207/";
    static {
        Map<String, List<Name>> namesMap = new HashMap<>();
        try (InputStream inputStream = ISO_639_3_Code.class.getResourceAsStream(DIR + "iso-639-3_Name_Index.tab");
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            String line = inputStreamReader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                List<Name> names = namesMap.computeIfAbsent(split[0], k -> new ArrayList<>());
                names.add(new Name(split[1], split[2]));
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
                List<Name> names = namesMap.get(split[0]);
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
                temp.put(found.id().toLowerCase(), found);
                line = inputStreamReader.readLine();
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        KNOWN = Collections.unmodifiableMap(temp);
    }
    
     /**
     * A stream with all known {@link ISO_639_Code language codes}.
     * 
     *
     * @return a stream of all known language codes.
     */
     public static Stream<ISO_639_3_Code> stream() {
        return KNOWN.values()
            .stream()
            .sorted(Comparator.comparing(ISO_639_3_Code::code));
    }
    
    static Optional<ISO_639_3_Code> getByPart1(String code) {
        if (code == null) {
            return Optional.empty();
        }
        final String lowerCode = code.toLowerCase();
        return  ISO_639_3_Code.stream()
            .filter(i -> lowerCode.equals(i.part1()))
            .findFirst();
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

    private transient  final List<Name> names;

    private ISO_639_3_Code(
        String id,
        String part2B,
        String part2T,
        String part1,
        Scope scope,
        Type languageType,
        String refName,
        String comment,
        List<Name> names
    ) {
        this.id = id;
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
        return part1 != null ? part1 : id;
    }


    @Override
    public String toString() {
        return code() + " (" + refName + ")";
    }


    /**
     * The three-letter 639-3 identifier
     * @return The three-letter 639-3 identifier
     */
    public String id() {
        return id;
    }
    
    /**
     * Synonym for {@link #id()}.
     * @return The three-letter 639-3 identifier
     */
    public String part3() {
        return id();
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
    public List<Name> names() {
        return names;
    }
    
    @Size
    public Name name(Locale locale) {
        if (locale.getLanguage().equals("en")) {
            return names.get(0);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    
    private Object readResolve() {
        LanguageCode gotten = LanguageCode.get(id()).orElse(null);
        if (gotten == null) {
            return this;
        } else {
            return gotten;
        }
    }
    
}
