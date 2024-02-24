/**
 * Basically, wrapping ISO-639-3 language codes
 */
module org.meeuw.i18n.languages {
    exports org.meeuw.i18n.languages;
    exports org.meeuw.i18n.languages.jaxb;
    
    exports org.meeuw.i18n.languages.validation;
    
    // deprecated language validator of regions depends on ours.
    exports org.meeuw.i18n.languages.validation.impl to org.meeuw.i18n.regions;
    
    requires static jakarta.validation;
    
    requires static java.logging;
    requires static org.checkerframework.cheangucker.qual;

    // xml binding (optional annotation)
    requires static jakarta.xml.bind;
    
    // jackson json binding (optional annotation)
    requires static com.fasterxml.jackson.databind;
    
    // but if used, these are needed too
    opens org.meeuw.i18n.languages to com.fasterxml.jackson.databind;
    
    // open to validator implementation
    opens org.meeuw.i18n.languages.validation.impl;


}
