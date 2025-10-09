/**
 * Basically, wrapping ISO-639-3 language codes
 */
module org.meeuw.i18n.languages {
    exports org.meeuw.i18n.languages;
    exports org.meeuw.i18n.languages.jaxb;

    exports org.meeuw.i18n.languages.validation;

    // deprecated language validator of regions depends on ours.

    requires static jakarta.validation;

    requires static java.logging;
    requires static org.checkerframework.checker.qual;

    // xml binding (optional annotation)
    requires static jakarta.xml.bind;

    // jackson json binding (optional annotation)
    requires static com.fasterxml.jackson.databind;
    requires static java.compiler;


    // but if used, these are needed too
    opens org.meeuw.i18n.languages to com.fasterxml.jackson.databind;

    // open to validator implementation
    opens org.meeuw.i18n.languages.validation.impl;


}
