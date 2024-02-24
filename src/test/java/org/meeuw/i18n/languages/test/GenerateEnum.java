package org.meeuw.i18n.languages.test;

import com.sun.codemodel.*;
import com.sun.codemodel.writer.OutputStreamCodeWriter;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.LanguageCode;

public class GenerateEnum {

    @Test
    public void createEnum() throws JClassAlreadyExistsException, IOException {


        JCodeModel model = new JCodeModel();
        JDefinedClass iso639_1 = model._class("org.meeuw.i18n.languages.ISO_639_1", ClassType.ENUM);

        iso639_1.javadoc()
            .append("This class is automatically generated.")
            .append("It contains the ISO 639-1 language codes.");
        


        JFieldVar languageCode1 = iso639_1.field(JMod.PRIVATE | JMod.FINAL, LanguageCode.class, "languageCode");

        JMethod constructor = iso639_1.constructor(JMod.NONE);
        JBlock body = constructor.body();
        body.assign(
            JExpr._this().ref(languageCode1), 
            model.ref(LanguageCode.class).staticInvoke("languageCode").arg(JExpr._this().invoke("name"))
        );
        
        JMethod getter = iso639_1.method(JMod.PUBLIC, LanguageCode.class, "getLanguageCode");
        getter.body()._return(languageCode1);
        
        JMethod toString = iso639_1.method(JMod.PUBLIC, String.class, "toString");
        toString.annotate(Override.class);
        toString.body()._return(languageCode1.invoke("toString"));



        LanguageCode.stream()
            .sorted()
            .filter(lc -> lc.getPart1() != null)
            .forEach(languageCode -> {

                JEnumConstant enumConstant = iso639_1.enumConstant(languageCode.getPart1());
                enumConstant.javadoc().append(languageCode.getPart1() + " " + languageCode.getName());
                }    
            );
        
        model.build(new OutputStreamCodeWriter(System.out, "UTF-8"));


    }
}
  