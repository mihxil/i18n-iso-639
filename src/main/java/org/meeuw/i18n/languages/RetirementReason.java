package org.meeuw.i18n.languages;

public enum RetirementReason {

    C ("change"),
    D ("duplicate"),
    N ("non-existent"),
    S ("split"),
    M ("merge");

    private final String string;

    RetirementReason(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
