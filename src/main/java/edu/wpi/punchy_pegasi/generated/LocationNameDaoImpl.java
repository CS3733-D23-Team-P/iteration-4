package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.LocationName;
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
public class LocationNameDaoImpl implements IDao<java.lang.Long, LocationName, LocationName.Field> {

    static String[] fields = {"uuid", "longName", "shortName", "nodeType"};
    private final PdbController dbController;

    public LocationNameDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<LocationName> get(java.lang.Long key) {
        try (var rs = dbController.searchQuery(TableType.LOCATIONNAMES, "uuid", key)) {
            rs.next();
            LocationName req = new LocationName(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("longName", java.lang.String.class),
                    rs.getObject("shortName", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.LocationName.NodeType.valueOf(rs.getString("nodeType")));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.lang.Long, LocationName> get(LocationName.Field column, Object value) {
        return get(new LocationName.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, LocationName> get(LocationName.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, LocationName>();
        try (var rs = dbController.searchQuery(TableType.LOCATIONNAMES, Arrays.stream(params).map(LocationName.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                LocationName req = new LocationName(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("longName", java.lang.String.class),
                    rs.getObject("shortName", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.LocationName.NodeType.valueOf(rs.getString("nodeType")));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return map;
    }

    @Override
    public ObservableMap<java.lang.Long, LocationName> getAll() {
        var map = new HashMap<java.lang.Long, LocationName>();
        try (var rs = dbController.searchQuery(TableType.LOCATIONNAMES)) {
            while (rs.next()) {
                LocationName req = new LocationName(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("longName", java.lang.String.class),
                    rs.getObject("shortName", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.LocationName.NodeType.valueOf(rs.getString("nodeType")));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return FXCollections.observableMap(map);
    }

    @Override
    public ObservableList<LocationName> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(LocationName locationName) {
        Object[] values = {locationName.getUuid(), locationName.getLongName(), locationName.getShortName(), locationName.getNodeType()};
        try {
            dbController.insertQuery(TableType.LOCATIONNAMES, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(LocationName locationName, LocationName.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.LOCATIONNAMES, "uuid", locationName.getUuid(), Arrays.stream(params).map(LocationName.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(locationName)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(LocationName locationName) {
        try {
            dbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", locationName.getUuid());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}