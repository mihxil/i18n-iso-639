package org.meeuw.i18n.languages.validation.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.checkerframework.checker.nullness.qual.*;
import org.meeuw.i18n.languages.LanguageCode;
import org.meeuw.i18n.languages.validation.Language;
import org.meeuw.i18n.languages.validation.LanguageValidationInfo;


/**
 * @author Michiel Meeuwissen
 * @since 2.2
 */
public class LanguageValidator implements ConstraintValidator<Language, Object> {

    private static final Logger logger = Logger.getLogger(LanguageValidator.class.getName());

    public static final String[] LEGACY = {"jw"}; // javanese?

    // http://www-01.sil.org/iso639-3/documentation.asp?id=zxx
    private static final Set<String> VALID_ISO_LANGUAGES;

    private static final Set<String> EXTRA_RECOGNIZED = ConcurrentHashMap.newKeySet();;


    static {
        Set<String> valid = new HashSet<>();
        valid.addAll(Arrays.asList(Locale.getISOLanguages()));
        valid.addAll(Arrays.asList(LEGACY));
        VALID_ISO_LANGUAGES = Collections.unmodifiableSet(valid);
    }

    @MonotonicNonNull
    Language annotation;

    @Override
    @EnsuresNonNull("annotation")
    public void initialize(@NonNull Language constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    @RequiresNonNull("annotation")
    public boolean isValid(@Nullable Object value, @Nullable ConstraintValidatorContext context) {
        return isValid(LanguageValidationInfo.of(annotation), value);
    }

    @RequiresNonNull("annotation")
    public static boolean isValid(LanguageValidationInfo annotation, @Nullable Object language) {
        if (language == null) {
            return true;
        }
        if (language instanceof  Locale) {
            return isValid(annotation, ((Locale) language).getLanguage());
        }
        
        if (language instanceof  CharSequence) {
            return isValid(annotation, ((CharSequence) language));
        }
        
        if (language instanceof  Collection) {
            boolean valid = true;
            for (Object o : (Collection<?>) language) {
                valid &= isValid(annotation, o);
            }
            return valid;
        }
        return false;
    }
    public static boolean isValid(LanguageValidationInfo annotation, @Nullable CharSequence language) {

        if (language == null) {
            return true;
        }
        String value = language.toString();
        boolean recognized  = VALID_ISO_LANGUAGES.contains(value) ||
            (annotation.lenient() && EXTRA_RECOGNIZED.contains(value));

        if (! recognized) {

            Optional<LanguageCode> iso3 = LanguageCode.getByPart1(value);
            if (iso3.isPresent()) {
                return true;
            }
            if (annotation.iso639_3()) {
                Optional<LanguageCode> isoPart1 = LanguageCode.getByPart3(value, annotation.iso639_3_retired());
                if (isoPart1.isPresent()){
                    return true;
                }
            }
            if (annotation.iso639_2()) {
                Optional<LanguageCode> isoPart2B = LanguageCode.getByPart2B(value);
                if (isoPart2B.isPresent()) {
                    return true;
                }

                Optional<LanguageCode> isoPart2T = LanguageCode.getByPart2T(value);
                if (isoPart2T.isPresent()) {
                    return true;
                }
            }
            if (annotation.lenient()) {
                String displayLanguage = new Locale(value).getDisplayLanguage();
                if (!language.equals(displayLanguage)) { // last fall back is iso code itself.
                    logger.info("Not a recognized language " + language + " -> " + displayLanguage + ", so recognized by the JMS. Will follow that");
                    EXTRA_RECOGNIZED.add(value);
                    return true;
                }
            }
        }
        return recognized;

    }
}
