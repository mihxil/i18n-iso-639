package org.meeuw.i18n.languages;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;

import jakarta.validation.constraints.Size;

import org.checkerframework.checker.nullness.qual.PolyNull;

import com.fasterxml.jackson.annotation.JsonCreator;

import static org.meeuw.i18n.languages.ISO_639_3_Code.KNOWN;
import static org.meeuw.i18n.languages.LanguageCode.NOTFOUND;

/**
 * A utility class for working with ISO 639 language codes.
 *
 * @since 3.1
 */
public class ISO_639 {

    static ThreadLocal<Boolean> ignoreNotFound = ThreadLocal.withInitial(() -> Boolean.FALSE);
    static ThreadLocal<BiFunction<String, Class<? extends ISO_639_Code>,  ISO_639_Code>> notFoundFallback = ThreadLocal.withInitial(() -> (s, c) -> NOTFOUND);

    /**
     * If a code is not found in {@link #iso639(String)}, do not throw {@link LanguageNotFoundException}, but return {@link LanguageCode#NOTFOUND}
     * @see #implicitUserDefine()
     */
    public static RemoveIgnoreNotFound setIgnoreNotFound() {
        ignoreNotFound.set(Boolean.TRUE);
        notFoundFallback.remove();
        return RemoveIgnoreNotFound.INSTANCE;
    }

    /**
     * If a code is not found in {@link #iso639(String)}, do not throw {@link LanguageNotFoundException}, but produce use given function
     * @param fallback What to produce in those cases
     * @since 3.8
     */
    public static RemoveIgnoreNotFound setIgnoreNotFound(Function<String, ISO_639_Code> fallback) {

        return setIgnoreNotFound((code, clazz) -> fallback.apply(code));
    }

    /**
     * @since 3.11
     */
    public static RemoveIgnoreNotFound setIgnoreNotFound(BiFunction<String, Class<? extends ISO_639_Code>, ISO_639_Code> fallback) {
        ignoreNotFound.set(Boolean.TRUE);
        notFoundFallback.set(fallback);
        return RemoveIgnoreNotFound.INSTANCE;
    }


    /**
     * If a code is not found in {@link #iso639(String)}, do not throw {@link LanguageNotFoundException}, but create {@link UserDefinedLanguage}
     * @since 3.8
     */
    public static RemoveIgnoreNotFound implicitUserDefine() {
        ignoreNotFound.set(true);
        notFoundFallback.set((c, clazz) -> new UserDefinedLanguage(c, null, c, "not found"));
        return RemoveIgnoreNotFound.INSTANCE;
    }


    public static void removeIgnoreNotFound() {
        ignoreNotFound.remove();
        notFoundFallback.remove();
    }

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
     * Retrieves a {@link ISO_639_3_Code} by  {@link ISO_639_3_Code#part3() its three-letter identifier}
     * <p>
     * If the given code is a {@link RetiredLanguageCode retired code}, the replacement code is returned if possible. If a retired code is matched, but no single replacement is found, an empty optional is returned, and a warning is logged (using {@link java.util.logging JUL})
     *
     * @param code A 2 or 3 letter language code
     * @param matchRetired Whether a {@link RetiredLanguageCode} result should be acceptable
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     * @since 2.2
     */
    public static Optional<LanguageCode> getByPart3(@Size(min = 3, max = 3) String code, boolean matchRetired) {

        return ISO_639_3_Code.getByPart3(code, matchRetired, Level.WARNING)
            .map(LanguageCode::updateToEnum)
            ;
    }

    /**
     * Defaulting version of {@link #getByPart3(String, boolean)}, matching retired codes too.
     * @param code A 2 or 3 letter language code
     * @return An optional containing the {@link ISO_639_3_Code} if found.
     */
    public static Optional<LanguageCode> getByPart3(@Size(min = 3, max = 3) String code) {
        return getByPart3(code, true);
    }

    /**
     * Retrieves a language family code by its 3-letter code.
     *
     * @param code A 3 letter language family code (according to ISO-639-5)
     * @return An optional containing the {@link LanguageFamilyCode} if found
     * @see LanguageFamilyCode#get(String)
     */
    public static Optional<LanguageFamilyCode> getByPart5(@Size(min = 3, max = 3) String code) {
        return LanguageFamilyCode.get(code);
    }

    /**
     * A stream with all known {@link ISO_639_Code language (or language family) codes} .
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

    /**
     * Registers a certain code as a fallback language or language group code. This is only valid for the current thread, until {@link #resetFallBacks()} is called.
     * <p>
     * The effect is that {@link #get(String)}  will return the registered {@link ISO_639_3_Code fallback code} if no real code is found.
     * <p>
     * If the given argument is an instance of {@link LanguageCode} it will also be registered as {@link LanguageCode#registerFallback(String, LanguageCode)}
     *
     * @param code The code to (temporary) recognize
     * @param exemption What it should fall back to
     * @see #setFallbacks(Map) To replace all current fallbacks with a map of these.
     * @see LanguageCode#registerFallback(String, LanguageCode)
     * @since 3.2
       */
    public static void registerFallback(String code, ISO_639_Code exemption) {
        FALLBACKS.get().put(code, exemption);
    }


    /**
     * Replaces all current (i.e. {@link ThreadLocal thread local}) fallbacks with a map of these.
     *
     * @see #registerFallback(String, ISO_639_Code)
     * @see #resetFallBacks()
     * @since 3.2
     */
    static void setFallbacks(Map<String, ISO_639_Code> exemptions) {
        FALLBACKS.set(exemptions);
    }

    /**
     * Returns the currently (i.e. {@link ThreadLocal thread local}) registered fallbacks (as an unmodifiable map).
     * @since 3.2
     * @see #registerFallback
     * @see #setFallbacks(Map)
     * @see LanguageCode#getFallBacks()
     * @see #resetFallBacks()
     * @return Unmodifiable map of registered fallbacks
     */
    public static Map<String, ISO_639_Code> getFallBacks() {
        return Collections.unmodifiableMap(FALLBACKS.get());
    }

    /**
     * Resets the fallbacks for the current thread. After this, no fallbacks will be effective anymore.
     * @since 3.2
     */
    public static void resetFallBacks() {
        FALLBACKS.remove();
    }

    /**
     * @return A language or language family by (one of their) code(s)
     * @param code The code
     * @see #get For a version that throws an exception if not found.
     * @since 3.11
     */
    @SuppressWarnings("unchecked")
    public static <L extends ISO_639_Code> Optional<L> get(String code, Class<L> clazz) {
        if (clazz.isAssignableFrom(LanguageCode.class)) {
            LanguageCode lc = LanguageCode.get(code).orElse(null);
            if (lc != null) {
                return Optional.of((L) lc);
            }
        }
        if (clazz.isAssignableFrom(LanguageFamilyCode.class)) {
            try {
                LanguageFamilyCode lc = LanguageFamilyCode.valueOf(code);
                return Optional.of((L) lc);
            } catch (IllegalArgumentException ignore) {
                // ignore
            }
        }
        ISO_639_Code o = FALLBACKS.get().get(code);
        if (clazz.isInstance(o)) {
            return Optional.of((L) o);
        }
        return Optional.empty();
    }

    public static Optional<ISO_639_Code> get(String code) {
        return get(code, ISO_639_Code.class);
    }


    /**
     * As {@link #get(String)}, but throws an {@link LanguageNotFoundException} if not found.
     *
     * @return The {@link ISO_639_3_Code} if found
     * @param code ISO-639 code to find
     * @throws LanguageNotFoundException if not found, unless {@link ISO_639#setIgnoreNotFound()} was set, in which case {@link LanguageCode#NOTFOUND}
     * @see #setIgnoreNotFound()
     * @see #setIgnoreNotFound(Function)
     * @see #implicitUserDefine()
     */
    public static ISO_639_Code iso639(String code) {
        return iso639(code, ISO_639_Code.class);
    }

    /**
     * @since 3.11
     */
    public static <L extends ISO_639_Code> L iso639(String code, Class<L> clazz) {
        if (ignoreNotFound.get()) {
            return get(code, clazz).orElseGet(() -> (L) notFoundFallback.get().apply(code, clazz));
        } else {
            return get(code, clazz).orElseThrow(() -> new LanguageNotFoundException(code));
        }
    }



    /**
     * @return As {@link #iso639(String)}, but  it returns {@code null} if the argument is {@code null} or the empty string
     * @param code ISO-639 code
     * @since 3.3
     */
    @JsonCreator
    public static @PolyNull ISO_639_Code lenientIso639(@PolyNull String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return iso639(code);
    }

    public static class  RemoveIgnoreNotFound implements AutoCloseable {

        public static final RemoveIgnoreNotFound INSTANCE = new RemoveIgnoreNotFound();
        private RemoveIgnoreNotFound() {

        }
        @Override
        public void close() {
            removeIgnoreNotFound();
        }
    }

}
