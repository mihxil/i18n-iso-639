package org.meeuw.i18n.languages.jackson3;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.ValueInstantiationException;

import org.meeuw.i18n.languages.*;

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
     public Object handleInstantiationProblem(DeserializationContext ctxt, Class<?> instClass, Object argument, Throwable t) {
         if (ISO_639_Code.class.isAssignableFrom(instClass) && t instanceof ValueInstantiationException) {
             if (t.getCause() instanceof LanguageNotFoundException) {
                 return fallBack;
             }
         }
         return super.handleInstantiationProblem(ctxt, instClass, argument, t);
     }
}
