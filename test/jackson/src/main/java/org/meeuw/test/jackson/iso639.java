package org.meeuw.test.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.meeuw.i18n.languages.LanguageCode;

public class iso639 {
    
    static class Container {
        LanguageCode language;
        
        public Container(LanguageCode language) {
            this.language = language;
        }
        public LanguageCode getLanguage() {
            return language;
        }
    }
    
    public static void main(String[] args) {
        String code;
        if (args.length == 0) {
            System.out.println("Usage: iso639 <code>");
            code = "nld";
        } else {
            code = args[0];
        }
        Container languageContainer = LanguageCode.get(code).map(c -> new Container(c)).orElseThrow();
        System.out.println(
            code + " -> " + languageContainer.language  + 
            "\n" + new ObjectMapper().valueToTree(languageContainer));
    }
}
