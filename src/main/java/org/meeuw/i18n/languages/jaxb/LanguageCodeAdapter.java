package org.meeuw.i18n.languages.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.meeuw.i18n.languages.*;
import static org.meeuw.i18n.languages.ISO_639.iso639;

public class LanguageCodeAdapter extends XmlAdapter<String, ISO_639_Code> {
    @Override
    public ISO_639_Code unmarshal(String v) throws Exception {
        if (v == null || v.isEmpty()) {
            return null;
        }
        return iso639(v);
    }

    @Override
    public String marshal(ISO_639_Code v) throws Exception {
        return v.code();
    }
}
