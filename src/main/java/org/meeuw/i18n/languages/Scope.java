package org.meeuw.i18n.languages;

/**
 * The 'scope' of the language as defined in ISO-639-3.
 * <p>
 * Or in the case of {@link #FAMILY} in it means that this code indicates a language family of 
 * ISO-639-5.
 */
public enum Scope {

    /**
     * Individual languages as defined by ISO 639-3
     */
    I("individual"),
    /**
     * MacroLanguage
     */
    M("macrolanguage"),
    /**
     * Special
     */
    S("special language"),
    
    
    /**
     * ISO-639-5
     */
    FAMILY("language family")
    ;

    private final String string;

    Scope(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

}
