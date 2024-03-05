package org.meeuw.i18n.languages;

import java.util.*;
import java.util.stream.Stream;

import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.i18n.languages.jaxb.LanguageCodeAdapter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A language with a ISO 639-3 language code (of three letters). Also, aware of the ISO-630-1 2 letter codes if that exist.
 *<p>
 * Annotated with {@link XmlJavaTypeAdapter}, so it will automatically be marshalled and unmarshalled in XML's.
 * <p>
 * Also annotated with jackson annotation, to be marshalled and unmarshalled in JSON as the code.
 *<p>
 * Implementations are immutable and can be used as a key in maps.
 */
@XmlJavaTypeAdapter(LanguageCodeAdapter.class)
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
            .map(LanguageCode::updateToEnum)
            .sorted(Comparator.comparing(LanguageCode::code));
    }


    /**
     * A stream with {@link Map.Entry map entries} with all known language names. Combined with their {@link ISO_639_Code}
     * This means that the same language may occur more than once in this stream. For example Dutch will occur as both "Dutch" and as "Flemish".
     *
     * @param locale The locale to use for the names. Currently, must be english.
     * @see ISO_639#streamByNames()
     * @since 3.0
     */
    static Stream<? extends Map.Entry<String, ? extends LanguageCode>> streamByNames(Locale locale) {
        if (! locale.getLanguage().equals("en")) {
            throw new UnsupportedOperationException("Only English is supported");
        }
        return ISO_639_3_Code
            .stream()
            .flatMap(l ->
                l.nameRecords().stream()
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
         * Retrieves a {@link ISO_639_3_Code} by on of its three-letter identifiers {@link ISO_639#getByPart3(String)}, {@link ISO_639#getByPart2B(String)}, or {@link ISO_639#getByPart2T(String)}  or its two letter identifier {@link #part1()}.
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     * @see #code()
     * @see ISO_639#getByPart1(String)
     * @see #getByCode(String)
     * @see #languageCode(String)
     * @since 0.2
     */
    static Optional<LanguageCode> get(String code, boolean matchRetired) {
        if (code.length() == 2) {
            return getByPart1(code);
        } else {
            Optional<LanguageCode> byPart3 = ISO_639.getByPart3(code, matchRetired);
            if (byPart3.isPresent()) {
                return byPart3;
            } else {
                Optional<LanguageCode> byPart2B = ISO_639.getByPart2B(code);
                if (byPart2B.isPresent()) {
                    return byPart2B;
                } else {
                    return ISO_639.getByPart2T(code);
                }
            }
        }
    }

    static Optional<LanguageCode> get(String code) {
        return get(code, true);
    }

    /**
     * As {@link ISO_639#get(String)}, but throws an {@link IllegalArgumentException} if not found.
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
     * Defaulting version of {@link ISO_639#getByPart3(String, boolean)}, matching retired codes too.
     * @deprecated Confusing, since not matching like {@link #code()}
     * @see ISO_639#getByPart3(String)
     */
    @Deprecated
    static Optional<LanguageCode> getByCode(@Size(min = 3, max=3) String code) {
        return ISO_639.getByPart3(code);
    }




    /**
     * Retrieves a {@link ISO_639_3_Code} by its Part1 code {@link #part1()}
     *
     * @param code A 2 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     *
     */
    @Deprecated
    static Optional<LanguageCode> getByPart1(String code) {
        return ISO_639.getByPart1(code);
    }


    /**
     * Retrieves a {@link LanguageCode} by its Part2B  ('bibliographic') code {@link ISO_639_3_Code#part2B()}
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     *
     */
    @Deprecated
    static Optional<LanguageCode> getByPart2B(String code) {
        return ISO_639.getByPart2B(code);
    }

    /**
     * Retrieves a {@link ISO_639_3_Code} by its Part2T ('terminology') code {@link ISO_639_3_Code#part2T()}
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     */
    @Deprecated
    static Optional<LanguageCode> getByPart2T(String code) {
        return ISO_639.getByPart2T(code);
    }

    /**
     * Retrieves a {@link ISO_639_3_Code} by its three-letter identifier {@link #getByPart3(String, boolean)} ()}
     * <p>
     * If the given code is a {@link RetiredLanguageCode retired code}, the replacement code is returned if possible. If a retired code is matched, but no single replacement is found, an empty optional is returned, and a warning is logged (using {@link java.util.logging JUL})
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     * @since 2.2
     */
    @Deprecated
    static Optional<LanguageCode> getByPart3(@Size(min = 3, max = 3) String code, boolean matchRetired) {

        return ISO_639.getByPart3(code, matchRetired);
    }

    /**
     * Defaulting version of {@link #getByPart3(String, boolean)}, matching retired codes too.
     */
    @Deprecated
    static Optional<LanguageCode> getByPart3(@Size(min = 3, max = 3) String code) {
        return getByPart3(code, true);
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
    List<NameRecord> nameRecords();

    default Locale toLocale() {
        return new Locale(code());
    }

    default NameRecord nameRecord(Locale locale) {
        if (locale.getLanguage().equals("en")) {
            return nameRecords().get(0);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * The macro language(s) of which this language is a part.
     * @return a list of macro languages, or an empty list if this language is not known to be a part of a macro language.
     */
    List<LanguageCode> macroLanguages();

    /**
     * If this is a {@link Scope#M macro language}, the known individual languages which are part of this macro language.
     */
    List<LanguageCode> individualLanguages();


}
