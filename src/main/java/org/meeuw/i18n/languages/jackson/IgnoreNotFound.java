package org.meeuw.i18n.languages.jackson;

import java.io.IOException;

import org.meeuw.i18n.languages.*;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

/**
 * Can be registered in Jackson to ignore {@link LanguageNotFoundException}
 * Could also be accomplished by {@link ISO_639#setIgnoreNotFound()}
 */
public class IgnoreNotFound extends DeserializationProblemHandler {

    public static final IgnoreNotFound NULL = new IgnoreNotFound(null);
    public static final IgnoreNotFound NOT_FOUND = new IgnoreNotFound(LanguageCode.NOTFOUND);

    private final ISO_639_Code fallBack;

    private IgnoreNotFound(LanguageCode fallBack) {
        this.fallBack = fallBack;
    }

    @Override
     public Object handleInstantiationProblem(DeserializationContext ctxt, Class<?> instClass, Object argument, Throwable t) throws IOException {
         if (ISO_639_Code.class.isAssignableFrom(instClass) && t instanceof ValueInstantiationException) {
             if (t.getCause() instanceof LanguageNotFoundException) {
                 return fallBack;
             }
         }
         return super.handleInstantiationProblem(ctxt, instClass, argument, t);
     }
}
