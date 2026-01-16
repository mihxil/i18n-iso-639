package org.meeuw.i18n.languages.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import org.meeuw.i18n.languages.Scope;
import org.meeuw.i18n.languages.Type;
import org.meeuw.i18n.languages.validation.impl.LanguageValidator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A validator for language codes (or for {@link java.util.Locale}s, which are also containers for language codes)
 *
 * @author Michiel Meeuwissen
 * @since 2.2
 */

@Target({FIELD, METHOD, TYPE_PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = LanguageValidator.class)
@Documented
public @interface Language {

    String message() default "{org.meeuw.i18n.languages.validation.language.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /***
     * If the language is  not directly recognized, we'll check if the JVM can produce a display language for it.
     */
    boolean lenient() default false;


    Type[] type() default {};

    Scope[] scope() default { };

    /**
     * The default is to accept both ISO-639-1 and ISO-639-3 codes. If you want to restrict to ISO-639-1 only, set this to false.
     */
    boolean iso639_3() default true;

    boolean iso639_3_retired() default true;

    /**
     * The default is to accept also part 2 codes.
     */
    boolean iso639_2() default true;


     /**
     * The default is not to accept also part 5 codes (language families)
     */
    boolean iso639_5() default false;


    boolean requireLowerCase() default true;

    /**
     * xml:lang uses '-' between language and country. In XML language codes are case-insensitive, so
     * you may want to set {@link #requireLowerCase()} to false too.
     */
    boolean forXml() default true;


    /**
     * Whether the locale may contain a country.
     * <p>
     * the country itself is not validated. Use {@code @org.meeuw.i18n.countries.validation.ValidCountry} for that, which is available in
     * <a href="https://www.javadoc.io/static/org.meeuw.i18n/i18n-regions-countries/3.0.0/org.meeuw.i18n.countries/org/meeuw/i18n/countries/validation/ValidCountry.html">org.meeuw.i18n:i18n-regions-countries</a>.
     */
    boolean mayContainCountry() default true;

    /**
     * Whether the locale may contain a variant
     */
    boolean mayContainVariant() default false;

}

