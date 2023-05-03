package edu.wpi.punchy_pegasi.backend;


import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.JsoniterSpi;
import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.schema.TableType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
public class PdbController {
    public Source source;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final PGNotificationListener listener = new PGNotificationListener() {
        @Override
        public void notification(int processId, String channelName, String payload) {
            var notification = JsonIterator.deserialize(payload, Notification.class);
            var data = JsonIterator.deserialize(notification.data, notification.tableType.getClazz());
            support.firePropertyChange(channelName, null, new DatabaseChangeEvent(notification.action, data));
            PGNotificationListener.super.notification(processId, channelName, payload);
        }

        @Override
        public void closed() {
            getConnection();
        }
    };
    private final String schema;
    private PGConnection connection;

    public PdbController(Source source, String schema) throws SQLException, ClassNotFoundException, DatabaseException {
        //add uuid support
        JsoniterSpi.registerTypeEncoder(UUID.class, (obj, stream) -> stream.writeVal(obj.toString()));
        JsoniterSpi.registerTypeDecoder(UUID.class, iter -> UUID.fromString(iter.readString()));
        JsoniterSpi.registerTypeEncoder(LocalDate.class, (obj, stream) -> stream.writeVal(obj.toString()));
        JsoniterSpi.registerTypeDecoder(LocalDate.class, iter -> LocalDate.parse(iter.readString()));
        JsoniterSpi.registerTypeEncoder(Instant.class, (obj, stream) -> stream.writeVal(obj.toString()));
        JsoniterSpi.registerTypeDecoder(Instant.class, iter -> Instant.parse(iter.readString()));
        this.source = source;
        this.schema = schema;
        Class.forName("com.impossibl.postgres.jdbc.PGDriver");
        if (!getConnection()) {
            throw new DatabaseException("Failed to connect to database");
        }
        var statement = connection.createStatement();
        statement.execute("CREATE SCHEMA IF NOT EXISTS " + this.schema + ";");
        connection.setSchema(this.schema);
        statement.close();
    }

    public PdbController(Source source) throws SQLException, ClassNotFoundException, DatabaseException {
        this(source, "teamp");
    }

    public void switchSource(Source source) throws DatabaseException, SQLException {
        this.source = source;
        if (connection != null)
            connection.close();
        if (!getConnection())
            throw new DatabaseException("Failed to connect to database");
        var statement = connection.createStatement();
        statement.execute("CREATE SCHEMA IF NOT EXISTS " + this.schema + ";");
        connection.setSchema(this.schema);
        statement.close();

    }

    private static String objectToPsqlString(Object o) {
        return objectToPsqlString(o, true);
    }

    public static String objectToPsqlString(Object o, boolean first) {
        if (o == null) return "NULL";
        if (o instanceof String || o instanceof UUID || o instanceof LocalDate || o instanceof Instant || o.getClass().isEnum()) {
            return "'" + o + "'";
        } else if (o instanceof List<?>) {
            return (first ? "ARRAY" : "") + "[" + String.join(", ", ((List<?>) o).stream().map(v -> objectToPsqlString(v, false)).toList()) + "]";
        } else if (o instanceof Object[]) {
            return (first ? "ARRAY" : "") + "[" + String.join(", ", Arrays.stream((Object[]) o).map(v -> objectToPsqlString(v, false)).toList()) + "]";
        } else {
            return o.toString();
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public synchronized boolean reconnectOnError(SQLException e) {
        if (e.getMessage().contains("BlockingReadTimeoutException") || e.getSQLState().equals("08006")) {
            return getConnection();
        }
        return false;
    }

    private boolean getConnection() {
        try {
            if (connection == null) {
                initConnection();
                return true;
            }
            if (connection.isClosed() || !connection.isValid(500)) {
                connection.close();
                initConnection();
                return true;
            }
            return false;
        } catch (SQLException e) {
            if (App.getSingleton().getLayout() == null) return false;
            App.getSingleton().getLayout().hideOverlay();
            var refresh = new PFXButton("Reload");
            var text = new Label("It seems the app has lost connection to the database.\nBy clicking reconnect, the app will exit and attempt to reconnect.\nIf the problem persists, please contact an administrator.");
            text.setWrapText(true);
            var box = new VBox(text, refresh);
            text.setTextAlignment(TextAlignment.CENTER);
            text.setTextFill(Color.BLACK);
            refresh.setOnMouseClicked(ev -> {
                try {
                    App.getSingleton().start(App.getSingleton().getPrimaryStage());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            var vC = new VBox();
            vC.setAlignment(Pos.CENTER);
            vC.setStyle("-fx-background-color: -pfx-secondary-transparent");
            var hC = new HBox();
            hC.setAlignment(Pos.CENTER);
            vC.getChildren().add(hC);
            var c = new VBox(text, refresh);
            c.setStyle("""
                    -fx-background-color: white;
                    -fx-background-radius: 12;
                    -fx-padding: 10;
                    -fx-spacing: 10;
                    -fx-alignment: CENTER;
                    """);
            hC.getChildren().add(c);
            Platform.runLater(() -> App.getSingleton().getLayout().showOverlay(vC, false));
            return false;
        }
    }

    private void initConnection() throws SQLException {
        DriverManager.setLoginTimeout(4);
        connection = DriverManager.getConnection("jdbc:pgsql://" + source.url + ":" + source.port + "/" + source.database, source.username, source.password).unwrap(PGConnection.class);
        connection.addNotificationListener(listener);
        connection.setSchema(schema);
        if (!(App.getSingleton() == null)) {
            connection.setNetworkTimeout(App.getSingleton().getExecutorService(), 4000);
        }
        var statement = connection.createStatement();
        for (var tableType : TableType.values()) {
            statement.executeUpdate("LISTEN " + tableType.name().toLowerCase() + "_update;");
        }
        statement.close();
    }

    public Connection exposeConnection() {
        return this.connection;
    }

    public void initTableByType(TableType tableType) throws DatabaseException {
        try {
            initTable(tableType);
            log.info(tableType.name() + " table initialized");
        } catch (SQLException e) {
            if (reconnectOnError(e)) {
                initTableByType(tableType);
                return;
            }
            log.error("Failed to initialize " + tableType.name() + " table", e);
            throw new DatabaseException("SQL error");
        }
    }

    private void initTable(TableType tableType) throws SQLException {
        var statement = connection.createStatement();
        statement.execute("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"); // create uuid extension
        var q = tableType.getTableSQL();
        statement.execute(q);
        statement.close();
    }

    private String getFieldValueString(String[] fields, Object[] values, String equator, String delimiter) {
        String query = "";
        for (int i = 0; i < fields.length; i++) {
            query += fields[i] + " " + equator + " " + objectToPsqlString(values[i]);
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
    public int updateQuery(TableType tableType, String keyField, Object keyValue, String[] fields, Object[] values) throws DatabaseException {
        if (fields.length != values.length) throw new DatabaseException("Fields and values must be the same length");
        try {
            @Cleanup var statement = connection.createStatement();
            var query = "UPDATE " + tableType.name().toLowerCase() + " SET ";
            query += getFieldValueString(fields, values, " = ", ", ");
            query += " WHERE " + keyField + " = " + objectToPsqlString(keyValue);
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            if (reconnectOnError(e)) return updateQuery(tableType, keyField, keyValue, fields, values);
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
            @Cleanup var statement = connection.createStatement();
            var query = "INSERT INTO " + tableType.name().toLowerCase() + " (";
            query += String.join(", ", fields);
            query += ") VALUES (";
            query += String.join(", ", Arrays.stream(values).map(PdbController::objectToPsqlString).toList());
            query += ");";
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            if (reconnectOnError(e)) return insertQuery(tableType, fields, values);
            log.error("Failed to insert row", e);
            throw new DatabaseException("SQL error");
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
            @Cleanup var statement = connection.createStatement();
            var query = "DELETE FROM " + tableType.name().toLowerCase() + " WHERE ";
            query += getFieldValueString(field, value, "=", " AND ");
            query += ";";
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            if (reconnectOnError(e)) return deleteQuery(tableType, field, value);
            log.error("Failed to delete node", e);
            throw new DatabaseException("SQL error");
        }
    }

    public String getDeleteQuery(TableType tableType, String[] field, Object[] value) throws DatabaseException {
        if (field.length != value.length) throw new DatabaseException("Fields and values must be the same length");
        var query = "DELETE FROM " + tableType.name().toLowerCase() + " WHERE ";
        query += getFieldValueString(field, value, "=", " AND ");
        query += ";";
        return query;
    }

    public int[] executeDeletes(List<String> deleteQueries) throws DatabaseException {
        try {
            var statement = connection.createStatement();
            statement.closeOnCompletion();
            for (var query : deleteQueries)
                statement.addBatch(query);
            return statement.executeBatch();
        } catch (SQLException e) {
            if (reconnectOnError(e)) return executeDeletes(deleteQueries);
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
            statement.closeOnCompletion();
            var query = "SELECT * FROM " + tableType.name().toLowerCase();
            if (fields.length > 0) {
                query += " WHERE ";
                query += getFieldValueString(fields, values, "=", " AND ");
            }
            query += ";";
            return statement.executeQuery(query);
        } catch (SQLException e) {
            if (reconnectOnError(e)) return searchQuery(tableType, fields, values);
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
        } catch (SQLException e) {
            if (reconnectOnError(e)) {
                exportTable(path, tableType);
                return;
            }
            log.error("Failed to export table:", e);
            throw new DatabaseException("Table does not exist");
        } catch (IOException e) {
            log.error("Failed to export table:", e);
            throw new DatabaseException("Failed to open selected file");
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

        } catch (SQLException e) {
            if (reconnectOnError(e)) {
                importTable(tableType, path);
                return;
            }
            log.error("Failed to import table:", e);
            if (e.getMessage().contains("violates unique constraint")) {
                throw new DatabaseException("One or more nodes in csv already exist");
            } else if (Pattern.matches("[\\S\\s]*column.+does not exist[\\S\\s]*", e.getMessage())) {
                throw new DatabaseException("CSV headers do not match table headers");
            }
            throw new DatabaseException("Table does not exist, CSV has incorrect headers");
        } catch (IOException e) {
            log.error("Failed to import table:", e);
            throw new DatabaseException("Failed to open selected file");
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
        statement.closeOnCompletion();
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

    public enum PG_ACTION {
        INSERT,
        UPDATE,
        DELETE
    }

    @AllArgsConstructor
    @Getter
    public enum Source {
        Wong("database.cs.wpi.edu", 5432, "teampdb", "teamp", "teamp130"),
        Blake("bruellcarlisle.dyndns.org", 54321, "softeng", "teamp", "teamp130");
        //Local("localhost", 5432, "postgres", "username", "password");
        private String url;
        private int port;
        private String database;
        private String username;
        private String password;
    }

    private static class Notification {
        public TableType tableType;
        public PG_ACTION action;
        public String data;
    }

    public record DatabaseChangeEvent(PG_ACTION action, Object data) {
    }

    public class DatabaseException extends Exception {
        public DatabaseException(String e) {
            super(e);
        }
    }
}
