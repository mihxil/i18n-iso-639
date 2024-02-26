package org.meeuw.i18n.languages;

public class Name {
    

    private final String value;
    private final String inverted;

    public Name(String value, String inverted) {
        this.value = value;
        this.inverted = inverted;
    }


    public String value() {
        return value;
    }
    
    /**
     * Sometimes the name of the language starts with something like 'eastern' or so. This returns the name with these prefixes postfixed, so this is the natural name to <em>sort</em> languages on.
     */
    public String inverted() {
        return inverted;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
