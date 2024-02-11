package org.meeuw.i18n.languages;

/**
 * The 'reason' for retirement of a code.
 * @see RetiredLanguageCode
 */
public enum RetirementReason {

    /**
     * 'change' The code has just changed. E.g. fri -> fry
     */
    C("change"),

    /**
     * 'duplicate'. This code is a duplicate of another code. E.g. bgh was a duplicate of bbh
     */
    D("duplicate"),

    /**
     * 'non-existent'. It was established that the language probably never existed.
     * There is no replacement.
     */
    N("non-existent"),

    /**
     * 'split'. The language is now considered to be two or more languages.
     * This cannot be automatically converted to the new code (there are more).
     */
    S("split"),

     /**
     * 'merge'. The language is not considered a separate language anymore, and its code is merged with another. E.g. the Souletin dialect of Basque is now considered to be just Basque ('eus').
     */
    M("merge");

    private final String string;

    RetirementReason(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
