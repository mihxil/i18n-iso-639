package org.meeuw.test.i18n.languages;

import java.io.*;
import javax.xml.bind.JAXB;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.LanguageCode;

public class BindingTest {


    @Test
    public void xml() {
        StringWriter writer = new StringWriter();
        JAXB.marshal(
            new A(LanguageCode.getByCode("nld").orElse(null)), writer);
        A a = JAXB.unmarshal(new StringReader(writer.toString()), A.class);
        assertThat(a.languageCode).isSameAs(LanguageCode.get("nld").get());
    }
    
    @Test
    public void serialize() throws IOException, ClassNotFoundException {
        A a = new A(LanguageCode.getByCode("nld").orElse(null));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream 
            = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(a);
        
        
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        A deserialized = (A) inputStream.readObject();
        assertThat(deserialized.languageCode.getCode()).isEqualTo("nl");
        assertThat(deserialized.languageCode).isSameAs(LanguageCode.get("nld").get());

        
        
    }
}
