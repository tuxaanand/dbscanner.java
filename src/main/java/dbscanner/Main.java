package dbscanner;

import dbscanner.graph.DbSchema;
import dbscanner.graph.DbTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    private static final Map<String, String> REQUIRED_PARAMS = new HashMap<>();

    static {
        REQUIRED_PARAMS.put("url", "JDBC URL");
        REQUIRED_PARAMS.put("username", "Username");
        REQUIRED_PARAMS.put("password", "Password");
        REQUIRED_PARAMS.put("schema", "Schema to be scanned");
    }

    private static final Map<String, String> OPTIONAL_PARAMS = new HashMap<>();

    static {
        OPTIONAL_PARAMS.put("file", "Output file path");
        OPTIONAL_PARAMS.put("exclude-tables", "Tables to exclude - comma separated");
        OPTIONAL_PARAMS.put("statement", "Statement template");
    }

    public static void main(String[] args) {

        Map<String, String> options = parseOptions(args);

        try (Connection connection
                     = DriverManager.getConnection(options.get("url"),
                options.get("username"), options.get("password"))) {
            Scanner scanner = new Scanner(connection);
            final String schemaName = options.get("schema");
            final DbSchema dbSchema = scanner.getDbSchema(schemaName);
            final Map<String, DbTable> tables = dbSchema.getTables();
            List<String> truncationOrder = new ArrayList<>();
            Set<String> visitedTables = new HashSet<>();
            final String excludeOption = options.get("exclude-tables");
            if(excludeOption != null && !excludeOption.isEmpty()) {
                visitedTables.addAll(Arrays.asList(excludeOption.split(","))
                        .stream()
                        .map(String::trim)
                        .collect(Collectors.toSet()));
            }
            for (DbTable table : tables.values()) {
                    checkAndAddToList(truncationOrder, table, tables, visitedTables);
            }
            PrintStream out = System.out;
            if (options.containsKey("file")) {
                out = new PrintStream(new File(options.get("file")));
            }
            String statement = "TRUNCATE TABLE %1s.%2s";
            if(options.containsKey("statement")) {
                statement = options.get("statement");
            }
            for (String tab : truncationOrder) {
                out.printf(statement + ";\n", schemaName, tab);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {

        }
    }

    private static Map<String, String> parseOptions(String[] args) {
        Map<String, String> options = new HashMap<>();
        Pattern pattern = Pattern.compile("\\-(.*)=(.*)");
        for (String arg : args) {
            final Matcher matcher = pattern.matcher(arg);
            if (matcher.matches()) {
                options.put(matcher.group(1), matcher.group(2));
            }
        }
        boolean printUsage = false;
        for (Map.Entry<String, String> paramEntry : REQUIRED_PARAMS.entrySet()) {
            if (!options.containsKey(paramEntry.getKey())) {
                System.out.printf("Required parameter %s missing\n", paramEntry.getKey());
                printUsage = true;
            }
        }
        if (printUsage) {
            printUsage();
            System.exit(0);
        }
        return options;
    }

    private static void printUsage() {
        StringBuilder usage = new StringBuilder();
        for (Map.Entry<String, String> paramEntry : REQUIRED_PARAMS.entrySet()) {
            usage.append(String.format(" -%s=<%s> (required) ", paramEntry.getKey(), paramEntry.getValue()));
        }
        for (Map.Entry<String, String> paramEntry : OPTIONAL_PARAMS.entrySet()) {
            usage.append(String.format(" -%s=<%s> (optional) ", paramEntry.getKey(), paramEntry.getValue()));
        }
        System.out.println("Usage:");
        System.out.println("\t" + Main.class.getName() + usage.toString());
    }

    private static void checkAndAddToList(List<String> truncationOrder, DbTable table, Map<String, DbTable> tables, Set<String> visitedTables) {
        if (visitedTables.contains(table.getName())) {
            return;
        }
        visitedTables.add(table.getName());
        final Set<ReferredColumn> referredColumns = table.getReferredColumns();
        if (referredColumns != null && !referredColumns.isEmpty()) {
            for (ReferredColumn referredColumn : referredColumns) {
                final DbTable referredTable = tables.get(referredColumn.getReferredByTable());
                if (!truncationOrder.contains(referredTable.getName())) {
                    checkAndAddToList(truncationOrder, referredTable, tables, visitedTables);
                }
            }
        }
        if (!truncationOrder.contains(table.getName())) {
            truncationOrder.add(table.getName());
        }
    }
}
