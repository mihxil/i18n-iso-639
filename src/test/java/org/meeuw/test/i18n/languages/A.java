package org.meeuw.test.i18n.languages;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
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
