package org.meeuw.i18n.languages.test;

import tools.jackson.databind.json.JsonMapper;

import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.ISO_639_1_Code;
import org.meeuw.i18n.languages.jackson3.IgnoreNotFound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meeuw.i18n.languages.LanguageCode.NOTFOUND;

public class Jackson3Tests {

    JsonMapper notFoundIsNull = JsonMapper.builder()
        .addHandler(IgnoreNotFound.NULL)
        .build();

    JsonMapper notFoundIsNotFound = JsonMapper.builder()
        .addHandler(IgnoreNotFound.NOT_FOUND)
        .build();



    @Test
    public void stillFinds() {
        A a = notFoundIsNull.readValue("{\"languageCode\": \"nl\"}", A.class);
        assertThat(a.languageCode).isEqualTo(ISO_639_1_Code.nl);
    }

    @Test
    public void jacksonNotFoundIsNull() {
        String code = "zz";
        A a = notFoundIsNull.readValue("{\"languageCode\": \"" + code + "\"}", A.class);
        assertThat(a.languageCode).isNull();
    }

    @Test
    public void stillFindsNotFound() {
        A a = notFoundIsNotFound.readValue("{\"languageCode\": \"nl\"}", A.class);
        assertThat(a.languageCode).isEqualTo(ISO_639_1_Code.nl);
    }

    @Test
    public void jacksonNotFoundIsNotFound() {
        String code = "zz";
        A a = notFoundIsNotFound.readValue("{\"languageCode\": \"" + code + "\"}", A.class);
        assertThat(a.languageCode).isEqualTo(NOTFOUND);
    }







}
