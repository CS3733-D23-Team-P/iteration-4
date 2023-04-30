package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.FurnitureRequestEntry;
import edu.wpi.punchy_pegasi.schema.IDao;
import edu.wpi.punchy_pegasi.schema.IForm;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class FurnitureRequestEntryCachedDaoImpl implements IDao<java.util.UUID, FurnitureRequestEntry, FurnitureRequestEntry.Field>, PropertyChangeListener {

    static String[] fields = {"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID", "selectFurniture"};

    private final ObservableMap<java.util.UUID, FurnitureRequestEntry> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<FurnitureRequestEntry> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public FurnitureRequestEntryCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.util.UUID, FurnitureRequestEntry>) c -> {
            Platform.runLater(() -> {
                if (c.wasRemoved() && c.wasAdded()) {
                    var index = list.indexOf(c.getValueRemoved());
                    if (index != -1) {
                        list.remove(index);
                        list.add(index, c.getValueAdded());
                    }
                }
                if (c.wasRemoved()) {
                    list.remove(c.getValueRemoved());
                }
                if (c.wasAdded()) {
                    list.add(c.getValueAdded());
                }
            });
        });
        initCache();
        this.dbController.addPropertyChangeListener(this);
    }

    public MFXTableView<FurnitureRequestEntry> generateTable(Consumer<FurnitureRequestEntry> onRowClick, FurnitureRequestEntry.Field[] hidden) {
        var table = new MFXTableView<FurnitureRequestEntry>();
        table.setItems(list);
        for (FurnitureRequestEntry.Field field : Arrays.stream(FurnitureRequestEntry.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<FurnitureRequestEntry> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<FurnitureRequestEntry> generateTable(Consumer<FurnitureRequestEntry> onRowClick) {
        return generateTable(onRowClick, new FurnitureRequestEntry.Field[]{});
    }

    public void add(FurnitureRequestEntry furnitureRequestEntry) {
        if (!cache.containsKey(furnitureRequestEntry.getServiceID()))
            cache.put(furnitureRequestEntry.getServiceID(), furnitureRequestEntry);
    }

    public void update(FurnitureRequestEntry furnitureRequestEntry) {
        cache.put(furnitureRequestEntry.getServiceID(), furnitureRequestEntry);
    }

    public void remove(FurnitureRequestEntry furnitureRequestEntry) {
        cache.remove(furnitureRequestEntry.getServiceID());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.FURNITUREREQUESTS)) {
            while (rs.next()) {
                FurnitureRequestEntry req = new FurnitureRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    java.util.Arrays.asList((String[])rs.getArray("selectFurniture").getArray()),
                    rs.getObject("employeeID", java.lang.Long.class));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<FurnitureRequestEntry> get(java.util.UUID key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.util.UUID, FurnitureRequestEntry> get(FurnitureRequestEntry.Field column, Object value) {
        return get(new FurnitureRequestEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, FurnitureRequestEntry> get(FurnitureRequestEntry.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, FurnitureRequestEntry>();
        if (params.length != value.length) return map;
        cache.values().forEach(v -> {
            var include = true;
            for (int i = 0; i < params.length; i++)
                include &= Objects.equals(params[i].getValue(v), value[i]);
            if (include)
                map.put(v.getServiceID(), v);
        });
        return map;
    }

    @Override
    public ObservableMap<java.util.UUID, FurnitureRequestEntry> getAll() {
        return cache;
    }

    @Override
    public ObservableList<FurnitureRequestEntry> getAllAsList() {
        return list;
    }

    @Override
    public void save(FurnitureRequestEntry furnitureRequestEntry) {
        Object[] values = {furnitureRequestEntry.getServiceID(), furnitureRequestEntry.getLocationName(), furnitureRequestEntry.getStaffAssignment(), furnitureRequestEntry.getAdditionalNotes(), furnitureRequestEntry.getStatus(), furnitureRequestEntry.getEmployeeID(), furnitureRequestEntry.getSelectFurniture()};
        try {
            dbController.insertQuery(TableType.FURNITUREREQUESTS, fields, values);
//            add(furnitureRequestEntry);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void update(FurnitureRequestEntry furnitureRequestEntry, FurnitureRequestEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.FURNITUREREQUESTS, "serviceID", furnitureRequestEntry.getServiceID(), Arrays.stream(params).map(FurnitureRequestEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(furnitureRequestEntry)).toArray());
//            update(furnitureRequestEntry);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(FurnitureRequestEntry furnitureRequestEntry) {
        try {
            dbController.deleteQuery(TableType.FURNITUREREQUESTS, "serviceID", furnitureRequestEntry.getServiceID());
//            remove(furnitureRequestEntry);
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.FURNITUREREQUESTS.name() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (FurnitureRequestEntry) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

    public static class FurnitureRequestEntryForm implements IForm<FurnitureRequestEntry> {
        @Getter
        private final List<javafx.scene.Node> form;
        private final List<TextField> inputs;
        public FurnitureRequestEntryForm() {
            form = new ArrayList<>();
            inputs = new ArrayList<>();
            for (var field : FurnitureRequestEntry.Field.values()) {
                var hbox = new HBox();
                var label = new Label(field.getColName());
                var input = new TextField();
                hbox.getChildren().addAll(label, input);
                form.add(hbox);
                inputs.add(input);
            }
        }

        public void populateForm(FurnitureRequestEntry entry) {
            for (var field : FurnitureRequestEntry.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                input.setText(field.getValueAsString(entry));
            }
        }

        public FurnitureRequestEntry commit() {
            var entry = new FurnitureRequestEntry();
            for (var field : FurnitureRequestEntry.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                field.setValueFromString(entry, input.getText());
            }
            return entry;
        }
    }
}