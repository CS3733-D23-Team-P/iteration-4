package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Signage;
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
public class SignageDaoImpl implements IDao<java.lang.Long, Signage, Signage.Field> {

    static String[] fields = {"uuid", "signName", "longName", "directionType"};
    private final PdbController dbController;

    public SignageDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<Signage> get(java.lang.Long key) {
        try (var rs = dbController.searchQuery(TableType.SIGNAGE, "uuid", key)) {
            rs.next();
            Signage req = new Signage(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("signName", java.lang.String.class),
                    rs.getObject("longName", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.Signage.DirectionType.valueOf(rs.getString("directionType")));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.lang.Long, Signage> get(Signage.Field column, Object value) {
        return get(new Signage.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, Signage> get(Signage.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, Signage>();
        try (var rs = dbController.searchQuery(TableType.SIGNAGE, Arrays.stream(params).map(Signage.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                Signage req = new Signage(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("signName", java.lang.String.class),
                    rs.getObject("longName", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.Signage.DirectionType.valueOf(rs.getString("directionType")));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return map;
    }

    @Override
    public ObservableMap<java.lang.Long, Signage> getAll() {
        var map = new HashMap<java.lang.Long, Signage>();
        try (var rs = dbController.searchQuery(TableType.SIGNAGE)) {
            while (rs.next()) {
                Signage req = new Signage(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("signName", java.lang.String.class),
                    rs.getObject("longName", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.Signage.DirectionType.valueOf(rs.getString("directionType")));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return FXCollections.observableMap(map);
    }

    @Override
    public ObservableList<Signage> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(Signage signage) {
        Object[] values = {signage.getUuid(), signage.getSignName(), signage.getLongName(), signage.getDirectionType()};
        try {
            dbController.insertQuery(TableType.SIGNAGE, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(Signage signage, Signage.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.SIGNAGE, "uuid", signage.getUuid(), Arrays.stream(params).map(Signage.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(signage)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Signage signage) {
        try {
            dbController.deleteQuery(TableType.SIGNAGE, "uuid", signage.getUuid());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}