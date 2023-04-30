package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.OfficeServiceRequestEntry;
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
public class OfficeServiceRequestEntryCachedDaoImpl implements IDao<java.util.UUID, OfficeServiceRequestEntry, OfficeServiceRequestEntry.Field>, PropertyChangeListener {

    static String[] fields = {"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID", "officeRequest"};

    private final ObservableMap<java.util.UUID, OfficeServiceRequestEntry> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<OfficeServiceRequestEntry> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public OfficeServiceRequestEntryCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.util.UUID, OfficeServiceRequestEntry>) c -> {
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

    public MFXTableView<OfficeServiceRequestEntry> generateTable(Consumer<OfficeServiceRequestEntry> onRowClick, OfficeServiceRequestEntry.Field[] hidden) {
        var table = new MFXTableView<OfficeServiceRequestEntry>();
        table.setItems(list);
        for (OfficeServiceRequestEntry.Field field : Arrays.stream(OfficeServiceRequestEntry.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<OfficeServiceRequestEntry> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<OfficeServiceRequestEntry> generateTable(Consumer<OfficeServiceRequestEntry> onRowClick) {
        return generateTable(onRowClick, new OfficeServiceRequestEntry.Field[]{});
    }

    public void add(OfficeServiceRequestEntry officeServiceRequestEntry) {
        if (!cache.containsKey(officeServiceRequestEntry.getServiceID()))
            cache.put(officeServiceRequestEntry.getServiceID(), officeServiceRequestEntry);
    }

    public void update(OfficeServiceRequestEntry officeServiceRequestEntry) {
        cache.put(officeServiceRequestEntry.getServiceID(), officeServiceRequestEntry);
    }

    public void remove(OfficeServiceRequestEntry officeServiceRequestEntry) {
        cache.remove(officeServiceRequestEntry.getServiceID());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.OFFICEREQUESTS)) {
            while (rs.next()) {
                OfficeServiceRequestEntry req = new OfficeServiceRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("officeRequest", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<OfficeServiceRequestEntry> get(java.util.UUID key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.util.UUID, OfficeServiceRequestEntry> get(OfficeServiceRequestEntry.Field column, Object value) {
        return get(new OfficeServiceRequestEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, OfficeServiceRequestEntry> get(OfficeServiceRequestEntry.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, OfficeServiceRequestEntry>();
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
    public ObservableMap<java.util.UUID, OfficeServiceRequestEntry> getAll() {
        return cache;
    }

    @Override
    public ObservableList<OfficeServiceRequestEntry> getAllAsList() {
        return list;
    }

    @Override
    public void save(OfficeServiceRequestEntry officeServiceRequestEntry) {
        Object[] values = {officeServiceRequestEntry.getServiceID(), officeServiceRequestEntry.getLocationName(), officeServiceRequestEntry.getStaffAssignment(), officeServiceRequestEntry.getAdditionalNotes(), officeServiceRequestEntry.getStatus(), officeServiceRequestEntry.getEmployeeID(), officeServiceRequestEntry.getOfficeRequest()};
        try {
            dbController.insertQuery(TableType.OFFICEREQUESTS, fields, values);
//            add(officeServiceRequestEntry);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void update(OfficeServiceRequestEntry officeServiceRequestEntry, OfficeServiceRequestEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.OFFICEREQUESTS, "serviceID", officeServiceRequestEntry.getServiceID(), Arrays.stream(params).map(OfficeServiceRequestEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(officeServiceRequestEntry)).toArray());
//            update(officeServiceRequestEntry);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(OfficeServiceRequestEntry officeServiceRequestEntry) {
        try {
            dbController.deleteQuery(TableType.OFFICEREQUESTS, "serviceID", officeServiceRequestEntry.getServiceID());
//            remove(officeServiceRequestEntry);
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.OFFICEREQUESTS.name() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (OfficeServiceRequestEntry) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

    public static class OfficeServiceRequestEntryForm implements IForm<OfficeServiceRequestEntry> {
        @Getter
        private final List<javafx.scene.Node> form;
        private final List<TextField> inputs;
        public OfficeServiceRequestEntryForm() {
            form = new ArrayList<>();
            inputs = new ArrayList<>();
            for (var field : OfficeServiceRequestEntry.Field.values()) {
                var hbox = new HBox();
                var label = new Label(field.getColName());
                var input = new TextField();
                hbox.getChildren().addAll(label, input);
                form.add(hbox);
                inputs.add(input);
            }
        }

        public void populateForm(OfficeServiceRequestEntry entry) {
            for (var field : OfficeServiceRequestEntry.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                input.setText(field.getValueAsString(entry));
            }
        }

        public OfficeServiceRequestEntry commit() {
            var entry = new OfficeServiceRequestEntry();
            for (var field : OfficeServiceRequestEntry.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                field.setValueFromString(entry, input.getText());
            }
            return entry;
        }
    }
}