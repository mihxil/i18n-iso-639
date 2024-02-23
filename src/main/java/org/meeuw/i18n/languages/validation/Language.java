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
 * A validator for language codes (or {@link java.util.Locale}s (which also is a container for language codes)
 * <p>  
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

    Scope[] scope() default {};

    /**
     * The default is to accept both ISO-639-1 and ISO-639-3 codes. If you want to restrict to ISO-639-1 only, set this to false.
     */
    boolean iso639_3() default true;

    boolean iso639_3_retired() default true;

    /**
     * The default is to accept also part 2 codes.
     */
    boolean iso639_2() default true;

    boolean requireLowerCase() default true;
    
    boolean forXml() default false;
    
    boolean mayContainCountry() default false;
    
    boolean mayContainVariant() default false;

}

