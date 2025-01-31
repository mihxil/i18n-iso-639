package org.meeuw.i18n.languages;

public class LanguageNotFoundException extends IllegalArgumentException{
    final String code;
    public LanguageNotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }
    public LanguageNotFoundException(String code) {
        this(code, "Unknown language code '" + code + "'");
    }

    public String code() {
        return code;
    }
}
