package org.meeuw.i18n.languages.test;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.meeuw.i18n.languages.LanguageCode;

@XmlRootElement
class A implements Serializable {
    @XmlAttribute
    LanguageCode languageCode;

    public A() {
    }

    public A(LanguageCode languageCode) {
        this.languageCode = languageCode;
    }
}
