package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.FlowerDeliveryRequestEntry;
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
public class FlowerDeliveryRequestEntryDaoImpl implements IDao<java.util.UUID, FlowerDeliveryRequestEntry, FlowerDeliveryRequestEntry.Field> {

    static String[] fields = {"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID", "flowerSize", "flowerType", "flowerAmount", "patientName"};
    private final PdbController dbController;

    public FlowerDeliveryRequestEntryDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<FlowerDeliveryRequestEntry> get(java.util.UUID key) {
        try (var rs = dbController.searchQuery(TableType.FLOWERREQUESTS, "serviceID", key)) {
            rs.next();
            FlowerDeliveryRequestEntry req = new FlowerDeliveryRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("patientName", java.lang.String.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("flowerSize", java.lang.String.class),
                    rs.getObject("flowerAmount", java.lang.String.class),
                    rs.getObject("flowerType", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.util.UUID, FlowerDeliveryRequestEntry> get(FlowerDeliveryRequestEntry.Field column, Object value) {
        return get(new FlowerDeliveryRequestEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, FlowerDeliveryRequestEntry> get(FlowerDeliveryRequestEntry.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, FlowerDeliveryRequestEntry>();
        try (var rs = dbController.searchQuery(TableType.FLOWERREQUESTS, Arrays.stream(params).map(FlowerDeliveryRequestEntry.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                FlowerDeliveryRequestEntry req = new FlowerDeliveryRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("patientName", java.lang.String.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("flowerSize", java.lang.String.class),
                    rs.getObject("flowerAmount", java.lang.String.class),
                    rs.getObject("flowerType", java.lang.String.class),
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
    public ObservableMap<java.util.UUID, FlowerDeliveryRequestEntry> getAll() {
        var map = new HashMap<java.util.UUID, FlowerDeliveryRequestEntry>();
        try (var rs = dbController.searchQuery(TableType.FLOWERREQUESTS)) {
            while (rs.next()) {
                FlowerDeliveryRequestEntry req = new FlowerDeliveryRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("patientName", java.lang.String.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("flowerSize", java.lang.String.class),
                    rs.getObject("flowerAmount", java.lang.String.class),
                    rs.getObject("flowerType", java.lang.String.class),
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
    public ObservableList<FlowerDeliveryRequestEntry> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry) {
        Object[] values = {flowerDeliveryRequestEntry.getServiceID(), flowerDeliveryRequestEntry.getLocationName(), flowerDeliveryRequestEntry.getStaffAssignment(), flowerDeliveryRequestEntry.getAdditionalNotes(), flowerDeliveryRequestEntry.getStatus(), flowerDeliveryRequestEntry.getEmployeeID(), flowerDeliveryRequestEntry.getFlowerSize(), flowerDeliveryRequestEntry.getFlowerType(), flowerDeliveryRequestEntry.getFlowerAmount(), flowerDeliveryRequestEntry.getPatientName()};
        try {
            dbController.insertQuery(TableType.FLOWERREQUESTS, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry, FlowerDeliveryRequestEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.FLOWERREQUESTS, "serviceID", flowerDeliveryRequestEntry.getServiceID(), Arrays.stream(params).map(FlowerDeliveryRequestEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(flowerDeliveryRequestEntry)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry) {
        try {
            dbController.deleteQuery(TableType.FLOWERREQUESTS, "serviceID", flowerDeliveryRequestEntry.getServiceID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}