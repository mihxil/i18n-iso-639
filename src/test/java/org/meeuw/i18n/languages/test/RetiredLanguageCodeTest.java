package org.meeuw.i18n.languages.test;

import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.RetiredLanguageCode;

import static org.assertj.core.api.Assertions.assertThat;

class RetiredLanguageCodeTest {


    @Test
    public void stream() {
        RetiredLanguageCode.stream().forEach(lc -> {
            try {
                System.out.println(lc + "\n-> " + lc.changeTo());
            } catch (RetiredLanguageCode.RetirementException re) {
                System.out.println(lc + "\n-> " + re.getMessage());

            }
        });
    }

    @Test
    public void ppr() throws RetiredLanguageCode.RetirementException {
        assertThat(RetiredLanguageCode.getByCode("ppr").get().changeTo().code()).isEqualTo("lcq");

    }

}
