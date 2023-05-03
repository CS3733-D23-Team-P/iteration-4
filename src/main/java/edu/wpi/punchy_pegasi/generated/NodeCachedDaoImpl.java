package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Node;
import edu.wpi.punchy_pegasi.schema.IDao;
import edu.wpi.punchy_pegasi.schema.TableType;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
public class NodeCachedDaoImpl implements IDao<java.lang.Long, Node, Node.Field>, PropertyChangeListener {

    static String[] fields = {"nodeID", "xcoord", "ycoord", "floor", "building"};

    private final ObservableMap<java.lang.Long, Node> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<Node> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public NodeCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.lang.Long, Node>) c -> {
            Platform.runLater(() -> {
                if (c.wasRemoved() && c.wasAdded()) {
                    IntStream.range(0, list.size())
                            .boxed().filter(i -> list.get(i).getNodeID()
                                    .equals(c.getValueRemoved().getNodeID())).findFirst().ifPresent(i -> {
                                list.remove((int) i);
                                list.add(i, c.getValueAdded());
                            });
                } else if (c.wasRemoved()) {
                    list.removeIf(o -> o.getNodeID()
                            .equals(c.getValueRemoved().getNodeID()));
                } else if (c.wasAdded()) {
                    list.add(c.getValueAdded());
                }
            });
        });
        initCache();
        this.dbController.addPropertyChangeListener(this);
    }

    public void refresh(){
        list.clear();
        cache.clear();
        initCache();
    }

    public MFXTableView<Node> generateTable(Consumer<Node> onRowClick, Node.Field[] hidden) {
        var table = new MFXTableView<Node>();
        table.setItems(list);
        for (Node.Field field : Arrays.stream(Node.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<Node> col = new MFXTableColumn<>(field.getColName(), true);
            col.setPickOnBounds(false);

            col.setRowCellFactory(p -> {
                var cell = new MFXTableRowCell<>(field::getValue);
                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                    if (!(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1)) return;
                    onRowClick.accept(p);
                });
                return cell;
            });
            table.getTableColumns().add(col);
        }
        table.setTableRowFactory(r -> {
            var row = new MFXTableRow<>(table, r);
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (!(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1)) return;
                onRowClick.accept(r);
            });
            return row;
        });
        return table;
    }

    public MFXTableView<Node> generateTable(Consumer<Node> onRowClick) {
        return generateTable(onRowClick, new Node.Field[]{});
    }

    public void add(Node node) {
        if (!cache.containsKey(node.getNodeID()))
            cache.put(node.getNodeID(), node);
    }

    public void update(Node node) {
        cache.put(node.getNodeID(), node);
    }

    public void remove(Node node) {
        cache.remove(node.getNodeID());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.NODES)) {
            while (rs.next()) {
                Node req = new Node(
                    rs.getObject("nodeID", java.lang.Long.class),
                    rs.getObject("xcoord", java.lang.Integer.class),
                    rs.getObject("ycoord", java.lang.Integer.class),
                    rs.getObject("floor", java.lang.String.class),
                    rs.getObject("building", java.lang.String.class));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<Node> get(java.lang.Long key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.lang.Long, Node> get(Node.Field column, Object value) {
        return get(new Node.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, Node> get(Node.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, Node>();
        if (params.length != value.length) return map;
        cache.values().forEach(v -> {
            var include = true;
            for (int i = 0; i < params.length; i++)
                include &= Objects.equals(params[i].getValue(v), value[i]);
            if (include)
                map.put(v.getNodeID(), v);
        });
        return map;
    }

    @Override
    public ObservableMap<java.lang.Long, Node> getAll() {
        return cache;
    }

    @Override
    public ObservableList<Node> getAllAsList() {
        return list;
    }

    @Override
    public void save(Node node) {
        Object[] values = {node.getNodeID(), node.getXcoord(), node.getYcoord(), node.getFloor(), node.getBuilding()};
        try {
            add(node);
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
            update(node);
            dbController.updateQuery(TableType.NODES, "nodeID", node.getNodeID(), Arrays.stream(params).map(Node.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(node)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Node node) {
        try {
            remove(node);
            dbController.deleteQuery(TableType.NODES, "nodeID", node.getNodeID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.NODES.name().toLowerCase() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (Node) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

//    public static class NodeForm implements IForm<Node> {
//        @Getter
//        private final List<javafx.scene.Node> form;
//        private final List<TextField> inputs;
//
//        public NodeForm() {
//            form = new ArrayList<>();
//            inputs = new ArrayList<>();
//            for (var field : Node.Field.values()) {
//                var hbox = new HBox();
//                var label = new Label(field.getColName());
//                var input = new TextField();
//                hbox.getChildren().addAll(label, input);
//                form.add(hbox);
//                inputs.add(input);
//            }
//        }
//
//        public void populateForm(Node entry) {
//            for (var field : Node.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                input.setText(field.getValueAsString(entry));
//            }
//        }
//
//        public Node commit() {
//            var entry = new Node();
//            for (var field : Node.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                field.setValueFromString(entry, input.getText());
//            }
//            return entry;
//        }
//    }
}