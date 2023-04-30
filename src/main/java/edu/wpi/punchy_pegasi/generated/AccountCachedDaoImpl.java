package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Account;
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
public class AccountCachedDaoImpl implements IDao<java.lang.Long, Account, Account.Field>, PropertyChangeListener {

    static String[] fields = {"uuid", "username", "password", "employeeID", "accountType"};

    private final ObservableMap<java.lang.Long, Account> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<Account> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public AccountCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.lang.Long, Account>) c -> {
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

    public MFXTableView<Account> generateTable(Consumer<Account> onRowClick, Account.Field[] hidden) {
        var table = new MFXTableView<Account>();
        table.setItems(list);
        for (Account.Field field : Arrays.stream(Account.Field.values()).filter(f -> !Arrays.asList(hidden).contains(f)).toList()) {
            MFXTableColumn<Account> col = new MFXTableColumn<>(field.getColName(), true);
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

    public MFXTableView<Account> generateTable(Consumer<Account> onRowClick) {
        return generateTable(onRowClick, new Account.Field[]{});
    }

    public void add(Account account) {
        if (!cache.containsKey(account.getUuid()))
            cache.put(account.getUuid(), account);
    }

    public void update(Account account) {
        cache.put(account.getUuid(), account);
    }

    public void remove(Account account) {
        cache.remove(account.getUuid());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.ACCOUNTS)) {
            while (rs.next()) {
                Account req = new Account(
                    rs.getObject("uuid", java.lang.Long.class),
                    rs.getObject("username", java.lang.String.class),
                    rs.getObject("password", java.lang.String.class),
                    rs.getObject("employeeID", java.lang.Long.class),
                    edu.wpi.punchy_pegasi.schema.Account.AccountType.valueOf(rs.getString("accountType")));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<Account> get(java.lang.Long key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.lang.Long, Account> get(Account.Field column, Object value) {
        return get(new Account.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.Long, Account> get(Account.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.Long, Account>();
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
    public ObservableMap<java.lang.Long, Account> getAll() {
        return cache;
    }

    @Override
    public ObservableList<Account> getAllAsList() {
        return list;
    }

    @Override
    public void save(Account account) {
        Object[] values = {account.getUuid(), account.getUsername(), account.getPassword(), account.getEmployeeID(), account.getAccountType()};
        try {
            dbController.insertQuery(TableType.ACCOUNTS, fields, values);
//            add(account);
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
//            update(account);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Account account) {
        try {
            dbController.deleteQuery(TableType.ACCOUNTS, "uuid", account.getUuid());
//            remove(account);
        } catch (PdbController.DatabaseException e) {
            log.error("Error deleting", e);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), TableType.ACCOUNTS.name() + "_update")) {
            var update = (PdbController.DatabaseChangeEvent) evt.getNewValue();
            var data = (Account) update.data();
            switch (update.action()) {
                case UPDATE -> update(data);
                case DELETE -> remove(data);
                case INSERT -> add(data);
            }
        }
    }

    public static class AccountForm implements IForm<Account> {
        @Getter
        private final List<javafx.scene.Node> form;
        private final List<TextField> inputs;
        public AccountForm() {
            form = new ArrayList<>();
            inputs = new ArrayList<>();
            for (var field : Account.Field.values()) {
                var hbox = new HBox();
                var label = new Label(field.getColName());
                var input = new TextField();
                hbox.getChildren().addAll(label, input);
                form.add(hbox);
                inputs.add(input);
            }
        }

        public void populateForm(Account entry) {
            for (var field : Account.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                input.setText(field.getValueAsString(entry));
            }
        }

        public Account commit() {
            var entry = new Account();
            for (var field : Account.Field.values()) {
                var input = (TextField) form.get(field.ordinal());
                field.setValueFromString(entry, input.getText());
            }
            return entry;
        }
    }
}