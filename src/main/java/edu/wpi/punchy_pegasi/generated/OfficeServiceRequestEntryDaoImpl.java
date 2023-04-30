package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.OfficeServiceRequestEntry;
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
public class OfficeServiceRequestEntryDaoImpl implements IDao<java.util.UUID, OfficeServiceRequestEntry, OfficeServiceRequestEntry.Field> {

    static String[] fields = {"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID", "officeRequest"};
    private final PdbController dbController;

    public OfficeServiceRequestEntryDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<OfficeServiceRequestEntry> get(java.util.UUID key) {
        try (var rs = dbController.searchQuery(TableType.OFFICEREQUESTS, "serviceID", key)) {
            rs.next();
            OfficeServiceRequestEntry req = new OfficeServiceRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("officeRequest", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.util.UUID, OfficeServiceRequestEntry> get(OfficeServiceRequestEntry.Field column, Object value) {
        return get(new OfficeServiceRequestEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, OfficeServiceRequestEntry> get(OfficeServiceRequestEntry.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, OfficeServiceRequestEntry>();
        try (var rs = dbController.searchQuery(TableType.OFFICEREQUESTS, Arrays.stream(params).map(OfficeServiceRequestEntry.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                OfficeServiceRequestEntry req = new OfficeServiceRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("officeRequest", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class));
                if (req != null)
                    map.put(req.getServiceID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return map;
    }

    @Override
    public ObservableMap<java.util.UUID, OfficeServiceRequestEntry> getAll() {
        var map = new HashMap<java.util.UUID, OfficeServiceRequestEntry>();
        try (var rs = dbController.searchQuery(TableType.OFFICEREQUESTS)) {
            while (rs.next()) {
                OfficeServiceRequestEntry req = new OfficeServiceRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("officeRequest", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class));
                if (req != null)
                    map.put(req.getServiceID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return FXCollections.observableMap(map);
    }

    @Override
    public ObservableList<OfficeServiceRequestEntry> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(OfficeServiceRequestEntry officeServiceRequestEntry) {
        Object[] values = {officeServiceRequestEntry.getServiceID(), officeServiceRequestEntry.getLocationName(), officeServiceRequestEntry.getStaffAssignment(), officeServiceRequestEntry.getAdditionalNotes(), officeServiceRequestEntry.getStatus(), officeServiceRequestEntry.getEmployeeID(), officeServiceRequestEntry.getOfficeRequest()};
        try {
            dbController.insertQuery(TableType.OFFICEREQUESTS, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(OfficeServiceRequestEntry officeServiceRequestEntry, OfficeServiceRequestEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.OFFICEREQUESTS, "serviceID", officeServiceRequestEntry.getServiceID(), Arrays.stream(params).map(OfficeServiceRequestEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(officeServiceRequestEntry)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(OfficeServiceRequestEntry officeServiceRequestEntry) {
        try {
            dbController.deleteQuery(TableType.OFFICEREQUESTS, "serviceID", officeServiceRequestEntry.getServiceID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}