package org.meeuw.i18n.languages;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class LanguageCode {

    static final Map<String, LanguageCode> KNOWN;

    static {
        Map<String, LanguageCode> temp = new HashMap<>();
        try (InputStream inputStream = LanguageCode.class.getResourceAsStream("/iso-639-3_Code_Tables_20230123/iso-639-3_20230123.tab");
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            inputStreamReader.readLine(); // skipheader;
            String line = inputStreamReader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                LanguageCode found = new LanguageCode(
                    split[0],
                    split[1],
                    split[2],
                    split[3].length() > 0 ? split[3] : null,
                    Scope.valueOf(split[4]),
                    Type.valueOf(split[5]),
                    split[6],
                    split.length == 8 ? split[7] : null);
                temp.put(found.getId(), found);
                line = inputStreamReader.readLine();
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        KNOWN = Collections.unmodifiableMap(temp);

    }

    @Size(min = 3, max = 3)
    @NotNull
    private final String id;
    
    private final String part2B;
    private final String part2T;
    @Size(min = 2, max = 2)
    @NotNull
    private final String part1;
    private final Scope scope;
    private final Type languageType;
    @NotNull
    private final String refName;
    private final String comment;


    private LanguageCode(
        String id,
        String part2B,
        String part2T,
        String part1,
        Scope scope,
        Type languageType,
        String refName,
        String comment) {
        this.id = id;
        this.part2B = part2B;
        this.part2T = part2T;
        this.part1 = part1;
        this.scope = scope;
        this.languageType = languageType;
        this.refName = refName;
        this.comment = comment;
    }

    public static Stream<LanguageCode> stream() {
        return KNOWN.values().stream();
    }

    public static Optional<LanguageCode> getByCode(String code) {
        return Optional.ofNullable(KNOWN.get(code));
    }

    public static Optional<LanguageCode> getByPart1(String code) {
        return KNOWN.values().stream().filter(i -> code.equals(i.getPart1())).findFirst();
    }

    public static Optional<LanguageCode> getByPart2B(String code) {
        return KNOWN.values().stream().filter(i -> code.equals(i.getPart2B())).findFirst();
    }

    public static Optional<LanguageCode> getByPart2T(String code) {
        return KNOWN.values().stream().filter(i -> code.equals(i.getPart2T())).findFirst();
    }

    /**
     * The three-letter 639-3 identifier
     */
    public String getId() {
        return id;
    }

    /**
     *  Equivalent 639-2 identifier of the bibliographic applications
     *  code set, if there is one
     */
    public String getPart2B() {
        return part2B;
    }

    /**
     * Equivalent 639-2 identifier of the terminology applications code
     * set, if there is one
     */
    public String getPart2T() {
        return part2T;
    }

    /**
     * Equivalent 639-1 identifier, if there is one
     */
    public String getPart1() {
        return part1;
    }

    public Type getLanguageType() {
        return languageType;
    }

    public String getRefName() {
        return refName;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LanguageCode.class.getSimpleName() + "[", "]")
            .add("id='" + id + "'")
            .add("part2B='" + part2B + "'")
            .add("part2T='" + part2T + "'")
            .add("part1='" + part1 + "'")
            .add("scope='" + scope + "'")
            .add("languageType='" + languageType + "'")
            .add("refName='" + refName + "'")
            .add("comment='" + comment + "'")
            .toString();
    }
}
