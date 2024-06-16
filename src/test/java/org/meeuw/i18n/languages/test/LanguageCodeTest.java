package org.meeuw.i18n.languages.test;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class LanguageCodeTest {


    @Test
    public void example() {

        // get a language by its code;
        Optional<LanguageCode> optional = ISO_639.getByPart3("nld");
        LanguageCode languageCode = LanguageCode.languageCode("nl");

        // show its 'inverted' name
        System.out.println(languageCode.nameRecord(Locale.US).inverted());

        // get a language family
        Optional<LanguageFamilyCode> family = ISO_639.getByPart5("ger");

        // get by any code
        Optional<ISO_639_Code> byCode = ISO_639.get("nl");

        // stream by names, language may have several names (dutch, flemish), and appear multiple times
        ISO_639.streamByNames().forEach(e -> {
            System.out.println(e.getKey() + " " + e.getValue());
        });
    }

    @Test
    public void stream() {
        LanguageCode.stream().forEach(lc -> {
            System.out.println(lc + " (" + lc.scope() + ")");
            assertThat(lc.code()).isNotNull();
            assertThat(lc.languageType()).isNotNull();
            assertThat(lc.scope()).isNotNull();

            if (lc.part1() != null) {
                assertThat(lc).isInstanceOf(ISO_639_1_Code.class);
            }
            if (lc.comment() != null) {
                System.out.println("Comment: " + lc.comment());
            }
            if (lc.scope() == Scope.M) {
                assertThat(lc.individualLanguages()).isNotEmpty();
                System.out.println("Macro language with: " + lc.individualLanguages());
                for (LanguageCode individual : lc.individualLanguages()) {
                    if (individual instanceof RetiredLanguageCode) {
                        System.out.println("Retired: " + individual + " " + ((RetiredLanguageCode) individual).retReason());
                    } else {
                        assertThat(individual.macroLanguages())
                            .withFailMessage("macro language " + lc + " has " + individual + " but this has not it as macro").contains(lc);
                    }
                }
            }
            if (! lc.macroLanguages().isEmpty()) {
                System.out.println("Macro language for " + lc + " :" + lc.macroLanguages());
                for (LanguageCode macro : lc.macroLanguages()) {
                    assertThat(macro.individualLanguages()).contains(lc);
                    assertThat(macro.scope()).isEqualTo(Scope.M);
                }
            }
        });
    }

    @Test
    public void streamByName() {
        AtomicLong count = new AtomicLong();
        LanguageCode.streamByNames().forEach(e -> {
            System.out.println(e.getKey() + " " + e.getValue());
            count.incrementAndGet();
        });
        assertThat(count.get()).isGreaterThan(LanguageCode.stream().count());
    }

    @Test
    public void sort() {
        LanguageCode.stream()
            .sorted(Comparator.comparing(LanguageCode::refName))
            .forEach(lc -> {
            System.out.println(lc.code() + "\t" + lc.refName() + " " + lc.nameRecord());
        });
    }

    @Test
    public void getByCode() {
        assertThat(ISO_639.getByPart3("nld").get().refName()).isEqualTo("Dutch");
        assertThat(ISO_639.getByPart3(null)).isEmpty();
    }

    @Test
    public void get() {
        assertThat(LanguageCode.get("nl").get().refName()).isEqualTo("Dutch");
        assertThat(LanguageCode.languageCode("nl").refName()).isEqualTo("Dutch");
        assertThat(LanguageCode.get("nld").get().refName()).isEqualTo("Dutch");
    }

    @Test
    public void getTokiPona() {
        assertThat(LanguageCode.get("tok").get().refName()).isEqualTo("Toki Pona");
    }

    @Test
    public void getByPart1() {
        assertThat(ISO_639.getByPart1("nl").get().refName()).isEqualTo("Dutch");
        assertThat(ISO_639.getByPart1(null)).isEmpty();
    }

    @Test
    public void getByPart2T() {
        assertThat(ISO_639.getByPart2T("nld").get().refName()).isEqualTo("Dutch");
        assertThat(ISO_639.getByPart2T(null)).isEmpty();
    }

    @Test
    public void getByPart2B() {
        assertThat(ISO_639.getByPart2B("dut").get().refName()).isEqualTo("Dutch");
        assertThat(ISO_639.getByPart2B(null)).isEmpty();
    }

    @Test
    public void getUnknown() {
        assertThat(ISO_639.getByPart3("doesntexist")).isEmpty();
    }

    @Test
    public void getCode() {
        assertThat(ISO_639.getByPart3("nld").get().code()).isEqualTo("nl");
        assertThat(ISO_639.getByPart3("act").get().code()).isEqualTo("act");
    }

    @Test
    public void krm() {
        // the 'krim' dialect (Sierra Leano) officially merged into 'bmf' (Bom-Kim) in 2017
        assertThat(ISO_639.getByPart3("krm").get().code()).isEqualTo("bmf");
    }

    @Test
    public void ppr() {
        assertThat(ISO_639.getByPart3("ppr").get().code()).isEqualTo("lcq");
    }

    @Test
    public void lcq() {
        assertThat(ISO_639.getByPart3("lcq").get().code()).isEqualTo("lcq");
    }

    @Test
    public void XX() {
        try {
            assertThatThrownBy(() -> ISO_639.iso639("XX")).isInstanceOf(IllegalArgumentException.class);

            LanguageCode.registerFallback("XX", LanguageCode.languageCode("zxx"));

            assertThat(ISO_639.iso639("XX").code()).isEqualTo("zxx");
        } finally {
            LanguageCode.resetFallBacks();
        }
    }

}
