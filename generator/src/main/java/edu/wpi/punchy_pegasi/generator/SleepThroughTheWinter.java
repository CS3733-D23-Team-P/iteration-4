package edu.wpi.punchy_pegasi.generator;

import edu.wpi.punchy_pegasi.generator.schema.TableType;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.signature.SignatureWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SleepThroughTheWinter {

    private static final Map<Class<?>, String> classToPostgres = new HashMap<>() {{
        put(Long.class, "bigint");
        put(Integer.class, "int");
        put(String.class, "varchar");
        put(UUID.class, "uuid DEFAULT gen_random_uuid()");
        put(List.class, "varchar ARRAY");
        put(LocalDate.class, "date NOT NULL");
        put(Instant.class, "timestamptz NOT NULL");
    }};
    private static final Map<Class<?>, String> objectFromString = new HashMap<>() {{
        put(Long.class, "Long.parseLong(value)");
        put(Integer.class, "Integer.parseInt(value)");
        put(String.class, "value");
        put(UUID.class, "UUID.fromString(value)");
        put(List.class, "new java.util.ArrayList<>(java.util.Arrays.asList(value.split(\"\\\\s*,\\\\s*\")))");
        put(LocalDate.class, "LocalDate.parse(value)");
        put(Instant.class, "Instant.parse(value)");
    }};
    private static final Map<Class<?>, String> objectToString = new HashMap<>() {{
        put(Long.class, "Long.toString(value)");
        put(Integer.class, "Integer.toString(value)");
        put(String.class, "value");
        put(UUID.class, "value.toString()");
        put(List.class, "String.join(\", \", value)");
        put(LocalDate.class, "value.toString()");
        put(Instant.class, "value.toString()");
    }};
    /***
     * Currently generates from the TableType enum, and supports any type which has one field with the @SchemaID
     * annotation, consists of only basic Objects, List<String>, Enum for parameters, and whose constructor's have
     * parameter names which EXACTLY match the corresponding field names.
     * @param args
     * @throws IOException
     */
    private static boolean cachedMode = true;

    private static String objectFromString(Class<?> clazz) {
        if (clazz.isEnum()) {
            return clazz.getSimpleName() + ".valueOf(value)";
        } else {
            return objectFromString.get(clazz);
        }
    }

    private static String objectToString(Class<?> clazz, String getter) {
        if (clazz.isEnum()) {
            return getter + ".name()";
        } else {
            return objectToString.get(clazz).replace("value", getter);
        }
    }

    private static String daoImplSuffix() {
        return cachedMode ? "CachedDaoImpl" : "DaoImpl";
    }

    private static String genericDaoImplPath() {
        return "src/main/java/edu/wpi/punchy_pegasi/generator/GenericRequestEntry" + daoImplSuffix() + ".java";
    }

    /**
     * Returns a list containing one parameter name for each argument accepted
     * by the given constructor. If the class was compiled with debugging
     * symbols, the parameter names will match those provided in the Java source
     * code. Otherwise, a generic "arg" parameter name is generated ("arg0" for
     * the first argument, "arg1" for the second...).
     * <p>
     * This method relies on the constructor's class loader to locate the
     * bytecode resource that defined its class.
     *
     * @param constructor
     * @return
     * @throws IOException
     */
    private static List<String> getParameterNames(Constructor<?> constructor) throws IOException {
        Class<?> declaringClass = constructor.getDeclaringClass();
        ClassLoader declaringClassLoader = declaringClass.getClassLoader();

        Type declaringType = Type.getType(declaringClass);
        String constructorDescriptor = Type.getConstructorDescriptor(constructor);
        String url = declaringType.getInternalName() + ".class";

        InputStream classFileInputStream = declaringClassLoader.getResourceAsStream(url);
        if (classFileInputStream == null) {
            throw new IllegalArgumentException("The constructor's class loader cannot find the bytecode that defined the constructor's class (URL: " + url + ")");
        }

        ClassNode classNode;
        try {
            classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classFileInputStream);
            classReader.accept(classNode, 0);
        } finally {
            classFileInputStream.close();
        }

        @SuppressWarnings("unchecked") List<MethodNode> methods = classNode.methods;
        for (MethodNode method : methods) {
            if (method.name.equals("<init>") && method.desc.equals(constructorDescriptor)) {
                Type[] argumentTypes = Type.getArgumentTypes(method.desc);
                List<String> parameterNames = new ArrayList<String>(argumentTypes.length);

                @SuppressWarnings("unchecked") List<LocalVariableNode> localVariables = method.localVariables;
                for (int i = 0; i < argumentTypes.length; i++) {
                    // The first local variable actually represents the "this" object
//                    transform(localVariables.get(i + 1).signature);
                    parameterNames.add(localVariables.get(i + 1).name);
                }

                return parameterNames;
            }
        }

        return null;
    }

    public static String firstLower(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    public static String firstUpper(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private static List<Field> getFieldsRecursively(Class<?> clazz) {
        //use getDeclaredFields() to get all fields in this class
        var fields = new java.util.ArrayList<>(Arrays.stream(clazz.getDeclaredFields()).toList());
        if (!clazz.getSuperclass().equals(Object.class)) fields.addAll(0, getFieldsRecursively(clazz.getSuperclass()));
        //combine the 2 arrays
        return fields;
    }

    private static String classResultSetConstructor(Class<?> clazz) throws IOException {
        var ctor = Arrays.stream(clazz.getConstructors()).max(Comparator.comparing(t -> t.getParameterTypes().length)).get();
        var ctorParameterNames = getParameterNames(ctor);

        StringBuilder constructor = new StringBuilder();
        constructor.append("new ").append(clazz.getSimpleName()).append("(\n");
        for (int i = 0; i < ctor.getParameterCount(); i++) {
            var type = ctor.getParameterTypes()[i];
            var column = ctorParameterNames.get(i);
            constructor.append("                    ");
            if (type.isEnum())
                constructor.append(type.getCanonicalName())
                        .append(".valueOf(rs.getString(\"").append(column).append("\"))");
            else if (List.class.isAssignableFrom(type))
                constructor.append("java.util.Arrays.asList((")//.append(type.getCanonicalName())
                        .append("String[])rs.getArray(\"").append(column).append("\").getArray())");
            else if (type.equals(Date.class))
                constructor.append("rs.getDate(\"").append(column).append("\").toLocalDate()");
            else if (type.equals(Instant.class))
                constructor.append("rs.getTimestamp(\"").append(column).append("\").toInstant()");
            else
                constructor.append("rs.getObject(\"").append(column).append("\", ")
                        .append(type.getCanonicalName()).append(".class)");
            constructor.append(",\n");
        }
        constructor.setLength(constructor.length() - 2);
        constructor.append(");");

        return constructor.toString();
    }

    public static String camelToSnake(String str) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        str = str.replaceAll(regex, replacement).toLowerCase();
        return str;
    }

    private static String generateEnum(Class<?> clazz, List<Field> fields) {
        return
                """
                        @lombok.RequiredArgsConstructor
                        public enum Field implements IField< """ + clazz.getCanonicalName() + """
                        >{
                        """ + "        " + String.join(",\n        ", fields.stream().map(f -> camelToSnake(f.getName()).toUpperCase() + "(\"" + f.getName() + "\", " + fieldIsID(f)  + "," + fieldIsUnique(f) + ")").toList()) + """
                        ;
                                @lombok.Getter
                                private final String colName;
                                @lombok.Getter
                                private final boolean primaryKey;
                                @lombok.Getter
                                private final boolean unique;
                                public Object getValue(""" + clazz.getCanonicalName() + """
                         ref){
                            return ref.getFromField(this);
                        }
                        public String getValueAsString(""" + clazz.getCanonicalName() + """
                         ref){
                            return ref.getFromFieldAsString(this);
                        }
                            public void setValueFromString(""" + clazz.getCanonicalName() + """
                         ref, String value){
                                    ref.setFieldFromString(this, value);
                                }
                                public int oridinal(){
                                    return ordinal();
                                }
                            }
                            public Object getFromField(Field field) {
                                return switch (field) {
                        """ + "            " + String.join("            ",
                        fields.stream().map(f -> "case " + camelToSnake(f.getName()).toUpperCase() + " -> get" + firstUpper(f.getName()) + "();\n").toList()) + """
                                };
                            }
                            public void setFieldFromString(Field field, String value) {
                                switch (field) {
                        """ + "            " + String.join("            ",
                        fields.stream().map(f -> "case " + camelToSnake(f.getName()).toUpperCase() + " -> set" + firstUpper(f.getName()) + "(" + objectFromString(f.getType()) + ");\n").toList()) + """
                                };
                            }
                            public String getFromFieldAsString(Field field) {
                                return switch (field) {
                        """ + "            " + String.join("            ",
                        fields.stream().map(f -> "case " + camelToSnake(f.getName()).toUpperCase() + " -> " + objectToString(f.getType(), "get" + firstUpper(f.getName()) + "()") + ";\n").toList()) + """
                                };
                            }
                        """;
    }

    private static boolean fieldIsID(Field field) {
        // locate the @SchemaID annotation
        return Arrays.stream(field.getAnnotations()).anyMatch(a -> a.annotationType() == SchemaID.class);
    }

    private static boolean fieldIsUnique(Field field) {
        // locate the @Unique annotation
        return Arrays.stream(field.getAnnotations()).anyMatch(a -> a.annotationType() == Unique.class);
    }

    private static String generateTableInit(TableType tt) {
        var clazz = tt.getClazz();
        var tableName = tt.name().toLowerCase();
        var sequenceName = tt.name().toLowerCase() + "_id_seq";
        var parentClass = clazz.getSuperclass().equals(Object.class) ? null : clazz.getSuperclass();
        var parentTable = Arrays.stream(TableType.values()).filter(t -> t.getClazz() == parentClass).toList();

        var inheritanceString = parentClass != null && parentTable.size() == 1
                ? " INHERITS (" + parentTable.get(0).name().toLowerCase() + ")"
                : "";

        var classFields = inheritanceString.isBlank()
                ? getFieldsRecursively(clazz)
                : Arrays.stream(clazz.getDeclaredFields()).toList();


//        var classFields = getFieldsRecursively(clazz);
        var idField = classFields.stream().filter(SleepThroughTheWinter::fieldIsID).findFirst();
        var tableColumns = classFields.stream().map(f -> {
            var keyText = fieldIsID(f) ?
                    f.getType() == Long.class ?
                            " DEFAULT nextval('" + sequenceName + "') PRIMARY KEY"
                            : " PRIMARY KEY"
                    : "";
            var uniqueText = fieldIsUnique(f) ? " UNIQUE" : "";
            if (f.getType().isEnum())
                return "" + f.getName() + " varchar" + keyText + " NOT NULL";
            return "" + f.getName() + " " + classToPostgres.get(f.getType()) + uniqueText + keyText;
        }).toList();

        var tableListener = String.format("""
                CREATE OR REPLACE FUNCTION notify_%1$s_update() RETURNS TRIGGER AS $$
                    DECLARE
                        row RECORD;
                    output JSONB;
                    BEGIN
                    IF (TG_OP = 'DELETE') THEN
                      row = OLD;
                    ELSE
                      row = NEW;
                    END IF;
                    -- encode data as json inside a string
                    output = jsonb_build_object('tableType', '%2$s', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
                    PERFORM pg_notify('%1$s_update',output::text);
                    RETURN NULL;
                    END;
                $$ LANGUAGE plpgsql;
                CREATE OR REPLACE TRIGGER trigger_%1$s_update
                  AFTER INSERT OR UPDATE OR DELETE
                  ON %1$s
                  FOR EACH ROW
                  EXECUTE PROCEDURE notify_%1$s_update();
                """, tableName, tt.name());

        return idField.isPresent() && idField.get().getType() == Long.class ?
                String.format("""
                        %1$s(%2$s.class, \"\"\"
                        DO $$
                        BEGIN
                          IF to_regclass('%3$s') IS NULL THEN
                            CREATE SEQUENCE %4$s;
                            CREATE TABLE %3$s
                            (
                              %5$s
                            );
                            ALTER SEQUENCE %4$s OWNED BY %3$s.%6$s;
                          END IF;
                        END $$;
                        %7$s
                        \"\"\", %2$s.Field.class)""", tt.name(), clazz.getCanonicalName(), tableName, sequenceName, String.join(",\n      ", tableColumns), idField.get().getName(), tableListener)
                : String.format("""
                        %1$s(%2$s.class, \"\"\"
                        CREATE TABLE IF NOT EXISTS %3$s
                        (
                          %4$s
                        )%5$s;
                        %6$s
                        \"\"\", %2$s.Field.class)
                        """, tt.name(), clazz.getCanonicalName(),
                tableName,
                String.join(",\n  ", tableColumns),
                inheritanceString,
                tableListener);
    }

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }

    public static List<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet()).stream().toList();
    }

    private static void generateTable() throws IOException {
        var schemaSourcePath = Paths.get("generator/src/main/java/edu/wpi/punchy_pegasi/generator/schema", TableType.class.getSimpleName() + ".java");
        var schemaDestPath = Paths.get("src/main/java/edu/wpi/punchy_pegasi/schema", TableType.class.getSimpleName() + ".java");
        var sourceFileText = new String(Files.readAllBytes(schemaSourcePath));

        for (var tt : TableType.values())
            sourceFileText = sourceFileText.replaceFirst(tt.name() + "\\([^\\)]*\\)",
                    Matcher.quoteReplacement(generateTableInit(tt)));
        Files.writeString(schemaDestPath, sourceFileText.replaceAll("edu\\.wpi\\.punchy_pegasi\\.generator\\.schema", "edu.wpi.punchy_pegasi.schema"));
    }

    private static void generateEntry(Class<?> entryClass) throws IOException, IllegalStateException {
        var schemaSourcePath = Paths.get("generator/src/main/java/edu/wpi/punchy_pegasi/generator/schema", entryClass.getSimpleName() + ".java");
        var schemaDestPath = Paths.get("src/main/java/edu/wpi/punchy_pegasi/schema", entryClass.getSimpleName() + ".java");
        var daoImplSourcePath = Paths.get(genericDaoImplPath());
        var daoImplDestPath = Paths.get("src/main/java/edu/wpi/punchy_pegasi/generated", entryClass.getSimpleName() + daoImplSuffix() + ".java");
        var sourceFileText = new String(Files.readAllBytes(schemaSourcePath))
                .replaceAll("edu\\.wpi\\.punchy_pegasi\\.generator\\.schema", "edu.wpi.punchy_pegasi.schema");

        var classFields = getFieldsRecursively(entryClass);

        // gen schema
        var schemaFileText = sourceFileText // remove @SchemaID annotations
//                .replaceAll("@SchemaID[.\n]*", "")
//                .replaceAll("@Unique[.\n]*", "")
                .replaceAll("import edu\\.wpi\\.punchy_pegasi\\.generator\\.SchemaID;[.\n]*", "import edu.wpi.punchy_pegasi.backend.SchemaID;")
                .replaceAll("import edu\\.wpi\\.punchy_pegasi\\.generator\\.Unique;[.\n]*", "import edu.wpi.punchy_pegasi.backend.Unique;").trim();

        var schemaLines = new ArrayList<>(schemaFileText.lines().toList());
        schemaLines.add(schemaLines.size() - 1, generateEnum(entryClass, classFields));

        schemaFileText = schemaLines.stream().reduce("", (a, b) -> a + "\n" + b)
                .replaceAll("\\.generator\\.", ".");

        var with = entryClass.getSuperclass().equals(Object.class) ? "    @lombok.With\n" : "";
        for (var field : classFields) {
            schemaFileText = schemaFileText.replaceAll("(.*(private|protected|public).*" + field.getName() + ";)", with + "    @com.jsoniter.annotation.JsonProperty(\"" + field.getName().toLowerCase() + "\")\n$1");
        }

        schemaFileText = schemaFileText.trim();


        Files.writeString(schemaDestPath, schemaFileText);
        // gen impl
        var typeOptional = Arrays.stream(TableType.values()).filter(tt -> tt.getClazz() == entryClass).findFirst();
        if (typeOptional.isEmpty()) {
            Files.writeString(schemaDestPath, sourceFileText);
            throw new IllegalStateException("No table type found for " + entryClass.getCanonicalName());
        }
        var tt = typeOptional.get();

        var idField = getIdField(entryClass);

        var ClassText = entryClass.getSimpleName();
        // set first letter lowercase
        var classText = firstLower(ClassText);

        var classFieldsText = classFields.stream().map(Field::getName).toList();
        var ClassFieldsGet = classFieldsText.stream().map(f -> classText + ".get" + firstUpper(f) + "()").toList();

        var idFieldText = idField.getName();
        var idFieldType = idField.getType().getCanonicalName();


        var template = new String(Files.readAllBytes(daoImplSourcePath));
        var implFileText = template.replaceAll("edu\\.wpi\\.punchy_pegasi\\.generator", "edu.wpi.punchy_pegasi.generated")
                .replaceAll("/\\*FacadeClassName\\*/", "")
                .replaceAll("GenericRequestEntry", ClassText).replaceAll("genericRequestEntry", classText)
                .replaceAll("TableType\\.GENERIC", "TableType." + tt.name())
                .replaceAll("/\\*fields\\*/", String.join(", ", classFieldsText.stream().map(v -> "\"" + v + "\"").toList()))
                .replaceAll("(\\S+)/\\*fromResultSet\\*/[^(null;)]*null;", "$1 = " + classResultSetConstructor(entryClass))
                .replaceAll("/\\*getFields\\*/", String.join(", ", ClassFieldsGet))
                .replaceAll("\"\"/\\*idField\\*/", "\"" + idFieldText + "\"")
                .replaceAll("String/\\*idFieldType\\*/", idFieldType)
                .replaceAll("\"(\\S+)\"/\\*getID\\*/", "$1.get" + firstUpper(idFieldText) + "()")
                .replaceAll("\\.generator\\.", ".").trim();

        var lines = new java.util.ArrayList<>(implFileText.lines().toList());
        implFileText = lines.stream().reduce("", (a, b) -> a + "\n" + b).trim();
        Files.writeString(daoImplDestPath, implFileText);
    }

    private static Field getIdField(Class<?> clazz) throws IllegalStateException {
        var classFields = getFieldsRecursively(clazz);
        var idFields = classFields.stream().filter(SleepThroughTheWinter::fieldIsID).toList(); // locate id field with @SchemaID
        if (idFields.size() < 1) {
            // check if no id annotation (@SchemaID) is present
            throw new IllegalStateException("No id field found for " + clazz.getCanonicalName());
        }
        if (idFields.size() > 1) {
            // check if more than one id annotation (@SchemaID) is present
            throw new IllegalStateException("More than one id field found for " + clazz.getCanonicalName());
        }
        return idFields.get(0);
    }

//    private static boolean checkUniqueFields(Class<?> clazz) throws IllegalStateException {
//        var classFields = getFieldsRecursively(clazz);
//        var uniqueFields = classFields.stream().filter(SleepThroughTheWinter::fieldIsUnique).toList(); // locate id field with @Unique
//        if(uniqueFields.size() < 1){
//            return false;
//        } else{
//            return true;
//        }
//    }

//    private static List<Field> getUniqueFields(Class<?> clazz) throws IllegalStateException {
//        var classFields = getFieldsRecursively(clazz);
//        var uniqueFields = classFields.stream().filter(SleepThroughTheWinter::fieldIsUnique).toList(); // locate id field with @Unique
//        return uniqueFields;
//    }

    private static StringBuilder generateFacadeEntry(Class<?> entryClass) throws IOException, IllegalStateException {
        var daoImplSourcePath = Paths.get(genericDaoImplPath());
        var template = new String(Files.readAllBytes(daoImplSourcePath));
        StringBuilder resultText = new StringBuilder();

        var ClassName = entryClass.getSimpleName();
        if (ClassName.equals("GenericRequestEntry")) return resultText;  // skip GenericRequestEntry
        var className = firstLower(ClassName);
        var id_field = getIdField(entryClass);
//        var id_field_name = id_field.getName();
        var id_field_type = id_field.getType().getCanonicalName();

        Pattern header = Pattern.compile("(public\\s+[^{]+\\/*\\*FacadeClassName\\*\\/\\s*\\([^)]*\\)\\s*)\\{");  // extract all public method header
        Pattern name = Pattern.compile("(\\w+)\\s*/\\*FacadeClassName\\*/");  // extract fcn name
        Pattern args = Pattern.compile("(\\([^()]*\\))");  // extract fcn args
        Pattern argsName = Pattern.compile("(?:,\\s*)?(\\w+)\\s*(?:=[^,)]+)?(?=[,)])");  // extract fcn args name
        Pattern returnType = Pattern.compile("public\\s*(\\w*)");  // extract return type
        Matcher matcher = header.matcher(template);

        while (matcher.find()) {
            var fcnHeader = matcher.group(1);

            Matcher matcherName = name.matcher(fcnHeader);
            if (!matcherName.find()) {
                throw new IllegalStateException("No function name found for " + ClassName);
            }
            var fcnName = matcherName.group(1);

            fcnHeader = fcnHeader.replaceAll("/\\*FacadeClassName\\*/", ClassName)
                    .replaceAll("String/\\*idFieldType\\*/", id_field_type)
                    .replaceAll("GenericRequestEntry", ClassName).replaceAll("genericRequestEntry", className);

            Matcher matcherArgs = args.matcher(fcnHeader);
            var argsField = "";
            if (matcherArgs.find()) {
                argsField = matcherArgs.group(1);
            } else {
                throw new IllegalStateException("No function args found for " + ClassName);
            }

            var fcnArgs = "";
            var sb = new StringBuilder();
            Matcher matcherArgsName = argsName.matcher(argsField);
            while (matcherArgsName.find()) {
                sb.append(matcherArgsName.group(1)).append(", ");
            }
            if (sb.length() > 0)
                fcnArgs = sb.substring(0, sb.length() - 2);

            Matcher matcherReturnType = returnType.matcher(fcnHeader);
            if (!matcherReturnType.find()) {
                throw new IllegalStateException("No return type found for " + ClassName);
            } else {
                if (matcherReturnType.group(1).equals("void")) {
                    resultText.append("\t")
                            .append(fcnHeader)
                            .append("{\n")
                            .append(String.format("""
                                    \t\t%1$s.%2$s(%3$s);
                                    \t}
                                    """, className + "Dao", fcnName, fcnArgs
                            ));
                } else {
                    resultText.append("\t")
                            .append(fcnHeader)
                            .append("{\n")
                            .append(String.format("""
                                    \t\treturn %1$s.%2$s(%3$s);
                                    \t}
                                    """, className + "Dao", fcnName, fcnArgs
                            ));
                }
            }
        }
        return resultText;
    }

    private static String getDaoByClass() {
        return """
                    public <K, T, C> IDao<K, T, C> getDaoByClass(Class<T> clazz) {
                        if (clazz == null) return null;
                """ + "        " + String.join("\n        ", Arrays.stream(TableType.values()).map(TableType::getClazz).map(c -> "else if (clazz == " + c.getCanonicalName() + ".class) return (IDao<K, T, C>)" + firstLower(c.getSimpleName()) + "Dao;").toList()) + """
                                            
                        else return null;
                    }
                """;
    }

    private static void generateFacade() throws IOException, IllegalStateException {
        var facadeDestPath = Paths.get("src/main/java/edu/wpi/punchy_pegasi/generated/Facade.java");
        var facadeSourcePath = Paths.get("generator/src/main/java/edu/wpi/punchy_pegasi/generator/schema/Facade.java");

        StringBuilder sbDaoDec = new StringBuilder();
        StringBuilder sbDaoInit = new StringBuilder();
        for (var clazz : Arrays.stream(TableType.values()).map(TableType::getClazz).toList()) {
            try {
                var ClassName = clazz.getSimpleName();
                var className = firstLower(ClassName);
                sbDaoDec.append("\tprivate final " + ClassName + daoImplSuffix() + " " + className + "Dao;\n");
                sbDaoInit.append("\t\t" + className + "Dao = new " + ClassName + daoImplSuffix() + "(dbController);\n");
            } catch (Exception e) {
                System.err.println("Failed to initialize Dao Impl for " + clazz.getCanonicalName() + ": " + e.getMessage());
            }
        }
        sbDaoDec.append(getDaoByClass());
        var template = new String(Files.readAllBytes(facadeSourcePath))
                .replaceAll("}\n*$", "")
                .replaceAll("/\\*Dao Declarations\\*/", sbDaoDec.toString())
                .replaceAll("/\\*Dao Initialization\\*/", sbDaoInit.toString())
                .replaceAll("\\*/", "")
                .replaceAll("/\\*", "")
                .replaceAll("package edu\\.wpi\\.punchy_pegasi\\.generator\\.schema;",
                        """
                                package edu.wpi.punchy_pegasi.generated;
                                                                
                                import edu.wpi.punchy_pegasi.schema.*;
                                import edu.wpi.punchy_pegasi.backend.PdbController;
                                import java.util.Map;
                                import javafx.collections.ObservableList;
                                import javafx.collections.ObservableMap;
                                import io.github.palexdev.materialfx.controls.MFXTableView;
                                import java.util.function.Consumer;
                                import java.util.Optional;
                                """);
        StringBuilder sbTemplate = new StringBuilder();
        sbTemplate.append(template);
        for (var clazz : Arrays.stream(TableType.values()).map(TableType::getClazz).toList()) {
            try {
                sbTemplate.append(generateFacadeEntry(clazz));
            } catch (Exception e) {
                System.err.println("Failed to generate schema for " + clazz.getCanonicalName() + ": " + e.getMessage());
            }
        }
        var facadeFileText = sbTemplate.append("}").toString();
        Files.writeString(facadeDestPath, facadeFileText);
    }

    public static void main(String[] args) throws IOException {
        // generate destination folders
        var schemaFolder = new File("src/main/java/edu/wpi/punchy_pegasi/schema");
        if (!schemaFolder.exists()) schemaFolder.mkdir();
        var generatedFolder = new File("src/main/java/edu/wpi/punchy_pegasi/generated");
        if (!generatedFolder.exists()) generatedFolder.mkdir();
        var schemaSourceDir = Paths.get("generator/src/main/java/edu/wpi/punchy_pegasi/generator/schema/");
        var schemaDestDir = Paths.get("src/main/java/edu/wpi/punchy_pegasi/schema/");
        for (boolean mode : new boolean[]{!cachedMode, cachedMode}) {
            cachedMode = mode;
            // default, copy files over
            try (var fileWalk = Files.walk(schemaSourceDir)) {
                for (var schemaSourceFile : fileWalk.filter(Files::isRegularFile).toList()) {
                    var schemaDestFile = schemaDestDir.resolve(schemaSourceDir.relativize(schemaSourceFile));
                    Files.writeString(schemaDestFile, new String(Files.readAllBytes(schemaSourceFile))
                            .replaceAll("edu\\.wpi\\.punchy_pegasi\\.generator\\.schema", "edu.wpi.punchy_pegasi.schema"));
                }
            }
        }
        generateFacade();
        generateTable();
        for (boolean mode : new boolean[]{!cachedMode, cachedMode}) {
            cachedMode = mode;
            for (var clazz : Arrays.stream(TableType.values()).map(TableType::getClazz).toList()) {
                try {
                    generateEntry(clazz);
                } catch (Exception e) {
                    System.err.println("Failed to generate schema for " + clazz.getCanonicalName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public static String transform(String signature) {
        SignatureReader reader = new SignatureReader(signature);
        SignatureWriter writer = new SignatureWriter();

        SignatureVisitor visitor = new SignatureVisitor(Opcodes.ASM9) {
            StringBuilder sb = new StringBuilder();

            @Override
            public void visitClassType(String name) {
                sb.append(name.replace('/', '.'));
                sb.append('.');
            }

            @Override
            public void visitTypeArgument() {
                sb.append('<');
            }

            @Override
            public SignatureVisitor visitTypeArgument(char wildcard) {
                sb.append(wildcard);
                return this;
            }

            @Override
            public void visitEnd() {
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1); // remove the last '.'
                }
                sb.append('>');
                writer.visitClassType(sb.toString());
                sb.setLength(0);
            }
        };

        reader.accept(visitor);

        return writer.toString();
    }
}
