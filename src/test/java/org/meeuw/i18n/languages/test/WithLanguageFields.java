package org.meeuw.i18n.languages.test;

import org.meeuw.i18n.languages.Type;
import org.meeuw.i18n.languages.validation.Language;

/**
 * @author Michiel Meeuwissen
 */
public class WithLanguageFields {
    
    @Language
    public String language;
    

    @Language(
        forXml = false,
        mayContainVariant = true
    )
    public String notForXml;
    

    @Language(type = {Type.L, Type.C})
    public String livingLanguage;

    @Language
    public Object object;
}
