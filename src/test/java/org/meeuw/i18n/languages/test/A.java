package org.meeuw.i18n.languages.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import org.meeuw.i18n.languages.ISO_639_Code;

@XmlRootElement 
class A implements Serializable {
    @XmlAttribute
    @JsonProperty
    ISO_639_Code languageCode;

    public A() {
    }

    public A(ISO_639_Code languageCode) {
        this.languageCode = languageCode;
    }
    
}
