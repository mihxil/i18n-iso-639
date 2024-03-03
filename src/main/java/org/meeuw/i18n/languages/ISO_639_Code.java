package org.meeuw.i18n.languages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;
import org.meeuw.i18n.languages.jaxb.LanguageCodeAdapter;

@XmlJavaTypeAdapter(LanguageCodeAdapter.class)
public interface ISO_639_Code extends Serializable {

    /**
     * A stream with all known {@link ISO_639_Code language codes}.
     * 
     *
     * @return a stream of all known language codes.
     */
    static Stream<ISO_639_Code> stream() {
        return Stream.concat(
            LanguageCode.stream(),
            Arrays.stream(LanguageFamilyCode.values())
        );
    }
    
    static Optional<ISO_639_Code> get(String code) {
        ISO_639_Code lc = LanguageCode.get(code).orElse(null);
        if (lc == null) {
            try {
                return Optional.of(LanguageFamilyCode.valueOf(code));
            } catch (IllegalArgumentException iae) {
                return Optional.empty();
            }
        } else {
            return Optional.of(lc);
        }
    }
    
      /**
     * As {@link #get(String)}, but throws an {@link IllegalArgumentException} if not found.
     *
     * @return The {@link ISO_639_3_Code} if found
     * @throws IllegalArgumentException if not found
     */
    @JsonCreator
    static ISO_639_Code iso639(String code) {
        return get(code)
            .orElseThrow(() -> new IllegalArgumentException("Unknown language code " + code));
    }

    
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
