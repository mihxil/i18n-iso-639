package org.meeuw.i18n.languages.test;

import jakarta.validation.*;
import java.util.*;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.meeuw.i18n.languages.LanguageCode;
import org.meeuw.i18n.languages.validation.Language;

/**
 * @author Michiel Meeuwissen
 * @since 2.2
 */
public class LanguageValidatorTest {
    private static final Validator VALIDATOR;


    static {
        Locale.setDefault(new Locale("nl"));
        Validator proposal;
        try (
            ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .buildValidatorFactory()) {

            proposal = factory.getValidator();
        } catch (Exception e) {
            proposal = null;
        }
        VALIDATOR = proposal;
    }

    @ParameterizedTest
    @ValueSource(strings = {"nl", "zxx", "jw", "iw", "dut", "sh", "iw", "ji", "in"})
    public void testIsValid(String lang) {
        String displayName = new Locale(lang).getDisplayLanguage();
        System.out.println(lang + ":" + displayName);
        WithLanguageFields a = new WithLanguageFields();
        a.language = lang;
        assertThat(VALIDATOR.validate(a)).isEmpty();

    }



    @ParameterizedTest
    @ValueSource(strings = {"cz"})
    public void testIsInValid(String lang) {
        WithLanguageFields a = new WithLanguageFields();
        a.language = lang;
        assertThat(VALIDATOR.validate(a)).isNotEmpty();
    }

    @Test
    public void nullIsValid() {
        WithLanguageFields a = new WithLanguageFields();
        assertThat(VALIDATOR.validate(a)).isEmpty();
    }

    @Test
    void achterhoeks() {
        WithLanguageFields a = new WithLanguageFields();
        a.language = "act";
        assertThat(VALIDATOR.validate(a)).isEmpty();
    }

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @ParameterizedTest
    @ValueSource(strings = {
        "ZZ",
    })
    public void testValidateInvalid(String language) {
        WithLanguageFields a = new WithLanguageFields();
        a.language = language;
        Set<ConstraintViolation<WithLanguageFields>> constraintViolations = testValidate(a, 1);
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(language + " is een ongeldige ISO639 taalcode");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ZZ",
        "NL"// case sensitive

    })
    public void testValidateInvalidNotForXml(String language) {
        WithLanguageFields a = new WithLanguageFields();
        a.notForXml = language;
        Set<ConstraintViolation<WithLanguageFields>> constraintViolations = testValidate(a, 1);
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo(language + " is een ongeldige ISO639 taalcode");
    }
    @ParameterizedTest
    @ValueSource(strings = {
        "zh",
        "nl-BE"// case sensitive

    })
    public void testValidateValidNotForXml(String language) {
        WithLanguageFields a = new WithLanguageFields();
        a.notForXml = language;
        Set<ConstraintViolation<WithLanguageFields>> constraintViolations = testValidate(a, 0);
    }


    @Test
    public void testWithCountry() {
        {
            WithLanguageFields a = new WithLanguageFields();
            a.language = "nl-NL";
            testValidate(a, 0);
        }
        {
            WithLanguageFields a = new WithLanguageFields();
            a.language = "nl-NL-INFORMAL";
            testValidate(a, 1);
        }
        {
            WithLanguageFields a = new WithLanguageFields();
            testValidate(a, 0);
        }

    }

    @Test
    void testObject() {
        {
            WithLanguageFields a = new WithLanguageFields();
            a.object = Arrays.asList("ZZ");
            testValidate(a, 1);
        }
        {
            WithLanguageFields a = new WithLanguageFields();
            a.object = Arrays.asList("nl-NL", "nl-be");
            testValidate(a, 0);
        }
    }

    private Set<ConstraintViolation<WithLanguageFields>> testValidate(WithLanguageFields value, int expectedSize) {
        Set<ConstraintViolation<WithLanguageFields>> validate = validator.validate(value);
        System.out.println("" + validate);
        assertThat(validate).hasSize(expectedSize);
        return validate;
    }


    @Test
    @Disabled
    void wiki() {
        Map<String, String> result = new TreeMap<>();
        for (String s : Locale.getISOLanguages()) {
            result.put(s, new Locale(s).getDisplayLanguage(new Locale("en")));
        }
        for (LanguageCode s : LanguageCode.stream().collect(Collectors.toList())) {
            result.put(s.toString(), s.getName());
        }
        // output sorted
        System.out.println("||code||name in english||name in dutch||name in language itself||");
        for (Map.Entry<String, String> e : result.entrySet()) {
            ///assertTrue(languageValidator.isValid(new Locale(e.getKey()), null));
            String en = e.getValue();
            String nl = new Locale(e.getKey()).getDisplayLanguage(new Locale("nl"));
            if (nl.equals(en) || nl.equals(e.getKey())) {
                nl = " ";
            }
            String self = new Locale(e.getKey()).getDisplayLanguage(new Locale(e.getKey()));
            if (self.equals(en) || self.equals(e.getKey())) {
                self = " ";
            }
            System.out.println("|" + e.getKey() + "|" + en + "|"
                + nl + "|" + self + "|");
        }
    }

    static class AXml {
        @Language()
        final String language;
        AXml(String l) {
            this.language = l;
        }
    }

    static class C {
        @Language()
        final String language;
        C(String l) {
            this.language = l;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"nl", "nl-NL", "nl-A"})
    void validA(String lang) {
        assertThat(VALIDATOR.validate(new C(lang))).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"NL", "bl", "bl-A"})
    void invalidA(String lang) {
        assertThat(VALIDATOR.validate(new C(lang))).hasSize(1);
    }
 
 
}