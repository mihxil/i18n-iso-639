package org.meeuw.i18n.languages.test;

import com.sun.codemodel.*;
import com.sun.codemodel.writer.FileCodeWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.*;


public class GenerateEnum {

    @Test
    public void createEnum() throws JClassAlreadyExistsException, IOException {
        
        String path = "src/main/java";

        File file = new File(path);
        String absolutePath = file.getAbsolutePath();
        System.out.println("File path : " + absolutePath);

        JCodeModel model = new JCodeModel();
        JDefinedClass iso639_1 = model._class("org.meeuw.i18n.languages.ISO_639_1", ClassType.ENUM);
        iso639_1._implements(LanguageCode.class);

        iso639_1.javadoc()
            .append("This class is automatically generated.")
            .append("It contains the ISO 639-1 language codes.");
        


        JFieldVar languageCode = iso639_1.field(JMod.PRIVATE, LanguageCode.class, "languageCode");

        JMethod constructor = iso639_1.constructor(JMod.NONE);
        JBlock body = constructor.body();
       
        JMethod getter = iso639_1.method(JMod.PUBLIC, LanguageCode.class, "getLanguageCode");
        JFieldRef ref = JExpr._this().ref(languageCode);

        getter.body()._if(ref.eq(JExpr._null()))._then().assign(
            JExpr._this().ref(languageCode), 
            model.ref(LanguageCode.class)
                .staticInvoke("getByPart1").arg(JExpr._this().invoke("name")).arg( JExpr.lit(false)).invoke("orElse").arg(JExpr._null()));
        getter.body()._return(ref);

        overrideGetter(iso639_1, languageCode, "toString", String.class);
        overrideGetter(iso639_1, languageCode, "code", String.class);
        overrideGetter(iso639_1, languageCode, "id", String.class);
        overrideGetter(iso639_1, languageCode, "part2B", String.class);
        overrideGetter(iso639_1, languageCode, "part2T", String.class);
        overrideGetter(iso639_1, languageCode, "part1", String.class);
        overrideGetter(iso639_1, languageCode, "scope", Scope.class);
        overrideGetter(iso639_1, languageCode, "languageType", Type.class);
        overrideGetter(iso639_1, languageCode, "refName", String.class);
        overrideGetter(iso639_1, languageCode, "comment", String.class);
        overrideGetter(iso639_1, languageCode, "names", 
            model.ref(List.class).narrow(Name.class)
        );
        Map<String, JEnumConstant> generated = new TreeMap<>();
        LanguageCode.stream()
            .filter(lc -> lc.part1() != null)
            .forEach(lc -> {
                JEnumConstant enumConstant = generated.computeIfAbsent(lc.part1(), (k) -> iso639_1.enumConstant(lc.part1()));
                enumConstant.javadoc()
                    .append(lc.part3() + " " + lc.refName() + " " + lc.names())
                ;
                }    
            );
        model.build(new FileCodeWriter(new File(absolutePath), false));
    }
    
    
    protected void overrideGetter(JDefinedClass iso639_1, JFieldVar languageCode, String name, Class<?> returnType) {
        overrideGetter(iso639_1, languageCode, name, iso639_1.owner().ref(returnType));
        
    }
    protected void overrideGetter(JDefinedClass iso639_1, JFieldVar languageCode1, String name, JType languageCode) {
        JMethod toString = iso639_1.method(JMod.PUBLIC, languageCode, name);
        toString.annotate(Override.class);
        toString.body()._return(
            JExpr._this().invoke("getLanguageCode").invoke(name));
    }
}
  