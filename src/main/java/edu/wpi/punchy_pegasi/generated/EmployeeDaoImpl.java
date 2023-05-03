package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Employee;
import edu.wpi.punchy_pegasi.schema.IDao;
import edu.wpi.punchy_pegasi.schema.TableType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class EmployeeDaoImpl implements IDao<java.lang.Long, Employee, Employee.Field> {

    static String[] fields = {"employeeID", "firstName", "lastName"};
    private final PdbController dbController;

    public EmployeeDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<Employee> get(java.lang.Long key) {
        try (var rs = dbController.searchQuery(TableType.EMPLOYEES, "employeeID", key)) {
            rs.next();
            Employee req = new Employee(
                    rs.getObject("employeeID", java.lang.Long.class),
                    rs.getObject("firstName", java.lang.String.class),
                    rs.getObject("lastName", java.lang.String.class));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.lang.Long, Employee> get(Employee.Field column, Object value) {
        return get(new Employee.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, Employee> get(Employee.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, Employee>();
        try (var rs = dbController.searchQuery(TableType.EMPLOYEES, Arrays.stream(params).map(Employee.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                Employee req = new Employee(
                    rs.getObject("employeeID", java.lang.Long.class),
                    rs.getObject("firstName", java.lang.String.class),
                    rs.getObject("lastName", java.lang.String.class));
                if (req != null)
                    map.put(req.getEmployeeID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return map;
    }

    @Override
    public ObservableMap<java.lang.Long, Employee> getAll() {
        var map = new HashMap<java.lang.Long, Employee>();
        try (var rs = dbController.searchQuery(TableType.EMPLOYEES)) {
            while (rs.next()) {
                Employee req = new Employee(
                    rs.getObject("employeeID", java.lang.Long.class),
                    rs.getObject("firstName", java.lang.String.class),
                    rs.getObject("lastName", java.lang.String.class));
                if (req != null)
                    map.put(req.getEmployeeID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return FXCollections.observableMap(map);
    }

    @Override
    public ObservableList<Employee> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(Employee employee) {
        Object[] values = {employee.getEmployeeID(), employee.getFirstName(), employee.getLastName()};
        try {
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
            dbController.updateQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID(), Arrays.stream(params).map(Employee.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(employee)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Employee employee) {
        try {
            dbController.deleteQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}