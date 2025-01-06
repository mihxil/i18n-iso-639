package org.meeuw.test.jackson;

import org.meeuw.i18n.languages.ISO_639_Code;
import org.meeuw.i18n.languages.LanguageCode;

import com.fasterxml.jackson.databind.ObjectMapper;

public class iso639 {

    static class Container {
        ISO_639_Code language;

        public Container(ISO_639_Code language) {
            this.language = language;
        }
        public ISO_639_Code getLanguage() {
            return language;
        }
    }

    public static void main(String[] args) {
        String code;
        if (args.length == 0) {
            System.out.println("Usage: iso639 <code>");
            System.out.println("Showing json marshalling of language code using jackson");
            code = "nld";
        } else {
            code = args[0];
        }
        Container languageContainer = LanguageCode.get(code).map(Container::new).orElseThrow();
        System.out.println(
            code + " -> " + languageContainer.language  +
            "\n" + new ObjectMapper().valueToTree(languageContainer)
        );
    }
}

