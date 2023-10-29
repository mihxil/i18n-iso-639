import com.sun.codemodel.*
import org.meeuw.i18n.languages.Scope
import org.meeuw.i18n.languages.Type

import java.nio.charset.StandardCharsets
import java.util.logging.Logger

/**
 * Nice idea, but the generated java class is too big.
 * 
 * Can't be solved anyway decently, because we need to get the class size below 64k. And this still produces something like 3 times too much.
 * 
 * We just provided the data as a resource now. And read it in dynamically.
 */

record iso_639_3(
        String id,
        String part2B,
        String part2T,
        String part1,
        Scope scope,
        Type type,
        String name,
        String comment
) {
  
}



static void createEnums(String source, String dest) {
    final Set<String> reserved = [
    "abstract", "assert", "boolean", "break","byte","case","catch","char","class","const","default","do","double","else","enum","extends","false","final","finally","float","for","goto","if","implements","import","instanceof","int","interface","long","native","new","null","package","private","protected","public","return","short","static","strictfp","super","switch","synchronized","this","throw","throws","transient","true","try","void","volatile","while"]
    final Logger logger = Logger.getLogger("")

    logger.info("Creating enums from " + source)
    def file = new File(source)

    JCodeModel model = new JCodeModel()
    model._class("org.meeuw.i18n.languages.ISO_639_3", ClassType.ENUM).with {
        annotate(SuppressWarnings).param("value", "UnnecessaryUnicodeEscape")
        javadoc()
                .append("This class is automatically generated from <a href=\"" + source + "\">" + source + "</a>.")
        
        var allConstructor = constructor(JMod.NONE)
        var allConstructorBody = allConstructor.body()
        
        iso_639_3.class.getRecordComponents().each {
            JFieldVar field = field(JMod.PRIVATE | JMod.FINAL, it.type, it.name)
            logger.info("Created field " + field.name());
            method(JMod.PUBLIC, it.type, "get" + it.name.capitalize()).with {
                body()._return(field)
                javadoc().append("Returns the ${it.name} of this language".toString())
            }
            allConstructorBody.assign(
                    JExpr._this().ref(field), 
                    allConstructor.param(it.type, it.name)
            )
        }
        var defaultConstructor1 = constructor(JMod.NONE)
        defaultConstructor1.with {
            body().with {
                def invoke = invoke("this")
                invoke.arg(defaultConstructor1.param(String.class, "id"))
                invoke.arg(JExpr._null()) // part2B
                invoke.arg(JExpr._null()) // part2T
                invoke.arg(JExpr._null()) // part1
                invoke.arg(model.ref(Scope.class).staticRef(Scope.I.name())) // scope
                invoke.arg(model.ref(Type.class).staticRef(Type.L.name())) // type
                invoke.arg(defaultConstructor1.param(String.class, "name"))
                invoke.arg(JExpr._null())
            }
        }
        
        var defaultConstructor2 = constructor(JMod.NONE)
        defaultConstructor2.with {
            body().with {
                def invoke = invoke("this");
                invoke.arg(defaultConstructor2.param(String.class, "id"))
                invoke.arg(JExpr._null()) // part2B
                invoke.arg(JExpr._null()) // part2T
                invoke.arg(JExpr._null()) // part1
                invoke.arg(model.ref(Scope.class).staticRef(Scope.I.name())) // scope
                invoke.arg(defaultConstructor2.param(Type.class, "type")) // type
                invoke.arg(defaultConstructor2.param(String.class, "name"))
                invoke.arg(JExpr._null())
            }
        }
        
        var defaultConstructor3 = constructor(JMod.NONE)
        defaultConstructor3.with {
            body().with {
                def invoke = invoke("this");
                invoke.arg(defaultConstructor3.param(String.class, "id"))
                invoke.arg(defaultConstructor3.param(String.class, "part2B")) // part2B
                invoke.arg(defaultConstructor3.param(String.class, "part2T")) // part2T
                invoke.arg(defaultConstructor3.param(String.class, "part1")) // part1
                invoke.arg(model.ref(Scope.class).staticRef(Scope.I.name())) // scope
                invoke.arg(defaultConstructor3.param(Type.class, "type")) // type
                invoke.arg(defaultConstructor3.param(String.class, "name"))
                invoke.arg(JExpr._null())
            }
        }
        

        try (InputStream inputStream = new FileInputStream(file);
             BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        ) {
            inputStreamReader.readLine(); // skipheader;
            String line = inputStreamReader.readLine();
            while (line != null) {
                String[] split = line.split("\t");
                Scope scope = Scope.valueOf(split[4]);
                if (scope == Scope.I) {
                    scope = null;
                }
                Type type = Type.valueOf(split[5]);
                if (type == Type.L) {
                    type = null;
                }

                iso_639_3 found = new iso_639_3(
                        split[0],
                        split[1].length() > 0 ? split[1] : null,
                        split[2].length() > 0 ? split[2] : null,
                        split[3].length() > 0 ? split[3] : null,
                        scope,
                        type,
                        split[6].length() > 0 ? split[6] : null,
                        split.length == 8 ? split[7] : null);
                String id = found.id
                while (reserved.contains(id)) {
                    id = id + "_";
                }
                enumConstant(id).with {
                    iso_639_3.class.getRecordComponents().each{
                        if (found[it.name] == null) {
                           // arg(JExpr._null())
                        } else if (it.type == String.class) {
                            arg(JExpr.lit(found[it.name]))
                        } else {
                            arg(model.ref(it.type).staticRef(found[it.name].name()))
                        }
                        
                    }
                }
                
                line = inputStreamReader.readLine();
            }
        } catch (IOException e) {

        }
    }
    File dir = new File(dest)
    dir.mkdirs()
    logger.info("Writing to " + dir.getAbsolutePath())
    try {
        model.build(dir)
    } catch (Throwable e) {
        //logger.warning(e.getClass() + ":" + e.getMessage())
    }
    logger.info("Ready")
}

createEnums(args[0], args[1])