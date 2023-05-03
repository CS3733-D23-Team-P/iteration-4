package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.IDao;
import edu.wpi.punchy_pegasi.schema.LocationName;
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
public class LocationNameCachedDaoImpl implements IDao<java.lang.Long, LocationName, LocationName.Field>, PropertyChangeListener {

    static String[] fields = {"uuid", "longName", "shortName", "nodeType"};

    private final ObservableMap<java.lang.Long, LocationName> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<LocationName> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public LocationNameCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.lang.Long, LocationName>) c -> {
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

    public MFXTableView<LocationName> generateTable(Consumer<LocationName> onRowClick, LocationName.Field[] hidden) {
        var table = new MFXTableView<LocationName>();
        table.setItems(list);
        for (LocationName.Field field : Arrays.stream(LocationName.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<LocationName> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<LocationName> generateTable(Consumer<LocationName> onRowClick) {
        return generateTable(onRowClick, new LocationName.Field[]{});
    }

    public void add(LocationName locationName) {
        if (!cache.containsKey(locationName.getUuid()))
            cache.put(locationName.getUuid(), locationName);
    }

    public void update(LocationName locationName) {
        cache.put(locationName.getUuid(), locationName);
    }

    public void remove(LocationName locationName) {
        cache.remove(locationName.getUuid());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.LOCATIONNAMES)) {
            while (rs.next()) {
                LocationName req = new LocationName(
                        rs.getObject("uuid", java.lang.Long.class),
                        rs.getObject("longName", java.lang.String.class),
                        rs.getObject("shortName", java.lang.String.class),
                        edu.wpi.punchy_pegasi.schema.LocationName.NodeType.valueOf(rs.getString("nodeType")));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<LocationName> get(java.lang.Long key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.lang.Long, LocationName> get(LocationName.Field column, Object value) {
        return get(new LocationName.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, LocationName> get(LocationName.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, LocationName>();
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
    public ObservableMap<java.lang.Long, LocationName> getAll() {
        return cache;
    }

    @Override
    public ObservableList<LocationName> getAllAsList() {
        return list;
    }

    @Override
    public void save(LocationName locationName) {
        Object[] values = {locationName.getUuid(), locationName.getLongName(), locationName.getShortName(), locationName.getNodeType()};
        try {
            add(locationName);
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
            update(locationName);
            dbController.updateQuery(TableType.LOCATIONNAMES, "uuid", locationName.getUuid(), Arrays.stream(params).map(LocationName.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(locationName)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(LocationName locationName) {
        try {
            remove(locationName);
            dbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", locationName.getUuid());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.LOCATIONNAMES.name().toLowerCase() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (LocationName) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

//    public static class LocationNameForm implements IForm<LocationName> {
//        @Getter
//        private final List<javafx.scene.Node> form;
//        private final List<TextField> inputs;
//
//        public LocationNameForm() {
//            form = new ArrayList<>();
//            inputs = new ArrayList<>();
//            for (var field : LocationName.Field.values()) {
//                var hbox = new HBox();
//                var label = new Label(field.getColName());
//                var input = new TextField();
//                hbox.getChildren().addAll(label, input);
//                form.add(hbox);
//                inputs.add(input);
//            }
//        }
//
//        public void populateForm(LocationName entry) {
//            for (var field : LocationName.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                input.setText(field.getValueAsString(entry));
//            }
//        }
//
//        public LocationName commit() {
//            var entry = new LocationName();
//            for (var field : LocationName.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                field.setValueFromString(entry, input.getText());
//            }
//            return entry;
//        }
//    }
}