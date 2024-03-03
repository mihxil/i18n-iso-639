package org.meeuw.i18n.languages.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.*;
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

        String splitter = annotation.forXml() ? "-" : "[_-]";
        String[] components = value.split(splitter, 3);

        if (!annotation.mayContainCountry() && components.length > 1 && components[1].length() > 0) {
            return false;
        }
        if (!annotation.mayContainVariant() && components.length > 2 && components[2].length() > 0) {
            return false;
        }

        value = components[0];

        if (!annotation.requireLowerCase()) {
            value = value.toLowerCase();
        } else if (!value.equals(value.toLowerCase())) {
            return false;
        }
        if (annotation.forXml()) {
            value = Locale.forLanguageTag(value).getLanguage();
        }
        
        Optional<? extends ISO_639_Code> languageCode = getLanguage(annotation, value);
        if (languageCode.isPresent()) {
            ISO_639_Code lc = languageCode.get();
            if (annotation.scope().length > 0 && !Arrays.asList(annotation.scope()).contains(lc.scope())) {
                return false;
            }
            if (annotation.type().length > 0 && !Arrays.asList(annotation.type()).contains(lc.languageType())) {
                return false;
            }

            return true;
        } else {
            return false;
        }


    }
    
    private static Optional<? extends ISO_639_Code> getLanguage(LanguageValidationInfo annotation, String value) {
      
        
        Optional<LanguageCode> iso3 = LanguageCode.getByPart1(value);
        if (iso3.isPresent()) {
            return iso3;
        }
        if (annotation.iso639_3()) {
            Optional<LanguageCode> isoPart1 = ISO_639.getByPart3(value, annotation.iso639_3_retired());
            if (isoPart1.isPresent()){
                return isoPart1;
            }
        }
        if (annotation.iso639_2()) {
            Optional<LanguageCode> isoPart2B = ISO_639.getByPart2B(value);
            if (isoPart2B.isPresent()) {
                return isoPart2B;
            }
            
            Optional<LanguageCode> isoPart2T = ISO_639.getByPart2T(value);
            if (isoPart2T.isPresent()) {
                return isoPart2T;
            }
        }
        if (annotation.iso639_5()) {
            Optional<LanguageFamilyCode> isoPart5 = LanguageFamilyCode.get(value);
            if (isoPart5.isPresent()) {
                return isoPart5;
            }
        }
        
        if (annotation.lenient()) {
            String displayLanguage = new Locale(value).getDisplayLanguage();
            if (!value.equals(displayLanguage)) { // last fall back is iso code itself.
                logger.info("Not a recognized language " + value+ " -> " + displayLanguage + ", but recognized by the JVM. Will follow that");
                //EXTRA_RECOGNIZED.add(value);
                //return ;
            }
        }
        return Optional.empty();
    }
    
}
