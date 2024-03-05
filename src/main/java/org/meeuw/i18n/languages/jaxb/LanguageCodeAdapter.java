package org.meeuw.i18n.languages.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.meeuw.i18n.languages.LanguageCode;

public class LanguageCodeAdapter extends XmlAdapter<String, LanguageCode> {
    @Override
    public LanguageCode unmarshal(String v) throws Exception {
        if (v == null || v.isEmpty()) {
            return null;
        }
        return LanguageCode.get(v).orElseThrow(() -> new IllegalStateException("No such language " + v));
    }

    @Override
    public String marshal(LanguageCode v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.getCode();
    }
}
