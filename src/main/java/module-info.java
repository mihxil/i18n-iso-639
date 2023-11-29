/**
 * Basically, wrapping ISO-639-3 language codes
 */
module org.meeuw.i18n.languages {
    
    exports org.meeuw.i18n.languages;
    exports org.meeuw.i18n.languages.binding;
    
    requires static java.validation;
    requires static java.xml.bind;

}
