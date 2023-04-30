package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Alert;
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
public class AlertCachedDaoImpl implements IDao<java.util.UUID, Alert, Alert.Field>, PropertyChangeListener {

    static String[] fields = {"uuid", "employeeID", "alertTitle", "description", "dateTime", "readStatus"};

    private final ObservableMap<java.util.UUID, Alert> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<Alert> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public AlertCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.util.UUID, Alert>) c -> {
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

    public MFXTableView<Alert> generateTable(Consumer<Alert> onRowClick, Alert.Field[] hidden) {
        var table = new MFXTableView<Alert>();
        table.setItems(list);
        for (Alert.Field field : Arrays.stream(Alert.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<Alert> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<Alert> generateTable(Consumer<Alert> onRowClick) {
        return generateTable(onRowClick, new Alert.Field[]{});
    }

    public void add(Alert alert) {
        if (!cache.containsKey(alert.getUuid()))
            cache.put(alert.getUuid(), alert);
    }

    public void update(Alert alert) {
        cache.put(alert.getUuid(), alert);
    }

    public void remove(Alert alert) {
        cache.remove(alert.getUuid());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.ALERT)) {
            while (rs.next()) {
                Alert req = new Alert(
                    rs.getObject("uuid", java.util.UUID.class),
                    rs.getObject("employeeID", java.lang.Long.class),
                    rs.getObject("alertTitle", java.lang.String.class),
                    rs.getObject("description", java.lang.String.class),
                    rs.getTimestamp("dateTime").toInstant(),
                    edu.wpi.punchy_pegasi.schema.Alert.ReadStatus.valueOf(rs.getString("readStatus")));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<Alert> get(java.util.UUID key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.util.UUID, Alert> get(Alert.Field column, Object value) {
        return get(new Alert.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, Alert> get(Alert.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, Alert>();
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
    public ObservableMap<java.util.UUID, Alert> getAll() {
        return cache;
    }

    @Override
    public ObservableList<Alert> getAllAsList() {
        return list;
    }

    @Override
    public void save(Alert alert) {
        Object[] values = {alert.getUuid(), alert.getEmployeeID(), alert.getAlertTitle(), alert.getDescription(), alert.getDateTime(), alert.getReadStatus()};
        try {
            dbController.insertQuery(TableType.ALERT, fields, values);
//            add(alert);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void update(Alert alert, Alert.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.ALERT, "uuid", alert.getUuid(), Arrays.stream(params).map(Alert.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(alert)).toArray());
//            update(alert);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Alert alert) {
        try {
            dbController.deleteQuery(TableType.ALERT, "uuid", alert.getUuid());
//            remove(alert);
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.ALERT.name() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (Alert) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

    public static class AlertForm implements IForm<Alert> {
        @Getter
        private final List<javafx.scene.Node> form;
        private final List<TextField> inputs;
        public AlertForm() {
            form = new ArrayList<>();
            inputs = new ArrayList<>();
            for (var field : Alert.Field.values()) {
                var hbox = new HBox();
                var label = new Label(field.getColName());
                var input = new TextField();
                hbox.getChildren().addAll(label, input);
                form.add(hbox);
                inputs.add(input);
            }
        }

        public void populateForm(Alert entry) {
            for (var field : Alert.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                input.setText(field.getValueAsString(entry));
            }
        }

        public Alert commit() {
            var entry = new Alert();
            for (var field : Alert.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                field.setValueFromString(entry, input.getText());
            }
            return entry;
        }
    }
}