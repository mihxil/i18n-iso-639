package org.meeuw.test;


import java.util.Optional;
import org.meeuw.i18n.languages.LanguageCode;

public class iso639 {
    
    public static void main(String[] args) {
        String code;
        if (args.length == 0) {
            System.out.println("Usage: iso639 <code>");
            code = "nld";
        } else {
            code = args[0];
        }
        Optional<LanguageCode> language = LanguageCode.get(code);
        System.out.println(code + " -> " + language);
    }
}

