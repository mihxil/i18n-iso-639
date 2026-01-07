package org.meeuw.i18n.languages.test;


import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.ISO_639_1_Code;
import org.meeuw.i18n.languages.jackson.IgnoreNotFound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meeuw.i18n.languages.LanguageCode.NOTFOUND;

public class Jackson2Tests {

    ObjectMapper notFoundIsNull = new ObjectMapper()
        .addHandler(IgnoreNotFound.NULL);


    ObjectMapper notFoundIsNotFound = new ObjectMapper()
        .addHandler(IgnoreNotFound.NOT_FOUND);



    @Test
    public void stillFinds() throws JsonProcessingException {
        A a = notFoundIsNull.readValue("{\"languageCode\": \"nl\"}", A.class);
        assertThat(a.languageCode).isEqualTo(ISO_639_1_Code.nl);
    }

    @Test
    public void jacksonNotFoundIsNull() throws JsonProcessingException {
        String code = "zz";
        A a = notFoundIsNull.readValue("{\"languageCode\": \"" + code + "\"}", A.class);
        assertThat(a.languageCode).isNull();
    }

    @Test
    public void stillFindsNotFound() throws JsonProcessingException {
        A a = notFoundIsNotFound.readValue("{\"languageCode\": \"nl\"}", A.class);
        assertThat(a.languageCode).isEqualTo(ISO_639_1_Code.nl);
    }

    @Test
    public void jacksonNotFoundIsNotFound() throws JsonProcessingException {
        String code = "zz";
        A a = notFoundIsNotFound.readValue("{\"languageCode\": \"" + code + "\"}", A.class);
        assertThat(a.languageCode).isEqualTo(NOTFOUND);
    }







}
