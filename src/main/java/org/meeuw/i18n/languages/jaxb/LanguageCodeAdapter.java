package org.meeuw.i18n.languages.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.meeuw.i18n.languages.ISO_639_Code;

import static org.meeuw.i18n.languages.ISO_639.lenientIso639;

public class LanguageCodeAdapter extends XmlAdapter<String, ISO_639_Code> {
    @Override
    public ISO_639_Code unmarshal(String v) {
        return lenientIso639(v);
    }

    @Override
    public String marshal(ISO_639_Code v) {
        return v.code();
    }
}
