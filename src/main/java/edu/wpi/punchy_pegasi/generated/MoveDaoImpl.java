package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Move;
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
public class MoveDaoImpl implements IDao<java.lang.Long, Move, Move.Field> {

    static String[] fields = {"uuid", "nodeID", "locationID", "date"};
    private final PdbController dbController;

    public MoveDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<Move> get(java.lang.Long key) {
        try (var rs = dbController.searchQuery(TableType.MOVES, "uuid", key)) {
            rs.next();
            Move req = new Move(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("nodeID", java.lang.Long.class),
                    rs.getObject("locationID", java.lang.Long.class),
                    rs.getObject("date", java.time.LocalDate.class));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.lang.Long, Move> get(Move.Field column, Object value) {
        return get(new Move.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, Move> get(Move.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, Move>();
        try (var rs = dbController.searchQuery(TableType.MOVES, Arrays.stream(params).map(Move.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                Move req = new Move(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("nodeID", java.lang.Long.class),
                    rs.getObject("locationID", java.lang.Long.class),
                    rs.getObject("date", java.time.LocalDate.class));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return map;
    }

    @Override
    public ObservableMap<java.lang.Long, Move> getAll() {
        var map = new HashMap<java.lang.Long, Move>();
        try (var rs = dbController.searchQuery(TableType.MOVES)) {
            while (rs.next()) {
                Move req = new Move(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("nodeID", java.lang.Long.class),
                    rs.getObject("locationID", java.lang.Long.class),
                    rs.getObject("date", java.time.LocalDate.class));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return FXCollections.observableMap(map);
    }

    @Override
    public ObservableList<Move> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(Move move) {
        Object[] values = {move.getUuid(), move.getNodeID(), move.getLocationID(), move.getDate()};
        try {
            dbController.insertQuery(TableType.MOVES, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(Move move, Move.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.MOVES, "uuid", move.getUuid(), Arrays.stream(params).map(Move.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(move)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Move move) {
        try {
            dbController.deleteQuery(TableType.MOVES, "uuid", move.getUuid());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}