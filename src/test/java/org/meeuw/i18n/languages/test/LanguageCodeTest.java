package org.meeuw.i18n.languages.test;

import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.LanguageCode;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class LanguageCodeTest {

    @Test
    public void stream() {
        LanguageCode.stream().forEach(lc -> {
            System.out.println(lc);
            assertThat(lc.getCode()).isNotNull();
            assertThat(lc.getLanguageType()).isNotNull();
            assertThat(lc.getScope()).isNotNull();
            
            if (lc.getPart1() != null) {
                System.out.println("Part1: " + lc.getISO_639_1());
            }

            if (lc.getComment() != null) {
                System.out.println("Comment: " + lc.getComment());
            }
        });
    }

    @Test
    public void sort() {
        LanguageCode.stream().sorted().forEach(lc -> {
            System.out.println(lc.getCode() + "\t" + lc.getName() + (lc.getName().equals(lc.getInvertedName()) ? "" : (" (" + lc.getInvertedName() + ")")));
        });
    }

    @Test
    public void getByCode() {
        assertThat(LanguageCode.getByPart3("nld").get().getRefName()).isEqualTo("Dutch");
        assertThat(LanguageCode.getByPart3(null)).isEmpty();
    }

    @Test
    public void get() {
        assertThat(LanguageCode.get("nl").get().getRefName()).isEqualTo("Dutch");
        assertThat(LanguageCode.languageCode("nl").getRefName()).isEqualTo("Dutch");
        assertThat(LanguageCode.get("nld").get().getRefName()).isEqualTo("Dutch");
    }

    @Test
    public void getTokiPona() {
        assertThat(LanguageCode.get("tok").get().getRefName()).isEqualTo("Toki Pona");
    }

    @Test
    public void getByPart1() {
        assertThat(LanguageCode.getByPart1("nl").get().getRefName()).isEqualTo("Dutch");
        assertThat(LanguageCode.getByPart1(null)).isEmpty();

    }
    @Test
    public void getByPart2T() {
        assertThat(LanguageCode.getByPart2T("nld").get().getRefName()).isEqualTo("Dutch");
        assertThat(LanguageCode.getByPart2T(null)).isEmpty();
    }

    @Test
    public void getByPart2B() {
        assertThat(LanguageCode.getByPart2B("dut").get().getRefName()).isEqualTo("Dutch");
        assertThat(LanguageCode.getByPart2B(null)).isEmpty();
    }

    @Test
    public void getUnknown() {
        assertThat(LanguageCode.getByPart3("doesntexist")).isEmpty();
    }

    @Test
    public void getCode() {
        assertThat(LanguageCode.getByPart3("nld").get().getCode()).isEqualTo("nl");
        assertThat(LanguageCode.getByPart3("act").get().getCode()).isEqualTo("act");
    }

    @Test
    public void krm() {
        // the 'krim' dialect (Sierra Leano) officially merged into 'bmf' (Bom-Kim) in 2017
        assertThat(LanguageCode.getByPart3("krm").get().getCode()).isEqualTo("bmf");
    }


    @Test
    public void ppr() {
        assertThat(LanguageCode.getByPart3("ppr").get().getCode()).isEqualTo("lcq");
    }

    @Test
    public void lcq() {
        assertThat(LanguageCode.getByPart3("lcq").get().getCode()).isEqualTo("lcq");
    }


}
