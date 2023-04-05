package edu.wpi.punchy_pegasi.backend;


import edu.wpi.punchy_pegasi.schema.TableType;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
public class PdbController {
    private Connection connection;
    public PdbController(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            log.error("Failed to connect to db :", e);
        }
    }

    private static String objectToPsqlString(Object o) {
        return objectToPsqlString(o, true);
    }

    private static String objectToPsqlString(Object o, boolean first) {
        if (o instanceof String || o instanceof UUID || o.getClass().isEnum()) {
            return "'" + o + "'";
        } else if (o instanceof List<?>) {
            return (first ? "ARRAY" : "") + "[" + String.join(", ", ((List<?>) o).stream().map(v -> objectToPsqlString(v, false)).toList()) + "]";
        } else if (o instanceof Object[]) {
            return (first ? "ARRAY" : "") + "[" + String.join(", ", Arrays.stream((Object[]) o).map(v -> objectToPsqlString(v, false)).toList()) + "]";
        } else {
            return o.toString();
        }
    }

    public void initTableByType(TableType tableType) throws DatabaseException {
        try {
            initTable(tableType);
            System.out.println("Table initialized");
        } catch (SQLException e) {
            log.error("Failed to Initialize table", e);
            throw new DatabaseException("SQL error");
        }
    }

    private void initTable(TableType tableType) throws SQLException {
        var statement = connection.createStatement();
        statement.execute("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"); // create uuid extension
        var query = "CREATE TABLE IF NOT EXISTS " + tableType.name().toLowerCase();
        switch (tableType) {
            case NODES -> {
                var ret = statement.execute(query + "(" +
                        "nodeID bigint PRIMARY KEY, " +
                        "xcoord int, " +
                        "ycoord int, " +
                        "floor varchar, " +
                        "building varchar);");
            }
            case EDGES -> {
                statement.execute("DROP SEQUENCE IF EXISTS table_name_id_seq;");
                statement.execute("CREATE SEQUENCE table_name_id_seq;");
                var ret2 = statement.execute(query + "(" +
                        "uuid bigint DEFAULT nextval('table_name_id_seq')," +
                        "startNode varchar, " +
                        "endNode varchar);");
                statement.execute("ALTER SEQUENCE table_name_id_seq OWNED BY edges.uuid;");

            }
            case MOVES -> {
                var ret2 = statement.execute(query +
                        "(" +
                        "nodeID bigint, " +
                        "longName varchar, " +
                        "date varchar" +
                        ");");
            }
            case LOCATIONNAMES -> {
                var ret2 = statement.execute(query + "(" +
                        "longName varchar PRIMARY KEY, " +
                        "shortName varchar, " +
                        "nodeType varchar" +
                        ");");
            }
            case FOODREQUESTS -> {
                var ret2 = statement.execute(query + "(" +
                        "serviceID uuid DEFAULT uuid_generate_v4()," +
                        // if pass null, psql will generate a uuid
                        "patientName varchar(100)," +
                        "roomNumber varchar(100)," +
                        "staffAssignment varchar(100)," +
                        "additionalNotes varchar(1000)," +
                        "status varchar(50)," +
                        "foodSelection varchar(100)," +
                        "tempType varchar(50)," +
                        "additionalItems varchar(100) ARRAY," +
                        "dietaryRestrictions varchar(1000)," +
                        "PRIMARY KEY (serviceID)" +
                        ");");
            }
            case FLOWERREQUESTS -> {
                var ret2 = statement.execute(query +
                        "(" +
                        "serviceID uuid DEFAULT uuid_generate_v4()," +
                        // if pass null, psql will generate a uuid
                        "patientName varchar(100)," +
                        "roomNumber varchar(100)," +
                        "staffAssignment varchar(100)," +
                        "additionalNotes varchar(1000)," +
                        "status varchar(50)," +
                        "flowerSize varchar(100)," +
                        "flowerAmount varchar(100)," +
                        "flowerType varchar(100)," +
                        "PRIMARY KEY (serviceID)" +
                        ");");
            }
            case CONFERENCEREQUESTS -> {
                var ret2 = statement.execute(query +
                        "(" +
                        "serviceID uuid DEFAULT uuid_generate_v4()," +
                        // if pass null, psql will generate a uuid
                        "patientName varchar(100)," +
                        "roomNumber varchar(100)," +
                        "additionalNotes varchar(1000)," +
                        "staffAssignment varchar(100)," +
                        "status varchar(50)," +
                        "beginningTime varchar(100)," +
                        "endTime varchar(100)," +
                        "PRIMARY KEY (serviceID)" +
                        ");");
            }
            case FURNITUREREQUESTS -> {
                var ret2 = statement.execute(query +
                        "(" +
                        "serviceID uuid DEFAULT uuid_generate_v4()," +
                        // if pass null, psql will generate a uuid
                        "patientName varchar(100)," +
                        "roomNumber varchar(100)," +
                        "staffAssignment varchar(100)," +
                        "additionalNotes varchar(1000)," +
                        "status varchar(50)," +
                        "selectFurniture varchar(100) ARRAY," +
                        "PRIMARY KEY (serviceID)" +
                        ");");
            }
            case OFFICEREQUESTS -> {
                var ret2 = statement.execute(query +
                        "(" +
                        "serviceID uuid DEFAULT uuid_generate_v4()," +
                        // if pass null, psql will generate a uuid
                        "patientName varchar(100)," +
                        "roomNumber varchar(100)," +
                        "staffAssignment varchar(100)," +
                        "additionalNotes varchar(1000)," +
                        "status varchar(50)," +
                        "officeRequest varchar(100)," +
                        "employeeName varchar(100)," +
                        "PRIMARY KEY (serviceID)" +
                        ");");
            }


        }
    }

    private String getFieldValueString(String[] fields, Object[] values, String delimiter) {
        String query = "";
        for (int i = 0; i < fields.length; i++) {
            query += fields[i] +
                        " = " + objectToPsqlString(values[i]);
            if (i != fields.length - 1) query += delimiter;
        }
        return query;
    }

    /**
     * Updates all entries in tableType table where the entries fields[0] value matches object[0]
     *
     * @param tableType
     * @param fields
     * @param values
     * @return The number of rows updated
     * @throws DatabaseException
     */
    public int updateQuery(TableType tableType, String[] fields, Object[] values) throws DatabaseException {
        if (fields.length != values.length) throw new DatabaseException("Fields and values must be the same length");
        try {
            var statement = connection.createStatement();
            var query = "UPDATE teamp." + tableType.name().toLowerCase() + " SET ";
            getFieldValueString(fields, values, ", ");
            query += " WHERE " + fields[0] + " = " + values[0];
            var ret = statement.executeUpdate(query);
            return ret;
        } catch (SQLException e) {
            log.error("Failed to update node", e);
            throw new DatabaseException("SQL error");
        }
    }

    /**
     * Inserts a new entry into the tableType table
     *
     * @param tableType
     * @param fields
     * @param values
     * @return The number of rows inserted
     * @throws DatabaseException
     */
    public int insertQuery(TableType tableType, String[] fields, Object[] values) throws DatabaseException {
        if (fields.length != values.length) throw new DatabaseException("Fields and values must be the same length");
        try {
            var statement = connection.createStatement();
            var query = "INSERT INTO teamp." + tableType.name().toLowerCase() + " (";
            query += String.join(", ", fields);
            query += ") VALUES (";
            query += String.join(", ", Arrays.stream(values).map(PdbController::objectToPsqlString).toList());
            query += ");";
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            log.error("Failed to insert row", e);
            throw new DatabaseException("SQL er ror");
        }
    }

    /**
     * Deletes all entries in tableType table whose fields match the given values
     *
     * @param tableType
     * @param field
     * @param value
     * @return The number of rows deleted
     * @throws DatabaseException
     */
    public int deleteQuery(TableType tableType, String[] field, Object[] value) throws DatabaseException {
        if (field.length != value.length) throw new DatabaseException("Fields and values must be the same length");
        try {
            var statement = connection.createStatement();
            var query = "DELETE FROM teamp." + tableType.name().toLowerCase() + " WHERE ";
            query += getFieldValueString(field, value, " AND ");
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            log.error("Failed to delete node", e);
            throw new DatabaseException("SQL error");
        }
    }

    /**
     * Deletes all entries in tableType table whose value for "field" matches the given value
     *
     * @param tableType
     * @param field
     * @param value
     * @return The number of rows deleted
     * @throws DatabaseException
     */
    public int deleteQuery(TableType tableType, String field, Object value) throws DatabaseException {
        return deleteQuery(tableType, new String[]{field}, new Object[]{value});
    }

    /**
     * Searches for all entries in tableType table whose fields match the given values
     *
     * @param tableType
     * @param fields
     * @param values
     * @return A list of all matching entries
     * @throws DatabaseException
     */
    public ResultSet searchQuery(TableType tableType, String[] fields, Object[] values) throws DatabaseException {
        if (fields.length != values.length) throw new DatabaseException("Fields and values must be the same length");
        try {
            var statement = connection.createStatement();
            var query = "SELECT * FROM teamp." + tableType.name().toLowerCase();
            if (fields.length > 0) {
                query += " WHERE ";
                query += getFieldValueString(fields, values, " AND ");
            }
            query += ";";
            return statement.executeQuery(query);
        } catch (SQLException e) {
            log.error("Failed to search node", e);
            throw new DatabaseException("SQL error");
        }
    }

    public ResultSet searchQuery(TableType tableType) throws DatabaseException {
        return searchQuery(tableType, new String[]{}, new Object[]{});
    }

    /**
     * Searches for all entries in tableType table whose value for "field" matches the given value
     *
     * @param tableType
     * @param field
     * @param value
     * @return A list of all matching entries
     * @throws DatabaseException
     */
    public ResultSet searchQuery(TableType tableType, String field, Object value) throws DatabaseException {
        return searchQuery(tableType, new String[]{field}, new Object[]{value});
    }

    public void exportTable(String path, TableType tableType) throws DatabaseException {
        try {
            exportToCSV(path, tableType);
            log.info("Exported table successfully");
        } catch (SQLException | IOException e) {
            log.error("Failed to export table:", e);
            if (e instanceof IOException) throw new DatabaseException("Failed to open selected file");
            else if (e instanceof SQLException) throw new DatabaseException("Table does not exist");
        }
    }

    public void importTable(TableType tableType, String path) throws DatabaseException {
        try {
            initTableByType(tableType);
        } catch (DatabaseException e) {
            log.error("Failed to initialize table:", e);
        }
        try {
            insertfromCSV(path, tableType);
            log.info("Imported table successfully");

        } catch (SQLException | IOException e) {
            log.error("Failed to import table:", e);
            if (e instanceof IOException) {
                throw new DatabaseException("Failed to open selected file");
            } else if (e instanceof SQLException) {
                if (e.getMessage().contains("violates unique constraint")) {
                    throw new DatabaseException("One or more nodes in csv already exist");
                } else if (Pattern.matches("[\\S\\s]*column.+does not exist[\\S\\s]*", e.getMessage())) {
                    throw new DatabaseException("CSV headers do not match table headers");
                }
            }
            throw new DatabaseException("Table does not exist, CSV has incorrect headers");
        }
    }

    private int insertfromCSV(String path, TableType tableType) throws IOException, SQLException {
        // read the file one line at a time
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path));

        var sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableType.name().toLowerCase()).append("(").append(reader.readLine()).append(") VALUES ");

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append("(");
            Arrays.stream(line.split(",")).forEach(val -> {
                sb.append("'");
                sb.append(val);
                sb.append("',");
            });
            sb.setLength(sb.length() - 1);
            sb.append("),");
        }
        sb.setLength(sb.length() - 1);
        sb.append("ON CONFLICT DO NOTHING;");
        var statement = connection.createStatement();
        return statement.executeUpdate(sb.toString());
    }

    private void exportToCSV(String path, TableType tableType) throws SQLException, DatabaseException, IOException {
        try (var result = searchQuery(tableType); var fw = new FileWriter(path)) {
            var metaData = result.getMetaData();
            var headerCount = metaData.getColumnCount();
            for (int i = 1; i <= headerCount; i++) {
                fw.append(metaData.getColumnName(i));
                fw.append(i < headerCount ? ',' : '\n');
            }
            while (result.next()) {
                for (int i = 1; i <= headerCount; i++) {
                    fw.append(result.getString(i));
                    fw.append(i <= headerCount - 1 ? ',' : '\n');
                }
            }
        }
    }

    public class DatabaseException extends Exception {
        public DatabaseException(String e) {
            super(e);
        }
    }
}