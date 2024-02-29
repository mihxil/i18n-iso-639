package org.meeuw.i18n.languages.test;

import org.meeuw.i18n.languages.Scope;
import org.meeuw.i18n.languages.Type;
import org.meeuw.i18n.languages.validation.Language;

/**
 * @author Michiel Meeuwissen
 */
public class WithLanguageFields {
    
    public WithLanguageFields(String la) {
        language = la;
    }
    public WithLanguageFields() {
        
    }

    
    @Language
    public String language;

    @Language(
        forXml = false,
        mayContainVariant = true
    )
    public String notForXml;
    

    @Language(type = {Type.L, Type.C})
    public String livingLanguage;
    
    @Language(type = {Type.L, Type.C}, iso639_5 = true)
    public String livingLanguageOrFamily;
    
    
    @Language(scope = {Scope.FAMILY}, iso639_5 = true)
    public String family;
    
      
    @Language(scope = {Scope.FAMILY}, iso639_5 = false)
    public String impossible;
    
    @Language
    public Object object;
}
