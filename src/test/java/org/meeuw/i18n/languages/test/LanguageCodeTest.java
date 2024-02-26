package org.meeuw.i18n.languages.test;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.LanguageCode;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class LanguageCodeTest {

    @Test
    public void stream() {
        LanguageCode.stream().forEach(lc -> {
            System.out.println(lc + " " + lc.names());
            assertThat(lc.code()).isNotNull();
            assertThat(lc.languageType()).isNotNull();
            assertThat(lc.scope()).isNotNull();
            
            if (lc.part1() != null) {
                System.out.println("Part1: " + lc.getClass());
            }

            if (lc.comment() != null) {
                System.out.println("Comment: " + lc.comment());
            }
        });
    }

    @Test
    public void sort() {
        LanguageCode.stream()
            .sorted((lc1, lc2) -> lc2.names().get(0).value().compareTo(lc1.names().get(0).inverted()))
            .forEach(lc -> {
            System.out.println(lc.code() + "\t" + lc.refName() + " " + lc.names());
        });
    }

    @Test
    public void getByCode() {
        assertThat(LanguageCode.getByPart3("nld").get().refName()).isEqualTo("Dutch");
        assertThat(LanguageCode.getByPart3(null)).isEmpty();
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
        assertThat(LanguageCode.getByPart1("nl").get().refName()).isEqualTo("Dutch");
        assertThat(LanguageCode.getByPart1(null)).isEmpty();

    }
    @Test
    public void getByPart2T() {
        assertThat(LanguageCode.getByPart2T("nld").get().refName()).isEqualTo("Dutch");
        assertThat(LanguageCode.getByPart2T(null)).isEmpty();
    }

    @Test
    public void getByPart2B() {
        assertThat(LanguageCode.getByPart2B("dut").get().refName()).isEqualTo("Dutch");
        assertThat(LanguageCode.getByPart2B(null)).isEmpty();
    }

    @Test
    public void getUnknown() {
        assertThat(LanguageCode.getByPart3("doesntexist")).isEmpty();
    }

    @Test
    public void getCode() {
        assertThat(LanguageCode.getByPart3("nld").get().code()).isEqualTo("nl");
        assertThat(LanguageCode.getByPart3("act").get().code()).isEqualTo("act");
    }

    @Test
    public void krm() {
        // the 'krim' dialect (Sierra Leano) officially merged into 'bmf' (Bom-Kim) in 2017
        assertThat(LanguageCode.getByPart3("krm").get().code()).isEqualTo("bmf");
    }


    @Test
    public void ppr() {
        assertThat(LanguageCode.getByPart3("ppr").get().code()).isEqualTo("lcq");
    }

    @Test
    public void lcq() {
        assertThat(LanguageCode.getByPart3("lcq").get().code()).isEqualTo("lcq");
    }


}
