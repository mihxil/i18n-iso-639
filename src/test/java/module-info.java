module org.meeuw.i18n.languages.test {
    exports org.meeuw.test.i18n.languages;
    requires org.meeuw.i18n.languages;

    requires transitive org.junit.jupiter.engine;
    requires transitive org.junit.jupiter.api;
    requires java.xml.bind;
    requires org.assertj.core;
}
    