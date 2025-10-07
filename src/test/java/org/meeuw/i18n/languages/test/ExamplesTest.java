package org.meeuw.i18n.languages.test;

import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ExamplesTest {

    @Test
    public void examples() {
        // get a language by its code;

        Optional<LanguageCode> optional = ISO_639.getByPart3("nld");
        LanguageCode nl1 = LanguageCode.languageCode("nl");
        // For major language having a ISO-639-1 code there are enum values available
        LanguageCode nl2 = ISO_639_1_Code.nl;

        // iso639 supports LanguageFamily and Language.
        LanguageCode nl3 = ISO_639.iso639("nl", LanguageCode.class);
        LanguageFamilyCode ger1 = ISO_639.iso639("gem", LanguageFamilyCode.class);
        LanguageFamilyCode ger2 = LanguageFamilyCode.gem;

        assertThat(nl1).isSameAs(nl2);
        assertThat(nl2).isSameAs(nl3);

        // show its 'inverted' name
        System.out.println(nl1.nameRecord(Locale.US).inverted());

        // get a language family
        Optional<LanguageFamilyCode> family = ISO_639.getByPart5("gem");
        assertThat(family.get()).isSameAs(ger1);

        // get by any code
        Optional<ISO_639_Code> byCode = ISO_639.get("nl");

        // stream by names, language may have several names (dutch, flemish), and appear multiple times
        ISO_639.streamByNames().forEach(e -> {
            System.out.println(e.getKey() + " " + e.getValue());
        });
    }
}
