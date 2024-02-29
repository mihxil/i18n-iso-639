package org.meeuw.i18n.languages;

import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

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
    
    /**
     * The {@link #part1() ISO-639-1-code} if available, otherwise the {@link #part3() ISO-639-3 code}.
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
        return name(Locale.US).value();
    }
    
    default Name name(Locale locale) {
        return new Name(toString());
    }
    
}
