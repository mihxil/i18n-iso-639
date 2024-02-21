package org.meeuw.i18n.languages.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import org.meeuw.i18n.languages.LanguageCode;

@XmlRootElement 
class A implements Serializable {
    @XmlAttribute
    @JsonProperty
    LanguageCode languageCode;

    public A() {
    }

    public A(LanguageCode languageCode) {
        this.languageCode = languageCode;
    }
    
}
