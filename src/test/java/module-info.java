open module org.meeuw.i18n.languages.test {

    exports org.meeuw.i18n.languages.test;
    requires org.meeuw.i18n.languages;

    requires transitive org.junit.jupiter.engine;
    requires transitive org.junit.jupiter.api;
    requires jakarta.xml.bind;
    requires org.assertj.core;
    requires org.junit.jupiter.params;
    requires jakarta.validation;
    
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
}
