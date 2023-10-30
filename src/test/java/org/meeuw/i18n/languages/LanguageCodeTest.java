package org.meeuw.i18n.languages;

import org.junit.jupiter.api.Test;

class LanguageCodeTest {
    
    @Test
    public void stream() {
        LanguageCode.stream().forEach(System.out::println);
    }

}