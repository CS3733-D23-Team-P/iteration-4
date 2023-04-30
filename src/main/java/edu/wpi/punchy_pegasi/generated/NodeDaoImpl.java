package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Node;
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
public class NodeDaoImpl implements IDao<java.lang.Long, Node, Node.Field> {

    static String[] fields = {"nodeID", "xcoord", "ycoord", "floor", "building"};
    private final PdbController dbController;

    public NodeDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<Node> get(java.lang.Long key) {
        try (var rs = dbController.searchQuery(TableType.NODES, "nodeID", key)) {
            rs.next();
            Node req = new Node(
                    rs.getObject("nodeID", java.lang.Long.class),
                    rs.getObject("xcoord", java.lang.Integer.class),
                    rs.getObject("ycoord", java.lang.Integer.class),
                    rs.getObject("floor", java.lang.String.class),
                    rs.getObject("building", java.lang.String.class));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.lang.Long, Node> get(Node.Field column, Object value) {
        return get(new Node.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, Node> get(Node.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, Node>();
        try (var rs = dbController.searchQuery(TableType.NODES, Arrays.stream(params).map(Node.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                Node req = new Node(
                    rs.getObject("nodeID", java.lang.Long.class),
                    rs.getObject("xcoord", java.lang.Integer.class),
                    rs.getObject("ycoord", java.lang.Integer.class),
                    rs.getObject("floor", java.lang.String.class),
                    rs.getObject("building", java.lang.String.class));
                if (req != null)
                    map.put(req.getNodeID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return map;
    }

    @Override
    public ObservableMap<java.lang.Long, Node> getAll() {
        var map = new HashMap<java.lang.Long, Node>();
        try (var rs = dbController.searchQuery(TableType.NODES)) {
            while (rs.next()) {
                Node req = new Node(
                    rs.getObject("nodeID", java.lang.Long.class),
                    rs.getObject("xcoord", java.lang.Integer.class),
                    rs.getObject("ycoord", java.lang.Integer.class),
                    rs.getObject("floor", java.lang.String.class),
                    rs.getObject("building", java.lang.String.class));
                if (req != null)
                    map.put(req.getNodeID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return FXCollections.observableMap(map);
    }

    @Override
    public ObservableList<Node> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(Node node) {
        Object[] values = {node.getNodeID(), node.getXcoord(), node.getYcoord(), node.getFloor(), node.getBuilding()};
        try {
            dbController.insertQuery(TableType.NODES, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(Node node, Node.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.NODES, "nodeID", node.getNodeID(), Arrays.stream(params).map(Node.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(node)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Node node) {
        try {
            dbController.deleteQuery(TableType.NODES, "nodeID", node.getNodeID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}