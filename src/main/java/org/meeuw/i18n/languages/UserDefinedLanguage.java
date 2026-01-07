package org.meeuw.i18n.languages;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;

import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @since 3.4
 */
public class UserDefinedLanguage implements LanguageCode {

    static final Map<String, UserDefinedLanguage> KNOWN = new ConcurrentHashMap<>();


    private final String code;
    private final Type type;
    private final Scope scope;
    private final String refName;
    private final String comment;

    /**
     * Constructor for user defined languages
     * @param code     A code, which not be assigned yet
     * @param type     The type of the language
     * @param refName
     * @param comment
     */
    public UserDefinedLanguage(String code, @NonNull Type type, Scope scope, String refName, @Nullable String comment) {
        this.code = code;
        this.type = type;
        this.scope = scope;
        this.refName = refName;
        this.comment = comment;
        KNOWN.put(this.code, this);
    }

    public UserDefinedLanguage(String code, @NonNull Type type, String refName, @Nullable String comment) {
        this(code, type, Scope.S, refName, comment);
    }


        /**
         * @since 4.0
         * @return
         */
    public static Stream<UserDefinedLanguage> stream() {
        return KNOWN.values().stream();
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
        return scope;
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
        return List.of(new NameRecord(refName, refName));
    }

    @Override
    public List<LanguageCode> macroLanguages() {
        return List.of();
    }

    @Override
    public List<LanguageCode> individualLanguages() {
        return List.of();
    }
    @Override
    public String toString() {
        return code() + " (" + refName + ")";
    }
}
