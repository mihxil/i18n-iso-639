package org.meeuw.i18n.languages.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.meeuw.i18n.languages.LanguageCode;

public class LanguageCodeAdapter extends XmlAdapter<String, LanguageCode> {
    @Override
    public LanguageCode unmarshal(String v) throws Exception {
        return LanguageCode.get(v).orElseThrow(Exception::new);
    }

    @Override
    public String marshal(LanguageCode v) throws Exception {
        return v.code();
    }
}
