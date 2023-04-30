package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.GenericRequestEntry;
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
public class GenericRequestEntryCachedDaoImpl implements IDao<java.util.UUID, GenericRequestEntry, GenericRequestEntry.Field>, PropertyChangeListener {

    static String[] fields = {"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID"};

    private final ObservableMap<java.util.UUID, GenericRequestEntry> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<GenericRequestEntry> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public GenericRequestEntryCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.util.UUID, GenericRequestEntry>) c -> {
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

    public MFXTableView<GenericRequestEntry> generateTable(Consumer<GenericRequestEntry> onRowClick, GenericRequestEntry.Field[] hidden) {
        var table = new MFXTableView<GenericRequestEntry>();
        table.setItems(list);
        for (GenericRequestEntry.Field field : Arrays.stream(GenericRequestEntry.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<GenericRequestEntry> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<GenericRequestEntry> generateTable(Consumer<GenericRequestEntry> onRowClick) {
        return generateTable(onRowClick, new GenericRequestEntry.Field[]{});
    }

    public void add(GenericRequestEntry genericRequestEntry) {
        if (!cache.containsKey(genericRequestEntry.getServiceID()))
            cache.put(genericRequestEntry.getServiceID(), genericRequestEntry);
    }

    public void update(GenericRequestEntry genericRequestEntry) {
        cache.put(genericRequestEntry.getServiceID(), genericRequestEntry);
    }

    public void remove(GenericRequestEntry genericRequestEntry) {
        cache.remove(genericRequestEntry.getServiceID());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.GENERIC)) {
            while (rs.next()) {
                GenericRequestEntry req = new GenericRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("employeeID", java.lang.Long.class));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<GenericRequestEntry> get(java.util.UUID key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.util.UUID, GenericRequestEntry> get(GenericRequestEntry.Field column, Object value) {
        return get(new GenericRequestEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, GenericRequestEntry> get(GenericRequestEntry.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, GenericRequestEntry>();
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
    public ObservableMap<java.util.UUID, GenericRequestEntry> getAll() {
        return cache;
    }

    @Override
    public ObservableList<GenericRequestEntry> getAllAsList() {
        return list;
    }

    @Override
    public void save(GenericRequestEntry genericRequestEntry) {
        Object[] values = {genericRequestEntry.getServiceID(), genericRequestEntry.getLocationName(), genericRequestEntry.getStaffAssignment(), genericRequestEntry.getAdditionalNotes(), genericRequestEntry.getStatus(), genericRequestEntry.getEmployeeID()};
        try {
            dbController.insertQuery(TableType.GENERIC, fields, values);
//            add(genericRequestEntry);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void update(GenericRequestEntry genericRequestEntry, GenericRequestEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.GENERIC, "serviceID", genericRequestEntry.getServiceID(), Arrays.stream(params).map(GenericRequestEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(genericRequestEntry)).toArray());
//            update(genericRequestEntry);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(GenericRequestEntry genericRequestEntry) {
        try {
            dbController.deleteQuery(TableType.GENERIC, "serviceID", genericRequestEntry.getServiceID());
//            remove(genericRequestEntry);
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.GENERIC.name() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (GenericRequestEntry) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

    public static class GenericRequestEntryForm implements IForm<GenericRequestEntry> {
        @Getter
        private final List<javafx.scene.Node> form;
        private final List<TextField> inputs;
        public GenericRequestEntryForm() {
            form = new ArrayList<>();
            inputs = new ArrayList<>();
            for (var field : GenericRequestEntry.Field.values()) {
                var hbox = new HBox();
                var label = new Label(field.getColName());
                var input = new TextField();
                hbox.getChildren().addAll(label, input);
                form.add(hbox);
                inputs.add(input);
            }
        }

        public void populateForm(GenericRequestEntry entry) {
            for (var field : GenericRequestEntry.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                input.setText(field.getValueAsString(entry));
            }
        }

        public GenericRequestEntry commit() {
            var entry = new GenericRequestEntry();
            for (var field : GenericRequestEntry.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                field.setValueFromString(entry, input.getText());
            }
            return entry;
        }
    }
}