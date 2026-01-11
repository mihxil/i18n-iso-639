package org.meeuw.i18n.languages;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meeuw.i18n.languages.jaxb.LanguageCodeAdapter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A code in the ISO-639 standard for {@link LanguageCode languages} and {@link LanguageFamilyCode language families}.
 */
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
        return nameRecord();
    }

     default NameRecord nameRecord(LanguageCode locale) {
        return nameRecord(locale.toLocale());
    }

    default NameRecord nameRecord() {
        return new NameRecord(toString());
    }

    @JsonCreator
    static ISO_639_Code fromCode(String code) {
        return ISO_639.iso639(code);
    }

    /**
     * @since 4.2
     * @param languageCode
     * @return
     */
    default String getDisplayName(LanguageCode languageCode) {
        return getDisplayName(languageCode.toLocale());
    }

    default String getDisplayName(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        ResourceBundle bundle =  ResourceBundle.getBundle("org.meeuw.i18n.languages.DisplayNames", locale);
        return bundle.getString(code());
    }
    /**
     * @since 4.2
     * @return
     */
    default String getDisplayName() {
        return getDisplayName(Locale.getDefault());
    }

}
