package org.meeuw.i18n.languages.test;

import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.ISO_639_Code;

public class ISO_639_Test {
  
    @Test
    public void codesUnique() {
        Set<String> code = new HashSet<>();
        ISO_639_Code.stream()
            .sorted(
                Comparator.comparing(ISO_639_Code::scope)
                .thenComparing(ISO_639_Code::code))
            .forEach(lc -> {
                System.out.println(lc + " ->  " + lc.languageType() + " " + lc.scope());
                assertThat(code.add(lc.code())).isTrue();

        });
    }
}
