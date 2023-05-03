package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Employee;
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
public class EmployeeCachedDaoImpl implements IDao<java.lang.Long, Employee, Employee.Field>, PropertyChangeListener {

    static String[] fields = {"employeeID", "firstName", "lastName"};

    private final ObservableMap<java.lang.Long, Employee> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<Employee> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public EmployeeCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.lang.Long, Employee>) c -> {
            Platform.runLater(() -> {
                if (c.wasRemoved() && c.wasAdded()) {
                    IntStream.range(0, list.size())
                            .boxed().filter(i -> list.get(i).getEmployeeID()
                                    .equals(c.getValueRemoved().getEmployeeID())).findFirst().ifPresent(i -> {
                                list.remove((int) i);
                                list.add(i, c.getValueAdded());
                            });
                } else if (c.wasRemoved()) {
                    list.removeIf(o -> o.getEmployeeID()
                            .equals(c.getValueRemoved().getEmployeeID()));
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

    public MFXTableView<Employee> generateTable(Consumer<Employee> onRowClick, Employee.Field[] hidden) {
        var table = new MFXTableView<Employee>();
        table.setItems(list);
        for (Employee.Field field : Arrays.stream(Employee.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<Employee> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<Employee> generateTable(Consumer<Employee> onRowClick) {
        return generateTable(onRowClick, new Employee.Field[]{});
    }

    public void add(Employee employee) {
        if (!cache.containsKey(employee.getEmployeeID()))
            cache.put(employee.getEmployeeID(), employee);
    }

    public void update(Employee employee) {
        cache.put(employee.getEmployeeID(), employee);
    }

    public void remove(Employee employee) {
        cache.remove(employee.getEmployeeID());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.EMPLOYEES)) {
            while (rs.next()) {
                Employee req = new Employee(
                        rs.getObject("employeeID", java.lang.Long.class),
                        rs.getObject("firstName", java.lang.String.class),
                        rs.getObject("lastName", java.lang.String.class));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<Employee> get(java.lang.Long key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.lang.Long, Employee> get(Employee.Field column, Object value) {
        return get(new Employee.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, Employee> get(Employee.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, Employee>();
        if (params.length != value.length) return map;
        cache.values().forEach(v -> {
            var include = true;
            for (int i = 0; i < params.length; i++)
                include &= Objects.equals(params[i].getValue(v), value[i]);
            if (include)
                map.put(v.getEmployeeID(), v);
        });
        return map;
    }

    @Override
    public ObservableMap<java.lang.Long, Employee> getAll() {
        return cache;
    }

    @Override
    public ObservableList<Employee> getAllAsList() {
        return list;
    }

    @Override
    public void save(Employee employee) {
        Object[] values = {employee.getEmployeeID(), employee.getFirstName(), employee.getLastName()};
        try {
            add(employee);
            dbController.insertQuery(TableType.EMPLOYEES, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void update(Employee employee, Employee.Field[] params) {
        if (params.length < 1)
            return;
        try {
            update(employee);
            dbController.updateQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID(), Arrays.stream(params).map(Employee.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(employee)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Employee employee) {
        try {
            remove(employee);
            dbController.deleteQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.EMPLOYEES.name().toLowerCase() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (Employee) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

//    public static class EmployeeForm implements IForm<Employee> {
//        @Getter
//        private final List<javafx.scene.Node> form;
//        private final List<TextField> inputs;
//
//        public EmployeeForm() {
//            form = new ArrayList<>();
//            inputs = new ArrayList<>();
//            for (var field : Employee.Field.values()) {
//                var hbox = new HBox();
//                var label = new Label(field.getColName());
//                var input = new TextField();
//                hbox.getChildren().addAll(label, input);
//                form.add(hbox);
//                inputs.add(input);
//            }
//        }
//
//        public void populateForm(Employee entry) {
//            for (var field : Employee.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                input.setText(field.getValueAsString(entry));
//            }
//        }
//
//        public Employee commit() {
//            var entry = new Employee();
//            for (var field : Employee.Field.values()) {
//                var input = (TextField) form.get(field.ordinal());
//                field.setValueFromString(entry, input.getText());
//            }
//            return entry;
//        }
//    }
}