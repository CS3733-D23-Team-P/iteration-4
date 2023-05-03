package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.ConferenceRoomEntry;
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
public class ConferenceRoomEntryCachedDaoImpl implements IDao<java.util.UUID, ConferenceRoomEntry, ConferenceRoomEntry.Field>, PropertyChangeListener {

    static String[] fields = {"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID", "beginningTime", "endTime", "date", "amountOfParticipants"};

    private final ObservableMap<java.util.UUID, ConferenceRoomEntry> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<ConferenceRoomEntry> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public ConferenceRoomEntryCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.util.UUID, ConferenceRoomEntry>) c -> {
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

    public MFXTableView<ConferenceRoomEntry> generateTable(Consumer<ConferenceRoomEntry> onRowClick, ConferenceRoomEntry.Field[] hidden) {
        var table = new MFXTableView<ConferenceRoomEntry>();
        table.setItems(list);
        for (ConferenceRoomEntry.Field field : Arrays.stream(ConferenceRoomEntry.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<ConferenceRoomEntry> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<ConferenceRoomEntry> generateTable(Consumer<ConferenceRoomEntry> onRowClick) {
        return generateTable(onRowClick, new ConferenceRoomEntry.Field[]{});
    }

    public void add(ConferenceRoomEntry conferenceRoomEntry) {
        if (!cache.containsKey(conferenceRoomEntry.getServiceID()))
            cache.put(conferenceRoomEntry.getServiceID(), conferenceRoomEntry);
    }

    public void update(ConferenceRoomEntry conferenceRoomEntry) {
        cache.put(conferenceRoomEntry.getServiceID(), conferenceRoomEntry);
    }

    public void remove(ConferenceRoomEntry conferenceRoomEntry) {
        cache.remove(conferenceRoomEntry.getServiceID());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.CONFERENCEREQUESTS)) {
            while (rs.next()) {
                ConferenceRoomEntry req = new ConferenceRoomEntry(
                    rs.getObject("serviceID", java.util.UUID.class),
                    rs.getObject("locationName", java.lang.Long.class),
                    rs.getObject("staffAssignment", java.lang.Long.class),
                    rs.getObject("additionalNotes", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf(rs.getString("status")),
                    rs.getObject("beginningTime", java.lang.String.class),
                    rs.getObject("endTime", java.lang.String.class),
                    rs.getObject("date", java.time.LocalDate.class),
                    rs.getObject("amountOfParticipants", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<ConferenceRoomEntry> get(java.util.UUID key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.util.UUID, ConferenceRoomEntry> get(ConferenceRoomEntry.Field column, Object value) {
        return get(new ConferenceRoomEntry.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.util.UUID, ConferenceRoomEntry> get(ConferenceRoomEntry.Field[] params, Object[] value) {
        var map = new HashMap<java.util.UUID, ConferenceRoomEntry>();
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
    public ObservableMap<java.util.UUID, ConferenceRoomEntry> getAll() {
        return cache;
    }

    @Override
    public ObservableList<ConferenceRoomEntry> getAllAsList() {
        return list;
    }

    @Override
    public void save(ConferenceRoomEntry conferenceRoomEntry) {
        Object[] values = {conferenceRoomEntry.getServiceID(), conferenceRoomEntry.getLocationName(), conferenceRoomEntry.getStaffAssignment(), conferenceRoomEntry.getAdditionalNotes(), conferenceRoomEntry.getStatus(), conferenceRoomEntry.getEmployeeID(), conferenceRoomEntry.getBeginningTime(), conferenceRoomEntry.getEndTime(), conferenceRoomEntry.getDate(), conferenceRoomEntry.getAmountOfParticipants()};
        try {
            add(conferenceRoomEntry);
            dbController.insertQuery(TableType.CONFERENCEREQUESTS, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void update(ConferenceRoomEntry conferenceRoomEntry, ConferenceRoomEntry.Field[] params) {
        if (params.length < 1)
            return;
        try {
            update(conferenceRoomEntry);
            dbController.updateQuery(TableType.CONFERENCEREQUESTS, "serviceID", conferenceRoomEntry.getServiceID(), Arrays.stream(params).map(ConferenceRoomEntry.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(conferenceRoomEntry)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(ConferenceRoomEntry conferenceRoomEntry) {
        try {
            remove(conferenceRoomEntry);
            dbController.deleteQuery(TableType.CONFERENCEREQUESTS, "serviceID", conferenceRoomEntry.getServiceID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.CONFERENCEREQUESTS.name().toLowerCase() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (ConferenceRoomEntry) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

//    public static class ConferenceRoomEntryForm implements IForm<ConferenceRoomEntry> {
//        @Getter
//        private final List<javafx.scene.Node> form;
//        private final List<TextField> inputs;
//
//        public ConferenceRoomEntryForm() {
//            form = new ArrayList<>();
//            inputs = new ArrayList<>();
//            for (var field : ConferenceRoomEntry.Field.values()) {
//                var hbox = new HBox();
//                var label = new Label(field.getColName());
//                var input = new TextField();
//                hbox.getChildren().addAll(label, input);
//                form.add(hbox);
//                inputs.add(input);
//            }
//        }
//
//        public void populateForm(ConferenceRoomEntry entry) {
//            for (var field : ConferenceRoomEntry.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                input.setText(field.getValueAsString(entry));
//            }
//        }
//
//        public ConferenceRoomEntry commit() {
//            var entry = new ConferenceRoomEntry();
//            for (var field : ConferenceRoomEntry.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                field.setValueFromString(entry, input.getText());
//            }
//            return entry;
//        }
//    }
}