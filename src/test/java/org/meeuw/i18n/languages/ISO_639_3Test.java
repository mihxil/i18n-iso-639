package org.meeuw.i18n.languages;

import org.junit.jupiter.api.Test;

class ISO_639_3Test {
    
    @Test
    public void stream() {
        LanguageCode.stream().forEach(System.out::println);
    }

}