package org.meeuw.i18n.languages.validation;

import org.meeuw.i18n.languages.Scope;
import org.meeuw.i18n.languages.Type;
import org.meeuw.i18n.languages.validation.impl.LanguageValidator;


/**
 * This class is used to pass the validation information from the Language annotation to the validation code.
 * It is used to pass the information from the annotation to the validation code.
 * <p>
 * Like a {@code record}, it will be simple to convert, if we drop java 11 support.
 * <p>
 *  I can be created using {@link #of(Language)}, and then
 * </p>
 */
public class LanguageValidationInfo {
    private final  boolean lenient;
    private final Type[] type;
    private final Scope[] scope;
    private final boolean iso639_3;
    private final boolean iso639_3_retired;
    private final boolean iso639_2;
    private final boolean iso639_5;
    private final boolean requireLowerCase;
    private final boolean forXml;
    private final boolean mayContainCountry;
    private final boolean mayContainVariant;

    private LanguageValidationInfo(
        boolean lenient,
        Type[] type,
        Scope[] scope,
        boolean iso6393,
        boolean iso6393Retired,
        boolean iso6392,
        boolean iso6395,
        boolean requireLowerCase,
        boolean forXml,
        boolean mayContainCountry,
        boolean mayContainVariant) {
        this.lenient = lenient;
        this.type = type;
        this.scope = scope;
        iso639_3 = iso6393;
        iso639_3_retired = iso6393Retired;
        iso639_2 = iso6392;
        iso639_5 = iso6395;
        this.requireLowerCase = requireLowerCase;
        this.forXml = forXml;
        this.mayContainCountry = mayContainCountry;
        this.mayContainVariant = mayContainVariant;
    }

    public static LanguageValidationInfo of(Language annotation) {
        return new LanguageValidationInfo(
            annotation.lenient(),
            annotation.type(),
            annotation.scope(),
            annotation.iso639_3(),
            annotation.iso639_3_retired(),
            annotation.iso639_2(),
            annotation.iso639_5(),
            annotation.requireLowerCase(),
            annotation.forXml(),
            annotation.mayContainCountry(),
            annotation.mayContainVariant()
        );
    }


    public boolean lenient()  {
      return lenient;
    }


    public Type[] type() {
        return type;
    }

    public Scope[] scope() {
        return scope;
    }

    public boolean iso639_3()  {
        return iso639_3;
    }

    public boolean iso639_3_retired()  {
        return iso639_3_retired;
    }


    public boolean iso639_2()  {
        return iso639_2;
    }

    public boolean iso639_5()  {
        return iso639_5;
    }

    public boolean requireLowerCase() {
        return requireLowerCase;
    }

    /**
     * Whether the language code should be suitable for {@code xml:lang} attributes.
     */
    public boolean forXml() {
        return forXml;
    }

    public boolean mayContainCountry() {
        return mayContainCountry;
    }

    public boolean mayContainVariant() {
        return mayContainVariant;
    }

    public boolean isValid(Object object){
        return LanguageValidator.isValid(this, object);
    }
}
