package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.FurnitureRequestEntry;
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
public class FurnitureRequestEntryDaoImpl implements IDao<java.util.UUID, FurnitureRequestEntry, FurnitureRequestEntry.Field> {

    static String[] fields = {"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID", "selectFurniture"};
    private final PdbController dbController;

    public FurnitureRequestEntryDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<FurnitureRequestEntry> get(java.util.UUID key) {
        try (var rs = dbController.searchQuery(TableType.FURNITUREREQUESTS, "serviceID", key)) {
            rs.next();
            FurnitureRequestEntry req = new FurnitureRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    java.util.Arrays.asList((String[])rs.getArray("selectFurniture").getArray()),
                    rs.getObject("employeeID", java.lang.Long.class));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.util.UUID, FurnitureRequestEntry> get(FurnitureRequestEntry.Field column, Object value) {
        return get(new FurnitureRequestEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, FurnitureRequestEntry> get(FurnitureRequestEntry.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, FurnitureRequestEntry>();
        try (var rs = dbController.searchQuery(TableType.FURNITUREREQUESTS, Arrays.stream(params).map(FurnitureRequestEntry.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                FurnitureRequestEntry req = new FurnitureRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    java.util.Arrays.asList((String[])rs.getArray("selectFurniture").getArray()),
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
    public ObservableMap<java.util.UUID, FurnitureRequestEntry> getAll() {
        var map = new HashMap<java.util.UUID, FurnitureRequestEntry>();
        try (var rs = dbController.searchQuery(TableType.FURNITUREREQUESTS)) {
            while (rs.next()) {
                FurnitureRequestEntry req = new FurnitureRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    java.util.Arrays.asList((String[])rs.getArray("selectFurniture").getArray()),
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
    public ObservableList<FurnitureRequestEntry> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(FurnitureRequestEntry furnitureRequestEntry) {
        Object[] values = {furnitureRequestEntry.getServiceID(), furnitureRequestEntry.getLocationName(), furnitureRequestEntry.getStaffAssignment(), furnitureRequestEntry.getAdditionalNotes(), furnitureRequestEntry.getStatus(), furnitureRequestEntry.getEmployeeID(), furnitureRequestEntry.getSelectFurniture()};
        try {
            dbController.insertQuery(TableType.FURNITUREREQUESTS, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(FurnitureRequestEntry furnitureRequestEntry, FurnitureRequestEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.FURNITUREREQUESTS, "serviceID", furnitureRequestEntry.getServiceID(), Arrays.stream(params).map(FurnitureRequestEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(furnitureRequestEntry)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(FurnitureRequestEntry furnitureRequestEntry) {
        try {
            dbController.deleteQuery(TableType.FURNITUREREQUESTS, "serviceID", furnitureRequestEntry.getServiceID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}