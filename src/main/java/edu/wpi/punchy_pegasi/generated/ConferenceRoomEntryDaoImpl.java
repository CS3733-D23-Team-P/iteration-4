package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.ConferenceRoomEntry;
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
public class ConferenceRoomEntryDaoImpl implements IDao<java.util.UUID, ConferenceRoomEntry, ConferenceRoomEntry.Field> {

    static String[] fields = {"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID", "beginningTime", "endTime", "date", "amountOfParticipants"};
    private final PdbController dbController;

    public ConferenceRoomEntryDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<ConferenceRoomEntry> get(java.util.UUID key) {
        try (var rs = dbController.searchQuery(TableType.CONFERENCEREQUESTS, "serviceID", key)) {
            rs.next();
            ConferenceRoomEntry req = new ConferenceRoomEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("beginningTime", java.lang.String.class),
                    rs.getObject("endTime", java.lang.String.class),
                    rs.getObject("date", java.time.LocalDate.class),
                    rs.getObject("amountOfParticipants", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.util.UUID, ConferenceRoomEntry> get(ConferenceRoomEntry.Field column, Object value) {
        return get(new ConferenceRoomEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, ConferenceRoomEntry> get(ConferenceRoomEntry.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, ConferenceRoomEntry>();
        try (var rs = dbController.searchQuery(TableType.CONFERENCEREQUESTS, Arrays.stream(params).map(ConferenceRoomEntry.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                ConferenceRoomEntry req = new ConferenceRoomEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("beginningTime", java.lang.String.class),
                    rs.getObject("endTime", java.lang.String.class),
                    rs.getObject("date", java.time.LocalDate.class),
                    rs.getObject("amountOfParticipants", java.lang.String.class),
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
    public ObservableMap<java.util.UUID, ConferenceRoomEntry> getAll() {
        var map = new HashMap<java.util.UUID, ConferenceRoomEntry>();
        try (var rs = dbController.searchQuery(TableType.CONFERENCEREQUESTS)) {
            while (rs.next()) {
                ConferenceRoomEntry req = new ConferenceRoomEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("beginningTime", java.lang.String.class),
                    rs.getObject("endTime", java.lang.String.class),
                    rs.getObject("date", java.time.LocalDate.class),
                    rs.getObject("amountOfParticipants", java.lang.String.class),
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
    public ObservableList<ConferenceRoomEntry> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(ConferenceRoomEntry conferenceRoomEntry) {
        Object[] values = {conferenceRoomEntry.getServiceID(), conferenceRoomEntry.getLocationName(), conferenceRoomEntry.getStaffAssignment(), conferenceRoomEntry.getAdditionalNotes(), conferenceRoomEntry.getStatus(), conferenceRoomEntry.getEmployeeID(), conferenceRoomEntry.getBeginningTime(), conferenceRoomEntry.getEndTime(), conferenceRoomEntry.getDate(), conferenceRoomEntry.getAmountOfParticipants()};
        try {
            dbController.insertQuery(TableType.CONFERENCEREQUESTS, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(ConferenceRoomEntry conferenceRoomEntry, ConferenceRoomEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.CONFERENCEREQUESTS, "serviceID", conferenceRoomEntry.getServiceID(), Arrays.stream(params).map(ConferenceRoomEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(conferenceRoomEntry)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(ConferenceRoomEntry conferenceRoomEntry) {
        try {
            dbController.deleteQuery(TableType.CONFERENCEREQUESTS, "serviceID", conferenceRoomEntry.getServiceID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}