package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Account;
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
public class AccountDaoImpl implements IDao<java.lang.Long, Account, Account.Field> {

    static String[] fields = {"uuid", "username", "password", "employeeID", "accountType", "theme", "accent"};
    private final PdbController dbController;

    public AccountDaoImpl(PdbController dbController) {
        this.dbController = dbController;
    }

    @Override
    public Optional<Account> get(java.lang.Long key) {
        try (var rs = dbController.searchQuery(TableType.ACCOUNTS, "uuid", key)) {
            rs.next();
            Account req = new Account(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("username", java.lang.String.class),
                    rs.getObject("password", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class),
                    edu.wpi.punchy_pegasi.schema.Account.AccountType.valueOf(rs.getString("accountType")),
                    edu.wpi.punchy_pegasi.schema.Account.Theme.valueOf(rs.getString("theme")),
                    edu.wpi.punchy_pegasi.schema.Account.Accent.valueOf(rs.getString("accent")));
            return Optional.ofNullable(req);
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<java.lang.Long, Account> get(Account.Field column, Object value) {
        return get(new Account.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, Account> get(Account.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, Account>();
        try (var rs = dbController.searchQuery(TableType.ACCOUNTS, Arrays.stream(params).map(Account.Field::getColName).toList().toArray(new String[params.length]), value)) {
            while (rs.next()) {
                Account req = new Account(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("username", java.lang.String.class),
                    rs.getObject("password", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class),
                    edu.wpi.punchy_pegasi.schema.Account.AccountType.valueOf(rs.getString("accountType")),
                    edu.wpi.punchy_pegasi.schema.Account.Theme.valueOf(rs.getString("theme")),
                    edu.wpi.punchy_pegasi.schema.Account.Accent.valueOf(rs.getString("accent")));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return map;
    }

    @Override
    public ObservableMap<java.lang.Long, Account> getAll() {
        var map = new HashMap<java.lang.Long, Account>();
        try (var rs = dbController.searchQuery(TableType.ACCOUNTS)) {
            while (rs.next()) {
                Account req = new Account(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("username", java.lang.String.class),
                    rs.getObject("password", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class),
                    edu.wpi.punchy_pegasi.schema.Account.AccountType.valueOf(rs.getString("accountType")),
                    edu.wpi.punchy_pegasi.schema.Account.Theme.valueOf(rs.getString("theme")),
                    edu.wpi.punchy_pegasi.schema.Account.Accent.valueOf(rs.getString("accent")));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        return FXCollections.observableMap(map);
    }

    @Override
    public ObservableList<Account> getAllAsList() {
        return FXCollections.observableList(getAll().values().stream().toList());
    }

    @Override
    public void save(Account account) {
        Object[] values = {account.getUuid(), account.getUsername(), account.getPassword(), account.getEmployeeID(), account.getAccountType(), account.getTheme(), account.getAccent()};
        try {
            dbController.insertQuery(TableType.ACCOUNTS, fields, values);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }

    }

    @Override
    public void update(Account account, Account.Field[] params) {
        if (params.length < 1)
            return;
        try {
            dbController.updateQuery(TableType.ACCOUNTS, "uuid", account.getUuid(), Arrays.stream(params).map(Account.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(account)).toArray());
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Account account) {
        try {
            dbController.deleteQuery(TableType.ACCOUNTS, "uuid", account.getUuid());
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }
}