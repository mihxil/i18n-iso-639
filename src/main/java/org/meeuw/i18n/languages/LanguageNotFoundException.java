package org.meeuw.i18n.languages;

/**
 * The {@link IllegalArgumentException} thrown if a language is not found by code.
 * @since 3.8
 */
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
