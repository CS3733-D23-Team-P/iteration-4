package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Edge;
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
public class EdgeCachedDaoImpl implements IDao<java.util.UUID, Edge, Edge.Field>, PropertyChangeListener {

    static String[] fields = {"uuid", "startNode", "endNode"};

    private final ObservableMap<java.util.UUID, Edge> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<Edge> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public EdgeCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.util.UUID, Edge>) c -> {
            Platform.runLater(() -> {
                if (c.wasRemoved() && c.wasAdded()) {
                    IntStream.range(0, list.size())
                            .boxed().filter(i -> list.get(i).getUuid()
                                    .equals(c.getValueRemoved().getUuid())).findFirst().ifPresent(i -> {
                                list.remove((int) i);
                                list.add(i, c.getValueAdded());
                            });
                } else if (c.wasRemoved()) {
                    list.removeIf(o -> o.getUuid()
                            .equals(c.getValueRemoved().getUuid()));
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

    public MFXTableView<Edge> generateTable(Consumer<Edge> onRowClick, Edge.Field[] hidden) {
        var table = new MFXTableView<Edge>();
        table.setItems(list);
        for (Edge.Field field : Arrays.stream(Edge.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<Edge> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<Edge> generateTable(Consumer<Edge> onRowClick) {
        return generateTable(onRowClick, new Edge.Field[]{});
    }

    public void add(Edge edge) {
        if (!cache.containsKey(edge.getUuid()))
            cache.put(edge.getUuid(), edge);
    }

    public void update(Edge edge) {
        cache.put(edge.getUuid(), edge);
    }

    public void remove(Edge edge) {
        cache.remove(edge.getUuid());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.EDGES)) {
            while (rs.next()) {
                Edge req = new Edge(
                    rs.getObject("uuid", java.util.UUID.class),
                    rs.getObject("startNode", java.lang.Long.class),
                    rs.getObject("endNode", java.lang.Long.class));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<Edge> get(java.util.UUID key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.util.UUID, Edge> get(Edge.Field column, Object value) {
        return get(new Edge.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, Edge> get(Edge.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, Edge>();
        if (params.length != value.length) return map;
        cache.values().forEach(v -> {
            var include = true;
            for (int i = 0; i < params.length; i++)
                include &= Objects.equals(params[i].getValue(v), value[i]);
            if (include)
                map.put(v.getUuid(), v);
        });
        return map;
    }

    @Override
    public ObservableMap<java.util.UUID, Edge> getAll() {
        return cache;
    }

    @Override
    public ObservableList<Edge> getAllAsList() {
        return list;
    }

    @Override
    public void save(Edge edge) {
        Object[] values = {edge.getUuid(), edge.getStartNode(), edge.getEndNode()};
        try {
            add(edge);
            dbController.insertQuery(TableType.EDGES, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void update(Edge edge, Edge.Field[] params) {
        if (params.length < 1)
            return;
        try {
            update(edge);
            dbController.updateQuery(TableType.EDGES, "uuid", edge.getUuid(), Arrays.stream(params).map(Edge.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(edge)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Edge edge) {
        try {
            remove(edge);
            dbController.deleteQuery(TableType.EDGES, "uuid", edge.getUuid());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.EDGES.name().toLowerCase() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (Edge) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

//    public static class EdgeForm implements IForm<Edge> {
//        @Getter
//        private final List<javafx.scene.Node> form;
//        private final List<TextField> inputs;
//
//        public EdgeForm() {
//            form = new ArrayList<>();
//            inputs = new ArrayList<>();
//            for (var field : Edge.Field.values()) {
//                var hbox = new HBox();
//                var label = new Label(field.getColName());
//                var input = new TextField();
//                hbox.getChildren().addAll(label, input);
//                form.add(hbox);
//                inputs.add(input);
//            }
//        }
//
//        public void populateForm(Edge entry) {
//            for (var field : Edge.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                input.setText(field.getValueAsString(entry));
//            }
//        }
//
//        public Edge commit() {
//            var entry = new Edge();
//            for (var field : Edge.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                field.setValueFromString(entry, input.getText());
//            }
//            return entry;
//        }
//    }
}