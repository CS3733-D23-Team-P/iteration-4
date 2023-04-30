package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Signage;
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
public class SignageCachedDaoImpl implements IDao<java.lang.Long, Signage, Signage.Field>, PropertyChangeListener {

    static String[] fields = {"uuid", "signName", "longName", "directionType"};

    private final ObservableMap<java.lang.Long, Signage> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<Signage> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public SignageCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.lang.Long, Signage>) c -> {
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

    public MFXTableView<Signage> generateTable(Consumer<Signage> onRowClick, Signage.Field[] hidden) {
        var table = new MFXTableView<Signage>();
        table.setItems(list);
        for (Signage.Field field : Arrays.stream(Signage.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<Signage> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<Signage> generateTable(Consumer<Signage> onRowClick) {
        return generateTable(onRowClick, new Signage.Field[]{});
    }

    public void add(Signage signage) {
        if (!cache.containsKey(signage.getUuid()))
            cache.put(signage.getUuid(), signage);
    }

    public void update(Signage signage) {
        cache.put(signage.getUuid(), signage);
    }

    public void remove(Signage signage) {
        cache.remove(signage.getUuid());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.SIGNAGE)) {
            while (rs.next()) {
                Signage req = new Signage(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("signName", java.lang.String.class),
                    rs.getObject("longName", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.Signage.DirectionType.valueOf(rs.getString("directionType")));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<Signage> get(java.lang.Long key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.lang.Long, Signage> get(Signage.Field column, Object value) {
        return get(new Signage.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, Signage> get(Signage.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, Signage>();
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
    public ObservableMap<java.lang.Long, Signage> getAll() {
        return cache;
    }

    @Override
    public ObservableList<Signage> getAllAsList() {
        return list;
    }

    @Override
    public void save(Signage signage) {
        Object[] values = {signage.getUuid(), signage.getSignName(), signage.getLongName(), signage.getDirectionType()};
        try {
            dbController.insertQuery(TableType.SIGNAGE, fields, values);
//            add(signage);
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
//            update(signage);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Signage signage) {
        try {
            dbController.deleteQuery(TableType.SIGNAGE, "uuid", signage.getUuid());
//            remove(signage);
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.SIGNAGE.name() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (Signage) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

    public static class SignageForm implements IForm<Signage> {
        @Getter
        private final List<javafx.scene.Node> form;
        private final List<TextField> inputs;
        public SignageForm() {
            form = new ArrayList<>();
            inputs = new ArrayList<>();
            for (var field : Signage.Field.values()) {
                var hbox = new HBox();
                var label = new Label(field.getColName());
                var input = new TextField();
                hbox.getChildren().addAll(label, input);
                form.add(hbox);
                inputs.add(input);
            }
        }

        public void populateForm(Signage entry) {
            for (var field : Signage.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                input.setText(field.getValueAsString(entry));
            }
        }

        public Signage commit() {
            var entry = new Signage();
            for (var field : Signage.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                field.setValueFromString(entry, input.getText());
            }
            return entry;
        }
    }
}