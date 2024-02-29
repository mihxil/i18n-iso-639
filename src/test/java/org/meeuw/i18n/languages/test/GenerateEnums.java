package org.meeuw.i18n.languages.test;

import com.sun.codemodel.*;
import com.sun.codemodel.writer.FileCodeWriter;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.meeuw.i18n.languages.*;


public class GenerateEnums {
    String path = "src/main/java";
    File file = new File(path);
    String absolutePath = file.getAbsolutePath();

    @Test
    public void create639_1() throws JClassAlreadyExistsException, IOException {
        
        System.out.println("File path : " + absolutePath);

        JCodeModel model = new JCodeModel();
        JDefinedClass iso639_1 = model._class("org.meeuw.i18n.languages.ISO_639_1_Code", ClassType.ENUM);
        iso639_1._implements(LanguageCode.class);

        iso639_1.javadoc()
            .append("This class is automatically generated.")
            .append("It contains the ISO 639-1 language codes.");
        


        JFieldVar languageCode = iso639_1.field(JMod.PRIVATE, ISO_639_3_Code.class, "languageCode");

        JMethod constructor = iso639_1.constructor(JMod.NONE);
        JBlock body = constructor.body();
       
        JMethod getter = iso639_1.method(JMod.PUBLIC, LanguageCode.class, "getLanguageCode");
        JFieldRef ref = JExpr._this().ref(languageCode);

        getter.body()._if(ref.eq(JExpr._null()))._then().assign(
            JExpr._this().ref(languageCode), 
            model.ref(ISO_639_3_Code.class)
                .staticInvoke("getByPart1").arg(JExpr._this().invoke("name")).invoke("orElse").arg(JExpr._null()));
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
        ISO_639_3_Code.stream()
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
    
    @Test
    public void create639_5() throws JClassAlreadyExistsException, IOException, ClassNotFoundException {
        
        JCodeModel model = new JCodeModel();
        JDefinedClass iso639_5 = model._class("org.meeuw.i18n.languages.LanguageFamilyCode", ClassType.ENUM);
        iso639_5._implements(ISO_639_Code.class);

        iso639_5.javadoc()
            .append("This class is automatically generated.")
            .append("It contains the ISO 639-5 language codes.");
        
        JFieldVar uri = iso639_5.field(JMod.PRIVATE | JMod.FINAL, URI.class, "uri");

        
        JClass narrowedMap = model.ref(Map.class).narrow(String.class, String.class
        );
        JFieldVar labels = iso639_5.field(JMod.PRIVATE | JMod.FINAL, narrowedMap, "labels");
        
        labels.init(JExpr._new(model.ref(HashMap.class).narrow(String.class, String.class)));

        JMethod constructor = iso639_5.constructor(JMod.NONE);
        JVar uriParam = constructor.param(String.class, "uri");
        JVar labelEnglishParam = constructor.param(String.class, "labelEnglish");
        JVar labelFrenshParam = constructor.param(String.class, "labelFrench");
        JBlock body = constructor.body();
        JType jType = model._ref(URI.class);
        body.assign(JExpr._this().ref(uri), jType.boxify().staticInvoke("create").arg(uriParam));
        body.directStatement("labels.put(\"en\", labelEnglish);");
        body.directStatement("labels.put(\"fr\", labelFrench);");


        {
            JMethod code = iso639_5.method(JMod.PUBLIC, String.class, "code");
            code.annotate(Override.class);
            code.body()._return(JExpr._this().invoke("name"));
        }
        
        {
            JMethod scope = iso639_5.method(JMod.PUBLIC, Scope.class, "scope");
            scope.annotate(Override.class);
            scope.body()._return(model.ref(Scope.class).staticRef(Scope.FAMILY.name()));
        }
        
        {
            JMethod languageType = iso639_5.method(JMod.PUBLIC, Type.class, "languageType");
            languageType.annotate(Override.class);
            languageType.body()._return(model.ref(Type.class).staticRef(Type.L.name()));
        }
        
        
        {
            JMethod uriM = iso639_5.method(JMod.PUBLIC, URI.class, "uri");
            uriM.body()._return(uri);
        }
        
        {
            JMethod refName = iso639_5.method(JMod.PUBLIC, String.class, "refName");
            refName.annotate(Override.class);
            refName.body()._return(labels.invoke("get").arg("en"));
        }
        
        {
            JMethod labelsM = iso639_5.method(JMod.PUBLIC, narrowedMap, "labels");
            JClass collections = model.ref(Collections.class);
            labelsM.body()._return(collections.staticInvoke("unmodifiableMap").arg(labels));
        }
        
        
        
        
        try (InputStream inputStream = ISO_639_3_Code.class.getResourceAsStream("/iso639-5.tsv");
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            inputStreamReader.readLine(); // skipheader;
            while (true) {
                String l = inputStreamReader.readLine();
                if (l == null) {
                    break;
                }
                String[] line = l.split("\t");
                JEnumConstant enumConstant = iso639_5.enumConstant(line[1]);
                enumConstant.arg(JExpr.lit(line[0]));
                enumConstant.arg(JExpr.lit(line[2]));
                enumConstant.arg(JExpr.lit(line[3]));
            }
        }
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
  