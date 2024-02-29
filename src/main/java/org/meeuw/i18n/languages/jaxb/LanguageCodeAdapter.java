package org.meeuw.i18n.languages.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.meeuw.i18n.languages.LanguageCode;
import org.meeuw.i18n.languages.ISO_639_Code;

public class LanguageCodeAdapter extends XmlAdapter<String, ISO_639_Code> {
    @Override
    public ISO_639_Code unmarshal(String v) throws Exception {
        return LanguageCode.get(v).orElseThrow(Exception::new);
    }

    @Override
    public String marshal(ISO_639_Code v) throws Exception {
        return v.code();
    }
}
