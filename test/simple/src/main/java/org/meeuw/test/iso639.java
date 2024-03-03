package org.meeuw.test;


import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.meeuw.i18n.languages.*;

public class iso639 {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: iso639 [<code>|-|" + Arrays.stream(Scope.values()).map(Enum::name).collect(Collectors.joining("|")) + "]");
            System.out.println("Showing that it works with no dependencies");
        } else {
            if (args[0].equals(args[0].toUpperCase())) {
                final Predicate<ISO_639_Code> predicate;
                if (args[0].equals("-")) {
                    predicate = c -> true;
                } else {
                    Scope scope = Scope.valueOf(args[0]);
                    predicate = c -> c.scope() == scope;
                }
                ISO_639_Code.stream()
                    .filter(predicate)
                    .forEach(iso639::print);
            } else {
                String code = args[0];
                ISO_639_Code language = ISO_639_Code.get(code).orElseThrow(() -> new IllegalArgumentException("No language found for " + code));
                print(language);
            }
        }
        
        Optional<LanguageCode> optional = LanguageCode.getByPart3("nld");

        ISO_639_Code languageCode = LanguageCode.languageCode("nl");

        languageCode.nameRecord(Locale.US).inverted();

        
     LanguageCode.stream()
         .sorted();
    }
    
    private static void print(ISO_639_Code language) {
        System.out.println(language.code() + " -> " + language);
    }
}

