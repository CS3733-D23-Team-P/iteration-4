package edu.wpi.punchy_pegasi.generator;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.GenericRequestEntry;
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
public class GenericRequestEntryCachedDaoImpl implements IDao<String/*idFieldType*/, GenericRequestEntry, GenericRequestEntry.Field>, PropertyChangeListener {

    static String[] fields = {/*fields*/};

    private final ObservableMap<String/*idFieldType*/, GenericRequestEntry> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<GenericRequestEntry> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public GenericRequestEntryCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<String/*idFieldType*/, GenericRequestEntry>) c -> {
            Platform.runLater(() -> {
                if (c.wasRemoved() && c.wasAdded()) {
                    IntStream.range(0, list.size())
                            .boxed().filter(i -> "list.get(i)"/*getID*/
                                    .equals("c.getValueRemoved()"/*getID*/)).findFirst().ifPresent(i -> {
                                list.remove((int) i);
                                list.add(i, c.getValueAdded());
                            });
                } else if (c.wasRemoved()) {
                    list.removeIf(o -> "o"/*getID*/
                            .equals("c.getValueRemoved()"/*getID*/));
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

    public MFXTableView<GenericRequestEntry> generateTable/*FacadeClassName*/(Consumer<GenericRequestEntry> onRowClick, GenericRequestEntry.Field[] hidden) {
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

    public MFXTableView<GenericRequestEntry> generateTable/*FacadeClassName*/(Consumer<GenericRequestEntry> onRowClick) {
        return generateTable(onRowClick, new GenericRequestEntry.Field[]{});
    }

    public void add(GenericRequestEntry genericRequestEntry) {
        if (!cache.containsKey("genericRequestEntry"/*getID*/))
            cache.put("genericRequestEntry"/*getID*/, genericRequestEntry);
    }

    public void update(GenericRequestEntry genericRequestEntry) {
        cache.put("genericRequestEntry"/*getID*/, genericRequestEntry);
    }

    public void remove(GenericRequestEntry genericRequestEntry) {
        cache.remove("genericRequestEntry"/*getID*/);
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.GENERIC)) {
            while (rs.next()) {
                GenericRequestEntry req/*fromResultSet*/ = null;
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<GenericRequestEntry> get/*FacadeClassName*/(String/*idFieldType*/ key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<String/*idFieldType*/, GenericRequestEntry> get/*FacadeClassName*/(GenericRequestEntry.Field column, Object value) {
        return get(new GenericRequestEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<String/*idFieldType*/, GenericRequestEntry> get/*FacadeClassName*/(GenericRequestEntry.Field[] params, Object[] value) {
        var map = new HashMap<String/*idFieldType*/, GenericRequestEntry>();
        if (params.length != value.length) return map;
        cache.values().forEach(v -> {
            var include = true;
            for (int i = 0; i < params.length; i++)
                include &= Objects.equals(params[i].getValue(v), value[i]);
            if (include)
                map.put("v"/*getID*/, v);
        });
        return map;
    }

    @Override
    public ObservableMap<String/*idFieldType*/, GenericRequestEntry> getAll/*FacadeClassName*/() {
        return cache;
    }

    @Override
    public ObservableList<GenericRequestEntry> getAllAsList/*FacadeClassName*/() {
        return list;
    }

    @Override
    public void save/*FacadeClassName*/(GenericRequestEntry genericRequestEntry) {
        Object[] values = {/*getFields*/};
        try {
//            add(genericRequestEntry);
            dbController.insertQuery(TableType.GENERIC, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void update/*FacadeClassName*/(GenericRequestEntry genericRequestEntry, GenericRequestEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
//            update(genericRequestEntry);
            dbController.updateQuery(TableType.GENERIC, ""/*idField*/, "genericRequestEntry"/*getID*/, Arrays.stream(params).map(GenericRequestEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(genericRequestEntry)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete/*FacadeClassName*/(GenericRequestEntry genericRequestEntry) {
        try {
//            remove(genericRequestEntry);
            dbController.deleteQuery(TableType.GENERIC, ""/*idField*/, "genericRequestEntry"/*getID*/);
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

//    public static class GenericRequestEntryForm implements IForm<GenericRequestEntry> {
//        @Getter
//        private final List<javafx.scene.Node> form;
//        private final List<TextField> inputs;
//
//        public GenericRequestEntryForm() {
//            form = new ArrayList<>();
//            inputs = new ArrayList<>();
//            for (var field : GenericRequestEntry.Field.values()) {
//                var hbox = new HBox();
//                var label = new Label(field.getColName());
//                var input = new TextField();
//                hbox.getChildren().addAll(label, input);
//                form.add(hbox);
//                inputs.add(input);
//            }
//        }
//
//        public void populateForm(GenericRequestEntry entry) {
//            for (var field : GenericRequestEntry.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                input.setText(field.getValueAsString(entry));
//            }
//        }
//
//        public GenericRequestEntry commit() {
//            var entry = new GenericRequestEntry();
//            for (var field : GenericRequestEntry.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                field.setValueFromString(entry, input.getText());
//            }
//            return entry;
//        }
//    }
}
