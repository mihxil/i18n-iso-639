package org.meeuw.i18n.languages;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Locale;
import org.meeuw.i18n.languages.jaxb.LanguageCodeAdapter;

@XmlJavaTypeAdapter(LanguageCodeAdapter.class)
public interface ISO_639_Code extends Serializable {


    /**
     * The code associated with this language or language family.
     *
     * @return A 2 or 3 letter language code
     * @since 0.2
     */
    @JsonValue
    String code();
        

    @Override
    String toString();
    
    Scope scope();

    Type languageType();
    
    default String refName() {
        return nameRecord(Locale.US).print();
    }
    
    default NameRecord nameRecord(Locale locale) {
        return new NameRecord(toString());
    }
    
    default NameRecord nameRecord() {
        return new NameRecord(toString());
    }
    
}
