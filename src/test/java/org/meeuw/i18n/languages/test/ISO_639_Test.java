package org.meeuw.i18n.languages.test;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.ISO_639;
import org.meeuw.i18n.languages.ISO_639_Code;

import static org.assertj.core.api.Assertions.assertThat;

public class ISO_639_Test {

    @Test
    public void codesUnique() {
        Set<String> code = new HashSet<>();
        ISO_639.stream()
            .sorted(
                Comparator.comparing(ISO_639_Code::scope)
                .thenComparing(ISO_639_Code::code))
            .forEach(lc -> {
                System.out.println(lc + " ->  " + lc.languageType() + " " + lc.scope() + " ref name:" + lc.refName() + " ");
                assertThat(code.add(lc.code())).isTrue();

        });
    }
}
