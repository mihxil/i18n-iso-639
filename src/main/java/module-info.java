/**
 * Basically, wrapping ISO-639-3 language codes
 */
module org.meeuw.i18n.languages {
    
    exports org.meeuw.i18n.languages;
    exports org.meeuw.i18n.languages.jaxb;
    
    requires static jakarta.validation;
    requires static jakarta.xml.bind;

}
