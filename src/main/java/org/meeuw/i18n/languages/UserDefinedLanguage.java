package org.meeuw.i18n.languages;

import java.util.List;

/**
 * @since 3.4
 */
public class UserDefinedLanguage implements LanguageCode {

    private final String code;
    private final Type type;
    private final String refName;
    private final String comment;

    public UserDefinedLanguage(String code, Type type, String refName, String comment) {
        this.code = code;
        this.type = type;
        this.refName = refName;
        this.comment = comment;
    }


    @Override
    public String code() {
        return code;
    }

    @Override
    public String part3() {
        return null;
    }

    @Override
    public String part2B() {
        return null;
    }

    @Override
    public String part2T() {
        return null;
    }

    @Override
    public String part1() {
        return null;
    }

    @Override
    public Scope scope() {
        return null;
    }

    @Override
    public Type languageType() {
        return type;
    }

    @Override
    public String refName() {
        return refName;
    }

    @Override
    public String comment() {
        return comment;
    }

    @Override
    public List<NameRecord> nameRecords() {
        return List.of();
    }

    @Override
    public List<LanguageCode> macroLanguages() {
        return List.of();
    }

    @Override
    public List<LanguageCode> individualLanguages() {
        return List.of();
    }
}
