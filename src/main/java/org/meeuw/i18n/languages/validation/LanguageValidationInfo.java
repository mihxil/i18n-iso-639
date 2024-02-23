package org.meeuw.i18n.languages.validation;

import org.meeuw.i18n.languages.Scope;
import org.meeuw.i18n.languages.Type;


public class LanguageValidationInfo {
    private final  boolean lenient;
    private final Type[] type;
    private final Scope[] scope;
    private final boolean iso639_3;
    private final boolean iso639_3_retired;
    private final boolean iso639_2;
    private final boolean requireLowerCase;

    public LanguageValidationInfo(boolean lenient, Type[] type, Scope[] scope, boolean iso6393, boolean iso6393Retired, boolean iso6392, boolean requireLowerCase) {
        this.lenient = lenient;
        this.type = type;
        this.scope = scope;
        iso639_3 = iso6393;
        iso639_3_retired = iso6393Retired;
        iso639_2 = iso6392;
        this.requireLowerCase = requireLowerCase;
    }
    
    public static LanguageValidationInfo of(Language annotation) {
        return new LanguageValidationInfo(
            annotation.lenient(), 
            annotation.type(), 
            annotation.scope(),
            annotation.iso639_3(),
            annotation.iso639_3_retired(),
            annotation.iso639_2(),
            annotation.requireLowerCase());
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

    public boolean requireLowerCase() {
        return requireLowerCase;
    }
}
