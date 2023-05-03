package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.FlowerDeliveryRequestEntry;
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
public class FlowerDeliveryRequestEntryCachedDaoImpl implements IDao<java.util.UUID, FlowerDeliveryRequestEntry, FlowerDeliveryRequestEntry.Field>, PropertyChangeListener {

    static String[] fields = {"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID", "selectedFlowers", "patientName"};

    private final ObservableMap<java.util.UUID, FlowerDeliveryRequestEntry> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<FlowerDeliveryRequestEntry> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public FlowerDeliveryRequestEntryCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.util.UUID, FlowerDeliveryRequestEntry>) c -> {
            Platform.runLater(() -> {
                if (c.wasRemoved() && c.wasAdded()) {
                    IntStream.range(0, list.size())
                            .boxed().filter(i -> list.get(i).getServiceID()
                                    .equals(c.getValueRemoved().getServiceID())).findFirst().ifPresent(i -> {
                                list.remove((int) i);
                                list.add(i, c.getValueAdded());
                            });
                } else if (c.wasRemoved()) {
                    list.removeIf(o -> o.getServiceID()
                            .equals(c.getValueRemoved().getServiceID()));
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

    public MFXTableView<FlowerDeliveryRequestEntry> generateTable(Consumer<FlowerDeliveryRequestEntry> onRowClick, FlowerDeliveryRequestEntry.Field[] hidden) {
        var table = new MFXTableView<FlowerDeliveryRequestEntry>();
        table.setItems(list);
        for (FlowerDeliveryRequestEntry.Field field : Arrays.stream(FlowerDeliveryRequestEntry.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<FlowerDeliveryRequestEntry> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<FlowerDeliveryRequestEntry> generateTable(Consumer<FlowerDeliveryRequestEntry> onRowClick) {
        return generateTable(onRowClick, new FlowerDeliveryRequestEntry.Field[]{});
    }

    public void add(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry) {
        if (!cache.containsKey(flowerDeliveryRequestEntry.getServiceID()))
            cache.put(flowerDeliveryRequestEntry.getServiceID(), flowerDeliveryRequestEntry);
    }

    public void update(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry) {
        cache.put(flowerDeliveryRequestEntry.getServiceID(), flowerDeliveryRequestEntry);
    }

    public void remove(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry) {
        cache.remove(flowerDeliveryRequestEntry.getServiceID());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.FLOWERREQUESTS)) {
            while (rs.next()) {
                FlowerDeliveryRequestEntry req = new FlowerDeliveryRequestEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("patientName", java.lang.String.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    java.util.Arrays.asList((String[])rs.getArray("selectedFlowers").getArray()),
                    rs.getObject("employeeID", java.lang.Long.class));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<FlowerDeliveryRequestEntry> get(java.util.UUID key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.util.UUID, FlowerDeliveryRequestEntry> get(FlowerDeliveryRequestEntry.Field column, Object value) {
        return get(new FlowerDeliveryRequestEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, FlowerDeliveryRequestEntry> get(FlowerDeliveryRequestEntry.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, FlowerDeliveryRequestEntry>();
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
    public ObservableMap<java.util.UUID, FlowerDeliveryRequestEntry> getAll() {
        return cache;
    }

    @Override
    public ObservableList<FlowerDeliveryRequestEntry> getAllAsList() {
        return list;
    }

    @Override
    public void save(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry) {
        Object[] values = {flowerDeliveryRequestEntry.getServiceID(), flowerDeliveryRequestEntry.getLocationName(), flowerDeliveryRequestEntry.getStaffAssignment(), flowerDeliveryRequestEntry.getAdditionalNotes(), flowerDeliveryRequestEntry.getStatus(), flowerDeliveryRequestEntry.getEmployeeID(), flowerDeliveryRequestEntry.getSelectedFlowers(), flowerDeliveryRequestEntry.getPatientName()};
        try {
            add(flowerDeliveryRequestEntry);
            dbController.insertQuery(TableType.FLOWERREQUESTS, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void update(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry, FlowerDeliveryRequestEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
            update(flowerDeliveryRequestEntry);
            dbController.updateQuery(TableType.FLOWERREQUESTS, "serviceID", flowerDeliveryRequestEntry.getServiceID(), Arrays.stream(params).map(FlowerDeliveryRequestEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(flowerDeliveryRequestEntry)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(FlowerDeliveryRequestEntry flowerDeliveryRequestEntry) {
        try {
            remove(flowerDeliveryRequestEntry);
            dbController.deleteQuery(TableType.FLOWERREQUESTS, "serviceID", flowerDeliveryRequestEntry.getServiceID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.FLOWERREQUESTS.name().toLowerCase() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (FlowerDeliveryRequestEntry) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

//    public static class FlowerDeliveryRequestEntryForm implements IForm<FlowerDeliveryRequestEntry> {
//        @Getter
//        private final List<javafx.scene.Node> form;
//        private final List<TextField> inputs;
//
//        public FlowerDeliveryRequestEntryForm() {
//            form = new ArrayList<>();
//            inputs = new ArrayList<>();
//            for (var field : FlowerDeliveryRequestEntry.Field.values()) {
//                var hbox = new HBox();
//                var label = new Label(field.getColName());
//                var input = new TextField();
//                hbox.getChildren().addAll(label, input);
//                form.add(hbox);
//                inputs.add(input);
//            }
//        }
//
//        public void populateForm(FlowerDeliveryRequestEntry entry) {
//            for (var field : FlowerDeliveryRequestEntry.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                input.setText(field.getValueAsString(entry));
//            }
//        }
//
//        public FlowerDeliveryRequestEntry commit() {
//            var entry = new FlowerDeliveryRequestEntry();
//            for (var field : FlowerDeliveryRequestEntry.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                field.setValueFromString(entry, input.getText());
//            }
//            return entry;
//        }
//    }
}