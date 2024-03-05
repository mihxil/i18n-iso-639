package org.meeuw.i18n.languages;

import com.fasterxml.jackson.annotation.JsonCreator;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Stream;
import static org.meeuw.i18n.languages.ISO_639_3_Code.KNOWN;

/**
 * A utility class for working with ISO 639 language codes.
 * 
 * @since 3.1
 */
public class ISO_639 {
    
    private ISO_639() {
    }
    /**
     * Retrieves a {@link ISO_639_3_Code} by its Part1 code {@link ISO_639_3_Code#part1()}
     *
     * @param code A 2 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     */
    
    public static Optional<LanguageCode> getByPart1(String code) {
        return  ISO_639_3_Code
            .getByPart1(code)
            .map(LanguageCode::updateToEnum);
    }

    /**
     * Retrieves a {@link LanguageCode} by its Part2B  ('bibliographic') code {@link ISO_639_3_Code#part2B()}
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     */
    public static Optional<LanguageCode> getByPart2B(String code) {
        if (code == null) {
            return Optional.empty();
        }
        final String lowerCode = code.toLowerCase();
        return ISO_639_3_Code.stream()
            .filter(i -> lowerCode.equals(i.part2B()))
            .map(LanguageCode::updateToEnum)
            .findFirst();
    }

    /**
     * Retrieves a {@link ISO_639_3_Code} by its Part2T ('terminology') code {@link ISO_639_3_Code#part2T()}
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     */
    public static Optional<LanguageCode> getByPart2T(String code) {
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
     * Retrieves a {@link ISO_639_3_Code} by its three-letter identifier {@link #getByPart3(String, boolean)} ()}
     * <p>
     * If the given code is a {@link RetiredLanguageCode retired code}, the replacement code is returned if possible. If a retired code is matched, but no single replacement is found, an empty optional is returned, and a warning is logged (using {@link java.util.logging JUL})
     *
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     * @since 2.2
     */
    public static Optional<LanguageCode> getByPart3(@Size(min = 3, max = 3) String code, boolean matchRetired) {
        
        return ISO_639_3_Code.getByPart3(code, matchRetired)
            .map(LanguageCode::updateToEnum)
            ;
    }

    /**
     * Defaulting version of {@link #getByPart3(String, boolean)}, matching retired codes too.
     */
    public static Optional<LanguageCode> getByPart3(@Size(min = 3, max = 3) String code) {
        return getByPart3(code, true);
    }

    /**
     * Retrieves a language family code by its 3 letter code.
     * 
     * @see LanguageFamilyCode#get(String) 
     */
    public static Optional<LanguageFamilyCode> getByPart5(@Size(min = 3, max = 3) String code) {
        
        return LanguageFamilyCode.get(code);
    }

    /**
     * A stream with all known {@link ISO_639_Code language (or language family) codes} .
     * 
     *
     * @return a stream of all known language codes.
     */
    public static Stream<ISO_639_Code> stream() {
        return Stream.concat(
            LanguageCode.stream(),
            Arrays.stream(LanguageFamilyCode.values())
        );
    }

    public static Stream<Map.Entry<String, ? extends ISO_639_Code>> streamByNames() {
        return Stream.concat(
            LanguageCode.streamByNames(),
            Arrays.stream(LanguageFamilyCode.values())
                .map(l -> new AbstractMap.SimpleEntry<>(l.refName(), l))
        );
    }


    private static final ThreadLocal<Map<String, ISO_639_Code>> FALLBACKS = ThreadLocal.withInitial(HashMap::new);

    public static void registerFallback(String code, ISO_639_Code exemption) {
        FALLBACKS.get().put(code, exemption);
    }

    public static Map<String, ISO_639_Code> getFallBacks() {
        return FALLBACKS.get();
    }
    
    public static void resetFallBacks() {
        FALLBACKS.remove();
    }

    /**
     * Obtains a language or language family by (one of their) code(s).
     * 
     * @see #get For a version that throws an exception if not found. 
     */
    public static Optional<ISO_639_Code> get(String code) {
        ISO_639_Code lc = LanguageCode.get(code).orElse(null);
        if (lc == null) {
            try {
                return Optional.of(LanguageFamilyCode.valueOf(code));
            } catch (IllegalArgumentException iae) {
                return Optional.ofNullable(FALLBACKS.get().get(code));
            }
        } else {
            return Optional.of(lc);
        }
    }

    /**
     * As {@link #get(String)}, but throws an {@link IllegalArgumentException} if not found.
     *
     * @return The {@link ISO_639_3_Code} if found
     * @throws IllegalArgumentException if not found
     */
    @JsonCreator
    public static ISO_639_Code iso639(String code) {
        return get(code)
            .orElseThrow(() -> new IllegalArgumentException("Unknown language code " + code));
    }
}
