package org.meeuw.i18n.languages.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXB;
import java.io.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.meeuw.i18n.languages.ISO_639_Code;
import static org.meeuw.i18n.languages.ISO_639.iso639;
import org.meeuw.i18n.languages.LanguageCode;

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
