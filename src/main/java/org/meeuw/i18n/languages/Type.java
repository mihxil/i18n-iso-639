package org.meeuw.i18n.languages;


/**
 * The 'type' of the language as defined in ISO-639-3
 */
public enum Type {
    /**
     * Ancient language
     */
    A("ancient"),
    /**
     * Constructed language
     */
    C("constructed"),
    /**
     * Extinct language
     */
    E("extinct"),
    /**
     * Historical language
     */
    H("historical"),
    /**
     * Living language
     */
    L("living"),
    /**
     * Special language
     */
    S("special");

  
    private final String toString;

    Type(String toString) {
        this.toString = toString;
    }

    @Override
    public String toString() {
        return toString;
    }
    
}
