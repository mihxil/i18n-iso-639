/**
 * Basically, wrapping ISO-639-3 language codes
 */
module org.meeuw.i18n.languages {
    
    exports org.meeuw.i18n.languages;
    exports org.meeuw.i18n.languages.jaxb;
    
    requires static java.validation;
    requires static java.xml.bind;
    requires java.logging;
    
    // jackson json binding (optional annotation)
    requires static com.fasterxml.jackson.databind;
    
    // but if used, these are needed too
    opens org.meeuw.i18n.languages to com.fasterxml.jackson.databind;

}
