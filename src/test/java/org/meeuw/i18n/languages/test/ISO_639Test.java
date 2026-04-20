package org.meeuw.i18n.languages.test;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.meeuw.i18n.languages.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ISO_639Test {

    @Test
    public void codesUnique() {
        Set<String> code = new HashSet<>();
        ISO_639.stream()
            .sorted(
                Comparator.comparing(ISO_639_Code::scope)
                .thenComparing(ISO_639_Code::code))
            .forEach(lc -> {
                System.out.println(lc + " ->  " + lc.languageType() + " " + lc.scope() + " ref name:" + lc.refName() + " " + " display: " + lc.getDisplayName());
                assertThat(code.add(lc.code())).isTrue();

        });
    }

    @Test
    public void undefined() {
        ISO_639_Code und = ISO_639.iso639("und");
        assertThat(und).isNotNull();
    }



    @ParameterizedTest
    @MethodSource("org.meeuw.i18n.languages.ISO_639#stream")
    public void caseInsensitive(ISO_639_Code code) {
        assertThat(ISO_639_Code.fromCode(code.code().toUpperCase())).isEqualTo(code);
    }

    @Test
    public void notFound() {
        assertThat(ISO_639_Code.fromCode("NOTFOUND")).isNotNull();
        assertThat(ISO_639_Code.fromCode("notfound")).isNotNull();

    }
}
