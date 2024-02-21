/**
 * Basically, wrapping ISO-639-3 language codes
 */
module org.meeuw.i18n.languages {
    exports org.meeuw.i18n.languages;
    exports org.meeuw.i18n.languages.jaxb;
    exports org.meeuw.i18n.languages.validation;
    exports org.meeuw.i18n.languages.validation.impl;
    exports org.meeuw.i18n.languages.jackson;

    requires static jakarta.validation;
    requires static jakarta.xml.bind;
    requires static java.logging;
    requires static org.checkerframework.checker.qual;
    
    requires static com.fasterxml.jackson.databind;
    opens org.meeuw.i18n.languages to com.fasterxml.jackson.databind;
    requires static com.fasterxml.jackson.core;

}
