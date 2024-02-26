package org.meeuw.i18n.languages.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXB;
import java.io.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.LanguageCode;
import static org.meeuw.i18n.languages.LanguageCode.languageCode;

public class SerializationTest {
    
    /**
     * Test serializing/deserializing to XML
     */
    @Test
    public void xml() {
        StringWriter writer = new StringWriter();
        JAXB.marshal(
            new A(LanguageCode.getByPart3("nld").orElse(null)), writer);
        System.out.println(writer.toString());
        A a = JAXB.unmarshal(new StringReader(writer.toString()), A.class);
        

        
        assertThat(a.languageCode).isSameAs(languageCode("nld"));
    }
    
    
     /**
     * Test serializing/deserializing to XML
     */
    @Test
    public void json() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.registerModule(new org.meeuw.i18n.languages.jackson.LanguageModule(true));
        
        String s = objectMapper.writeValueAsString(new A(LanguageCode.getByPart3("nld").orElseThrow()));
        System.out.println(s);
        
        assertThat(s).isEqualTo("{\"languageCode\":\"nl\"}");
        
        A rounded = objectMapper.readValue(s, A.class);
        assertThat(rounded.languageCode.code()).isEqualTo("nl");         
    }
    
    
    
    /**
     * Test java serializing/deserializing 
     */
    @Test
    public void serialize() throws IOException, ClassNotFoundException {
        A a = new A(LanguageCode.getByPart3("nld").orElse(null));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream 
            = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(a);
        
        
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        A deserialized = (A) inputStream.readObject();
        assertThat(deserialized.languageCode.code()).isEqualTo("nl");
        assertThat(deserialized.languageCode).isSameAs(languageCode("nld"));

        
        
    }
}
