package org.meeuw.i18n.languages.test;

import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;

// tag::import[]
import org.meeuw.i18n.languages.*;

// end::import[]

import static org.assertj.core.api.Assertions.assertThat;

public class ExamplesTest {

    @Test
    public void examples() {

        // tag::dutch[]
        // a language code can be obtained via static method
        LanguageCode nl = LanguageCode.languageCode("nl");
        // For major language having a ISO-639-1 code there are enum values available
        assertThat(nl).isSameAs(ISO_639_1_Code.nl);

        // other parts of the standards work too
        assertThat(nl).isSameAs(LanguageCode.languageCode("dut")); // ISO-639-2/B code
        assertThat(nl).isSameAs(LanguageCode.languageCode("nld")); // ISO-639-3 / ISO-639-2/T code

        // use it in some way
        assertThat(nl.nameRecord(Locale.US).inverted()).isEqualTo("Dutch");

        // end::dutch[]

        // tag::germanic[]
        LanguageFamilyCode germanic = (LanguageFamilyCode) ISO_639.iso639("gem");
        assertThat(germanic).isSameAs(LanguageFamilyCode.gem);
        // there are not very many language families, so they are all available as enum values
        assertThat(germanic).isSameAs(LanguageFamilyCode.valueOf("gem"));
        // end::germanic[]


        // get by any code
        Optional<ISO_639_Code> byCode = ISO_639.get("nl");

        // stream by names, language may have several names (dutch, flemish), and appear multiple times
        ISO_639.streamByNames().forEach(e -> {
            System.out.println(e.getKey() + " " + e.getValue());
        });

        // tag::krim[]

        // the 'krim' dialect (Sierra Leone) officially merged into 'bmf' (Bom-Kim) in 2017

        assertThat(ISO_639.getByPart3("krm").get().getCode()).isEqualTo("bmf");

        // end::krim[]


        // tag::fallback[]
        // Our partner uses the pseudo ISO-639-1 code 'XX' for 'no language'
        //  fall back to a proper Part 3 code.
        try {
            LanguageCode.registerFallback("XX", LanguageCode.languageCode("zxx"));
            assertThat(ISO_639.iso639("XX").code()).isEqualTo("zxx");
        } finally {
            LanguageCode.resetFallBacks();
        }
        // end::fallback[]


        // tag::dse[]

        assertThat(LanguageCode.languageCode("dse").getDisplayName(new Locale("nl"))).isEqualTo("Nederlandse Gebarentaal");

        // end::dse[]

    }
}
