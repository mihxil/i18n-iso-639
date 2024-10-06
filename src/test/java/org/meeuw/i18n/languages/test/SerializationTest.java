package org.meeuw.i18n.languages.test;

import java.io.*;

import jakarta.xml.bind.JAXB;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.meeuw.i18n.languages.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meeuw.i18n.languages.ISO_639.iso639;
import static org.meeuw.i18n.languages.LanguageCode.NOTFOUND;
import static org.meeuw.i18n.languages.LanguageCode.UNKNOWN;


public class SerializationTest {

    /**
     * Test serializing/deserializing to XML
     */
    @ParameterizedTest
    @ValueSource(strings = {"nld", "gem"})
    public void xml(String code) {
        StringWriter writer = new StringWriter();
        JAXB.marshal(
            new A(iso639(code), LanguageCode.languageCode("be")), writer);
        System.out.println(writer);
        A a = JAXB.unmarshal(new StringReader(writer.toString()), A.class);


        assertThat(a.isoCode).isSameAs(iso639(code));
    }

    /**
     * Test deserializing from XML (erroneous)
     */
    @Test
    public void xmlErroneous() {
        {
            A a = JAXB.unmarshal(new StringReader("<a languageCode='zz' />"), A.class);

            assertThat(a.languageCode).isNull();
        }
        try {
            ISO_639.setIgnoreNotFound();
             A a = JAXB.unmarshal(new StringReader("<a languageCode='zz' />"), A.class);
            assertThat(a.languageCode).isEqualTo(NOTFOUND);
        } finally {
            ISO_639.removeIgnoreNotFound();
        }

        try {
            ISO_639.registerFallback("zz", UNKNOWN);
            A a = JAXB.unmarshal(new StringReader("<a languageCode='zz' />"), A.class);
            assertThat(a.languageCode).isEqualTo(UNKNOWN);
        } finally {
            ISO_639.resetFallBacks();
        }




    }



     /**
     * Test serializing/deserializing to XML
     */
    @ParameterizedTest
    @ValueSource(strings = {"nld", "gem"})
    public void json(String code) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String targetCode = iso639(code).code();
        //objectMapper.registerModule(new org.meeuw.i18n.languages.jackson.LanguageModule(true));

        String s = objectMapper.writeValueAsString(new A(iso639(code), LanguageCode.languageCode("be")));
        System.out.println(s);

        assertThat(s).isEqualTo("{\"isoCode\":\"" + targetCode + "\",\"languageCode\":\"be\"}");

        A rounded = objectMapper.readValue(s, A.class);
        assertThat(rounded.isoCode.code()).isEqualTo(targetCode);
    }

     /**
     * Test deserializing from XML (erroneous)
     */
    @Test
    public void jsonErroneous() throws JsonProcessingException {
        String code = "zz";
        ObjectMapper objectMapper = new ObjectMapper();


        Assertions.assertThatThrownBy(() -> {
            A a = objectMapper.readValue("{\"languageCode\": \"" + code + "\"}", A.class);
        }).isInstanceOf(ValueInstantiationException.class);

        try {
            ISO_639.setIgnoreNotFound();
            A a = objectMapper.readValue("{\"languageCode\": \"" + code + "\"}", A.class);
            assertThat(a.languageCode).isEqualTo(NOTFOUND);
        } finally {
            ISO_639.removeIgnoreNotFound();
        }
        try {
            ISO_639.registerFallback("zz", UNKNOWN);
            A a = objectMapper.readValue("{\"languageCode\": \"" + code + "\"}", A.class);
            assertThat(a.languageCode).isEqualTo(UNKNOWN);
        } finally {
            ISO_639.resetFallBacks();
        }
    }






    /**
     * Test java serializing/deserializing
     */
    @ParameterizedTest
    @ValueSource(strings = {"nld", "gem"})
    public void serialize(String code) throws IOException, ClassNotFoundException {
        ISO_639_Code looked = iso639(code);
        A a = new A(looked, LanguageCode.languageCode("be"));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream
            = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(a);


        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        A deserialized = (A) inputStream.readObject();
        assertThat(deserialized.isoCode.code()).isEqualTo(looked.code());
        assertThat(deserialized.isoCode).isSameAs(looked);
    }


}
