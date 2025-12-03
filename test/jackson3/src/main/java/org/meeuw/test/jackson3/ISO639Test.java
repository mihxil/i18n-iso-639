package org.meeuw.test.jackson3;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.meeuw.i18n.languages.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ISO639Test {

    static class Container {
        ISO_639_Code language;

        @JsonCreator
        public Container(@JsonProperty("language") ISO_639_Code language) {
            this.language = language;
        }
        public ISO_639_Code getLanguage() {
            return language;
        }
    }

    public static void main(String[] args) throws IOException {
        String code;
        if (args.length == 0) {
            System.out.println("Usage: iso639 <code>|<json to unmarshal>");
            System.out.println("Showing json marshalling of language code using jackson");
            code = "nld";
        } else {
            code = args[0];
            if ("-".equals(code)) {
                code = new String(System.in.readAllBytes());
            }
        }

        try (ISO_639.RemoveIgnoreNotFound s = ISO_639.implicitUserDefine()) {
            ObjectMapper mapper = new ObjectMapper();
            //mapper.addHandler(IgnoreNotFound.NOT_FOUND);

            if (code.startsWith("{")) {
                Container languageContainer = mapper.readValue(code, Container.class);
                System.out.println(code + " -> " + languageContainer.language);
            } else {
                Container languageContainer = LanguageCode.get(code).map(Container::new).orElseThrow();
                System.out.println(
                    code + " -> " + languageContainer.language +
                        "\n" + mapper.valueToTree(languageContainer)
                );
            }
        }
    }
}

