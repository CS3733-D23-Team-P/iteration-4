package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Alert;
import edu.wpi.punchy_pegasi.schema.IDao;
import edu.wpi.punchy_pegasi.schema.TableType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class AlertDaoImpl implements IDao<java.util.UUID, Alert, Alert.Field> {

    static String[] fields = {"uuid", "employeeID", "alertTitle", "description", "dateTime", "readStatus"};
    private final PdbController dbController;

    public AlertDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<Alert> get(java.util.UUID key) {
        try (var rs = dbController.searchQuery(TableType.ALERT, "uuid", key)) {
            rs.next();
            Alert req = new Alert(
                    rs.getObject("uuid", java.util.UUID.class),
                    rs.getObject("employeeID", java.lang.Long.class),
                    rs.getObject("alertTitle", java.lang.String.class),
                    rs.getObject("description", java.lang.String.class),
                    rs.getTimestamp("dateTime").toInstant(),
                    edu.wpi.punchy_pegasi.schema.Alert.ReadStatus.valueOf(rs.getString("readStatus")));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.util.UUID, Alert> get(Alert.Field column, Object value) {
        return get(new Alert.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, Alert> get(Alert.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, Alert>();
        try (var rs = dbController.searchQuery(TableType.ALERT, Arrays.stream(params).map(Alert.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                Alert req = new Alert(
                    rs.getObject("uuid", java.util.UUID.class),
                    rs.getObject("employeeID", java.lang.Long.class),
                    rs.getObject("alertTitle", java.lang.String.class),
                    rs.getObject("description", java.lang.String.class),
                    rs.getTimestamp("dateTime").toInstant(),
                    edu.wpi.punchy_pegasi.schema.Alert.ReadStatus.valueOf(rs.getString("readStatus")));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return map;
    }

    @Override
    public ObservableMap<java.util.UUID, Alert> getAll() {
        var map = new HashMap<java.util.UUID, Alert>();
        try (var rs = dbController.searchQuery(TableType.ALERT)) {
            while (rs.next()) {
                Alert req = new Alert(
                    rs.getObject("uuid", java.util.UUID.class),
                    rs.getObject("employeeID", java.lang.Long.class),
                    rs.getObject("alertTitle", java.lang.String.class),
                    rs.getObject("description", java.lang.String.class),
                    rs.getTimestamp("dateTime").toInstant(),
                    edu.wpi.punchy_pegasi.schema.Alert.ReadStatus.valueOf(rs.getString("readStatus")));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return FXCollections.observableMap(map);
    }

    @Override
    public ObservableList<Alert> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(Alert alert) {
        Object[] values = {alert.getUuid(), alert.getEmployeeID(), alert.getAlertTitle(), alert.getDescription(), alert.getDateTime(), alert.getReadStatus()};
        try {
            dbController.insertQuery(TableType.ALERT, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(Alert alert, Alert.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.ALERT, "uuid", alert.getUuid(), Arrays.stream(params).map(Alert.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(alert)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Alert alert) {
        try {
            dbController.deleteQuery(TableType.ALERT, "uuid", alert.getUuid());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}