package org.meeuw.i18n.languages.test;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.validation.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.meeuw.i18n.languages.ISO_639;
import org.meeuw.i18n.languages.ISO_639_Code;

import static org.assertj.core.api.Assertions.assertThat;

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
    @ValueSource(strings = {
        "nl",
        "zxx",
        "jw", // should be jv, but wrong in specs on one place
        "iw", // was changed to he
        "id", // was changed to in
        "ji", // was changed to yi
        "dut", // part 2 b code. Valid.
        "sh",
        "in"
    })
    public void testIsValid(String lang) {
        WithLanguageFields a = new WithLanguageFields();
        a.language = lang;
        String displayName = new Locale(lang).getDisplayLanguage();
        System.out.println(lang + ":" + ISO_639.get(a.language).get() + "  (" + displayName + ")");

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
        for (ISO_639_Code s : ISO_639.stream().collect(Collectors.toList())) {
            result.put(s.toString(), s.nameRecord(Locale.ENGLISH).print());
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



    @ParameterizedTest
    @ValueSource(strings = {"nl", "nl-NL", "nl-A"})
    void validField(String lang) {
        assertThat(VALIDATOR.validate(new WithLanguageFields(lang))).isEmpty();
    }


    @ParameterizedTest
    @ValueSource(strings = {"nl", "nl-NL", "nl-A", "hok"})
    void validLanguageOrFamilyField(String lang) {
        WithLanguageFields withLanguageFields = new WithLanguageFields();
        withLanguageFields.livingLanguageOrFamily = lang;
        assertThat(VALIDATOR.validate(withLanguageFields)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"hok"})
    void validFamilyField(String lang) {
        WithLanguageFields withLanguageFields = new WithLanguageFields();
        withLanguageFields.family = lang;
        assertThat(VALIDATOR.validate(withLanguageFields)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"nl", "nl-NL", "nl-A"})
    void invalidFamilyField(String lang) {
        WithLanguageFields withLanguageFields = new WithLanguageFields();
        withLanguageFields.family = lang;
        assertThat(VALIDATOR.validate(withLanguageFields)).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "NL", // uppercase
        "bl", // not a code
        "bl-A", // not a code
        "hok" // a family, not a language
    })
    void invalidFieldValues(String lang) {
        assertThat(VALIDATOR.validate(new WithLanguageFields(lang))).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"bl", "bl-A"})
    void invalidLocale(String lang) {
        WithLanguageFields a = new WithLanguageFields();
        a.object = Locale.forLanguageTag(lang);
        assertThat(VALIDATOR.validate(a)).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"nl", "NL", "nld-A"})
    void validLocale(String lang) {
        WithLanguageFields a = new WithLanguageFields();
        a.object = Locale.forLanguageTag(lang);
        assertThat(VALIDATOR.validate(a)).hasSize(0);
    }



}
