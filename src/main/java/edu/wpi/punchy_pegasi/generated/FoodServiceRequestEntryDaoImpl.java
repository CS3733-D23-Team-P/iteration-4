package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.FoodServiceRequestEntry;
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
public class FoodServiceRequestEntryDaoImpl implements IDao<java.util.UUID, FoodServiceRequestEntry, FoodServiceRequestEntry.Field> {

    static String[] fields = {"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID", "foodSelection", "tempType", "additionalItems", "dietaryRestrictions", "patientName", "beverage"};
    private final PdbController dbController;

    public FoodServiceRequestEntryDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<FoodServiceRequestEntry> get(java.util.UUID key) {
        try (var rs = dbController.searchQuery(TableType.FOODREQUESTS, "serviceID", key)) {
            rs.next();
            FoodServiceRequestEntry req = new FoodServiceRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("foodSelection", java.lang.String.class),
                    rs.getObject("tempType", java.lang.String.class),
                    java.util.Arrays.asList((String[])rs.getArray("additionalItems").getArray()),
                    rs.getObject("beverage", java.lang.String.class),
                    rs.getObject("dietaryRestrictions", java.lang.String.class),
                    rs.getObject("patientName", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.util.UUID, FoodServiceRequestEntry> get(FoodServiceRequestEntry.Field column, Object value) {
        return get(new FoodServiceRequestEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, FoodServiceRequestEntry> get(FoodServiceRequestEntry.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, FoodServiceRequestEntry>();
        try (var rs = dbController.searchQuery(TableType.FOODREQUESTS, Arrays.stream(params).map(FoodServiceRequestEntry.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                FoodServiceRequestEntry req = new FoodServiceRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("foodSelection", java.lang.String.class),
                    rs.getObject("tempType", java.lang.String.class),
                    java.util.Arrays.asList((String[])rs.getArray("additionalItems").getArray()),
                    rs.getObject("beverage", java.lang.String.class),
                    rs.getObject("dietaryRestrictions", java.lang.String.class),
                    rs.getObject("patientName", java.lang.String.class),
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
    public ObservableMap<java.util.UUID, FoodServiceRequestEntry> getAll() {
        var map = new HashMap<java.util.UUID, FoodServiceRequestEntry>();
        try (var rs = dbController.searchQuery(TableType.FOODREQUESTS)) {
            while (rs.next()) {
                FoodServiceRequestEntry req = new FoodServiceRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("foodSelection", java.lang.String.class),
                    rs.getObject("tempType", java.lang.String.class),
                    java.util.Arrays.asList((String[])rs.getArray("additionalItems").getArray()),
                    rs.getObject("beverage", java.lang.String.class),
                    rs.getObject("dietaryRestrictions", java.lang.String.class),
                    rs.getObject("patientName", java.lang.String.class),
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
    public ObservableList<FoodServiceRequestEntry> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(FoodServiceRequestEntry foodServiceRequestEntry) {
        Object[] values = {foodServiceRequestEntry.getServiceID(), foodServiceRequestEntry.getLocationName(), foodServiceRequestEntry.getStaffAssignment(), foodServiceRequestEntry.getAdditionalNotes(), foodServiceRequestEntry.getStatus(), foodServiceRequestEntry.getEmployeeID(), foodServiceRequestEntry.getFoodSelection(), foodServiceRequestEntry.getTempType(), foodServiceRequestEntry.getAdditionalItems(), foodServiceRequestEntry.getDietaryRestrictions(), foodServiceRequestEntry.getPatientName(), foodServiceRequestEntry.getBeverage()};
        try {
            dbController.insertQuery(TableType.FOODREQUESTS, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(FoodServiceRequestEntry foodServiceRequestEntry, FoodServiceRequestEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.FOODREQUESTS, "serviceID", foodServiceRequestEntry.getServiceID(), Arrays.stream(params).map(FoodServiceRequestEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(foodServiceRequestEntry)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(FoodServiceRequestEntry foodServiceRequestEntry) {
        try {
            dbController.deleteQuery(TableType.FOODREQUESTS, "serviceID", foodServiceRequestEntry.getServiceID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}