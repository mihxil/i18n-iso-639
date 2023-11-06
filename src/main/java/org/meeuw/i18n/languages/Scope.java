package org.meeuw.i18n.languages;

/**
 * The 'scope' of the language as defined in ISO-639-3
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
    S("Special language");

    private final String string;

    Scope(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

}
