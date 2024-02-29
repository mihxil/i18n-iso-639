package org.meeuw.i18n.languages;

/**
 * The 'scope' of the language as defined in ISO-639-3.
 * <p>
 * Or in the case of {@link #FAMILY} in it means that this code indicates a language family.
 */
public enum Scope {

    /**
     * Individual languages as defined by ISO 639-3
     */
    I("Individual"),
    /**
     * MacroLanguage
     */
    M("Macrolanguage"),
    /**
     * Special
     */
    S("Special language"),
    
    
    /**
     * ISO-639-5
     */
    FAMILY("Language family")
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
