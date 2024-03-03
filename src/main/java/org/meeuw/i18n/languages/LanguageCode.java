package org.meeuw.i18n.languages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import static org.meeuw.i18n.languages.ISO_639_3_Code.KNOWN;
import static org.meeuw.i18n.languages.ISO_639_3_Code.LOGGER;

/**
 * A language with a ISO 639-3 language code (of three letters). Also, aware of the ISO-630-1 2 letter codes if that exist.
 *<p>
 * Annotated with {@link XmlJavaTypeAdapter}, so it will automatically be marshalled and unmarshalled in XML's. 
 * <p>
 * Also annotated with jackson annotation, to be marshalled and unmarshalled in JSON as the code.
 *<p>
 * Implementations are immutable and can be used as a key in maps.
 */
public interface LanguageCode extends ISO_639_Code {
    
    /**
     * A stream with all known {@link ISO_639_Code language codes}.
     * 
     *
     * @return a stream of all known language codes.
     */
    static Stream<@NonNull LanguageCode> stream() {
        return ISO_639_3_Code
            .stream()
            .sorted(Comparator.comparing(LanguageCode::code))
            .map(LanguageCode::updateToEnum);
    }
    
    
    /**
     * A stream with {@link Map.Entry map entries} with all known language names. Combined with their {@link ISO_639_Code}
     * This means that the same language may occur more than once in this stream. For example Dutch will occur as both "Dutch" and as "Flemish".
     *
     * @param locale The locale to use for the names. Currently, must be english.
     * @see #streamByNames()
     * @since 3.0
     */
    static Stream<? extends Map.Entry<String, ? extends LanguageCode>> streamByNames(Locale locale) {
        if (! locale.getLanguage().equals("en")) {
            throw new UnsupportedOperationException("Only English is supported");
        }
        return ISO_639_3_Code
            .stream()
            .flatMap(l ->
                l.names().stream()
                    .map(n -> new AbstractMap.SimpleEntry<>(
                        n.inverted(), 
                        LanguageCode.updateToEnum(l)
                        )
                    )
            )             
            .sorted(Map.Entry.comparingByKey());
    }

    /**
     * Defaulting version of {@link #streamByNames(Locale)}, using {@link Locale#US}.
     * @since 3.0
     */
    static Stream<? extends Map.Entry<String, ? extends LanguageCode>> streamByNames() {
        return streamByNames(Locale.US);
    }

    

    
    /**
     * Retrieves a {@link ISO_639_3_Code} by on of its three-letter identifiers {@link #getByPart3(String)}, {@link #getByPart2B(String)}, or {@link #getByPart2T(String)}  or its two letter identifier {@link #part1()}.
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
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
            Optional<LanguageCode> byPart3 = getByPart3(code, matchRetired);
            if (byPart3.isPresent()) {
                return byPart3;
            } else {
                Optional<LanguageCode> byPart2B = getByPart2B(code);
                if (byPart2B.isPresent()) {
                    return byPart2B;
                } else {
                    return getByPart2T(code);
                }
            }
        }
    }
    
    static Optional<LanguageCode> get(String code) {
        return get(code, true);
    }

    /**
     * As {@link #get(String)}, but throws an {@link IllegalArgumentException} if not found.
     *
     * @return The {@link LanguageCode} if found
     * @throws IllegalArgumentException if not found
     */
    @JsonCreator
    static LanguageCode languageCode(String code) {
        return get(code)
            .orElseThrow(() -> new IllegalArgumentException("Unknown language code " + code));
    }


    /**
     * Retrieves a {@link ISO_639_3_Code} by its three-letter identifier {@link #getByPart3(String, boolean)} ()}
     *
     * If the given code is a {@link RetiredLanguageCode retired code}, the replacement code is returned if possible. If a retired code is matched, but no single replacement is found, an empty optional is returned, and a warning is logged (using {@link java.util.logging JUL})
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
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
     * @see #getByPart3(String) 
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
     * Retrieves a {@link ISO_639_3_Code} by its Part1 code {@link #part1()}
     *
     * @param code A 2 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     */
    
    static Optional<LanguageCode> getByPart1(String code) {
        return  ISO_639_3_Code
            .getByPart1(code)
            .map(LanguageCode::updateToEnum);
    }
    
    @NonNull
    static LanguageCode updateToEnum(@NonNull LanguageCode languageCode) {
        if (! (languageCode instanceof ISO_639_1_Code) && languageCode.part1() != null) {
            return ISO_639_1_Code.valueOf(languageCode.part1());
        } else {
            return languageCode;
        }
    }

    /**
     * Retrieves a {@link ISO_639_3_Code} by its Part2B  ('bibliographic') code {@link #part2B()}
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
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
     * Retrieves a {@link ISO_639_3_Code} by its Part2T ('terminology') code {@link #part2T()}
     *
     * @param code A 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
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
     * The {@link LanguageCode#part1() ISO-639-1-code} if available, otherwise the {@link LanguageCode#part3() ISO-639-3 code}.
     *
     * @return A 2 or 3 letter language code
     * @since 0.2
     */
    @JsonValue
    @Override
    String code();


    /**
     * @deprecated use {@link #code()}
     */
    @Deprecated
    default String getCode() {
        return code();
    }
    
    /**
     * The three-letter 639-3 identifier
     */
    String part3();

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

    
    default Locale toLocale() {
        return new Locale(code());
    }
    
 
    
    default Name name(Locale locale) {
        if (locale.getLanguage().equals("en")) {
            return names().get(0);
        } else {
            throw new UnsupportedOperationException();
        }
    }

}
