package org.meeuw.i18n.languages.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.*;
import org.meeuw.i18n.languages.*;
import org.meeuw.i18n.languages.validation.Language;
import org.meeuw.i18n.languages.validation.LanguageValidationInfo;


/**
 * @author Michiel Meeuwissen
 * @since 2.2
 */
public class LanguageValidator implements ConstraintValidator<Language, Object> {

    private static final Logger logger = Logger.getLogger(LanguageValidator.class.getName());

    public static final String[] LEGACY = {"jw"}; // javanese?

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
        if (language instanceof Locale) {
            return isValid(annotation, ((Locale) language).getLanguage());
        }
        
        if (language instanceof  CharSequence) {
            return isValid(annotation, ((CharSequence) language));
        }
        
        if (language instanceof  Iterable) {
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
      
        String splitter = annotation.forXml() ? "-" :"[_-]";
        String[] components = value.split(splitter, 3);
        
        if (! annotation.mayContainCountry() && components.length > 1 && components[1].length() > 0) { 
            return false;
        } 
        if (! annotation.mayContainVariant() && components.length > 2 && components[2].length() > 0) { 
            return false;
        }
        
        value = components[0];
        
        if (! annotation.requireLowerCase()) {
            value = value.toLowerCase();
        } else if ( ! value.equals(value.toLowerCase())) {
            return false;
        }
        if (annotation.forXml()) {
            value = Locale.forLanguageTag(value).getLanguage();
        }
        
        boolean recognized  = VALID_ISO_LANGUAGES.contains(value) ||
            (annotation.lenient() && EXTRA_RECOGNIZED.contains(value));


        ISO_639_Code code;
        if (! recognized) {

            Optional<? extends LanguageCode> iso3 = LanguageCode.getByPart1(value);
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
            if (annotation.iso639_5()) {
                try {
                    LanguageFamilyCode isoPart5 = LanguageFamilyCode.valueOf(value);
                    return true;
                } catch (IllegalArgumentException iae) {
                    return false;
                }
            }
            
            if (annotation.lenient()) {
                String displayLanguage = new Locale(value).getDisplayLanguage();
                if (!language.equals(displayLanguage)) { // last fall back is iso code itself.
                    logger.info("Not a recognized language " + language + " -> " + displayLanguage + ", but recognized by the JVM. Will follow that");
                    EXTRA_RECOGNIZED.add(value);
                    return true;
                }
            }
        }
        return recognized;

    }
}
