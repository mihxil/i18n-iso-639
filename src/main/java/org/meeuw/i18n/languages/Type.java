package org.meeuw.i18n.languages;


/**
 * The 'type' of the language as defined in ISO-639-3
 */
public enum Type {
    /**
     * Ancient language
     */
    A("Ancient language"),
    /**
     * Constructed language
     */
    C("Constructed language"),
    /**
     * Extinct language
     */
    E("Extinct language"),
    /**
     * Historical language
     */
    H("Historical language"),
    /**
     * Living language
     */
    L("Living language"),
    /**
     * Special language
     */
    S("Special language");

  
    private final String toString;

    Type(String toString) {
        this.toString = toString;
    }

    @Override
    public String toString() {
        return toString;
    }
    
}
