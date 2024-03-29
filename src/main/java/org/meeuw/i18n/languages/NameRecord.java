package org.meeuw.i18n.languages;

/**
 * A 'print' and 'inverted' name collected in a record.
 * <p>
 * The 'inverted' version of a name can be used for alphabetic sorting. It implies that qualifiers like 'eastern' or 'southern' are postfixed.
 * 
 * @since 3.1
 */
public class NameRecord {
    
    private final String print;
    private final String inverted;

    public NameRecord(String print, String inverted) {
        this.print = print;
        this.inverted = inverted;
    }
    
    public NameRecord(String value) {
        this(value, value);
    }


    public String print() {
        return print;
    }
    
    /**
     * Sometimes the name of the language starts with something like 'eastern' or so. This returns the name with these prefixes postfixed, so this is the natural name to <em>sort</em> languages on.
     */
    public String inverted() {
        return inverted;
    }
    
    @Override
    public String toString() {
        return print;
    }
}
