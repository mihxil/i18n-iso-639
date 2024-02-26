package org.meeuw.i18n.languages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;
import static org.meeuw.i18n.languages.LanguageCodeImpl.KNOWN;
import static org.meeuw.i18n.languages.LanguageCodeImpl.LOGGER;
import org.meeuw.i18n.languages.jaxb.LanguageCodeAdapter;

/**
 * A language with a ISO 639-3 language code (of three letters). Also, aware of the ISO-630-1 2 letter codes if they exist.
 *<p>
 * Annotated with {@link XmlJavaTypeAdapter}, so it will automatically be marshalled and unmarshalled in XML's. 
 * <p>
 * Also annotated with jackson annotation, to be marshalled and unmarshalled in JSON as the code.
 *<p>
 * This class is immutable and can be used as a key in maps.
 */
@XmlJavaTypeAdapter(LanguageCodeAdapter.class)
public interface LanguageCode extends Serializable {

    /**
     * A stream with all known {@link LanguageCodeImpl}
     *
     * @return a stream of all known language codes.
     */
    static Stream<LanguageCode> stream() {
        return KNOWN.values()
            .stream()
            .map(LanguageCode::updateToEnum)
            .sorted(Comparator.comparing(LanguageCode::getInvertedName));

    }

    /**
     * Retrieves a {@link LanguageCodeImpl} by its three-letter identifier {@link #id()} (using {@link #getByCode(String)}, or by its two letter identifier {@link #part1()}.
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link LanguageCodeImpl} if found.
     * @see #code()
     * @see #getByPart1(String)
     * @see #getByCode(String)
     * @see #languageCode(String)
     * @since 0.2
     */
    static Optional<LanguageCode> get(String code, boolean matchRetired) {
        if (code.length() == 2) {
            return getByPart1(code);
        } else {
            return getByPart3(code, matchRetired);
        }
    }
    
    static Optional<LanguageCode> get(String code) {
        return get(code, true);
    }

    /**
     * As {@link #get(String)}, but throws an {@link IllegalArgumentException} if not found.
     *
     * @return The {@link LanguageCodeImpl} if found
     * @throws IllegalArgumentException if not found
     */
    @JsonCreator
    static LanguageCode languageCode(String code) {
        return get(code)
            .orElseThrow(() -> new IllegalArgumentException("Unknown language code " + code));
    }


    /**
     * Retrieves a {@link LanguageCodeImpl} by its three-letter identifier {@link #id()}
     *
     * If the given code is a {@link RetiredLanguageCode retired code}, the replacement code is returned if possible. If a retired code is matched, but no single replacement is found, an empty optional is returned, and a warning is logged (using {@link java.util.logging JUL})
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link LanguageCodeImpl} if found.
     * @since 2.2
     */
    static Optional<LanguageCode> getByPart3(@Size(min = 3, max=3) String code, boolean matchRetired) {
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

        return Optional
            .ofNullable(prop)
            .map(LanguageCode::updateToEnum)
            ;
    }


    /**
     * Defaulting version of {@link #getByPart3(String, boolean)}, matching retired codes too.
     * @deprecated Confusing, since not matching like {@link #code()}
     * @see #getByPart3(String,) 
     */
    @Deprecated
    static Optional<LanguageCode> getByCode(@Size(min = 3, max=3) String code) {
        return getByPart3(code);
    }
    
    /**
     * Defaulting version of {@link #getByPart3(String, boolean)}, matching retired codes too.
     */
    static Optional<LanguageCode> getByPart3(@Size(min = 3, max=3) String code) {
        return getByPart3(code, true);
    }

    
    /**
     * Retrieves a {@link LanguageCodeImpl} by its Part1 code {@link #part1()}
     *
     * @param code A 2 letter language code
     * @return An optional containing the {@link LanguageCodeImpl} if found.
     */
    static Optional<LanguageCode> getByPart1(String code) {
        return getByPart1(code, true);
    }
    
    static Optional<LanguageCode> getByPart1(String code, boolean resolveToEnum) {
        if (code == null) {
            return Optional.empty();
        }
        final String lowerCode = code.toLowerCase();
        Optional<LanguageCode> result =  KNOWN.values().stream()
            .filter(i -> lowerCode.equals(i.part1()))
            .findFirst();
        if (resolveToEnum) {
            result = result.map(LanguageCode::updateToEnum);
        }
        return result;
    }
    
    static LanguageCode updateToEnum(LanguageCode languageCode) {
        if (! (languageCode instanceof ISO_639_1) && languageCode.part1() != null) {
            return ISO_639_1.valueOf(languageCode.part1());
        } else {
            return languageCode;
        }
    }

    /**
     * Retrieves a {@link LanguageCodeImpl} by its Part2B  ('bibliographic') code {@link #part2B()}
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link LanguageCodeImpl} if found.
     */
    static Optional<LanguageCode> getByPart2B(String code) {
        if (code == null) {
            return Optional.empty();
        }
        final String lowerCode = code.toLowerCase();
        return stream()
            .filter(i -> lowerCode.equals(i.part2B()))
            .map(LanguageCode::updateToEnum)
            .findFirst();
    }


    /**
     * Retrieves a {@link LanguageCodeImpl} by its Part2T ('terminology') code {@link #part2T()}
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link LanguageCodeImpl} if found.
     */
    static Optional<LanguageCode> getByPart2T(String code) {
        if (code == null) {
            return Optional.empty();
        }
        final String lowerCode = code.toLowerCase();
        return KNOWN.values().stream()
            .filter(i -> lowerCode.equals(i.part2T()))
            .map(LanguageCode::updateToEnum)
            .findFirst();
    }



    /**
     * The {@link #part1() ISO-639-1-code} if available, otherwise the {@link #part3() ISO-639-3 code}.
     *
     * @return A 2 or 3 letter language code
     * @since 0.2
     */
    @JsonValue
    String code();
        

    @Override
    String toString();
        

    /**
     * The three-letter 639-3 identifier
     * @return The three-letter 639-3 identifier
     */
    String id();

    
    /**
     * Synonym for {@link #id()}.
     * @return The three-letter 639-3 identifier
     */
    default String part3() {
        return id();
    }

    /**
     * Equivalent 639-2 identifier of the bibliographic applications
     * code set, if there is one
     * @return bibliographic id or {@code null}
     */
    String part2B();

    /**
     * Equivalent 639-2 identifier of the terminology applications code
     * set, if there is one
     * @return terminology id or {@code null}
     */
    String part2T();

    /**
     * Equivalent 639-1 identifier, if there is one
     * @return 2 letter id or {@code null}
     */
    String part1();
    
    Scope scope();

    Type languageType();

    String refName();

    String comment();

    /**
     * @since 2.2
     */
    List<Name> names();

    /**
     * The (first) name (in english) of the language.
     * @deprecated 
     */
    @Deprecated
    default String getName() {
        return names().get(0).value();
    }

    @Deprecated
    default String getInvertedName() {
        return names().get(0).inverted();
    }
    
    default Locale toLocale() {
        return new Locale(code());
    }

 //   @Override
    default int compareTo(LanguageCode o) {
        return getInvertedName().compareTo(o.getInvertedName());
    }

    private Object readResolve() {
        return get(id()).orElse(this);
    }


}
