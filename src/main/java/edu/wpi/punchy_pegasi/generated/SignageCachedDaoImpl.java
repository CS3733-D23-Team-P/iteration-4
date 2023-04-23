package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.Signage;
import edu.wpi.punchy_pegasi.schema.IDao;
import edu.wpi.punchy_pegasi.schema.TableType;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class SignageCachedDaoImpl implements IDao<java.lang.String, Signage, Signage.Field>, PropertyChangeListener {

    static String[] fields = {"longName", "directionType"};

    private final ObservableMap<java.lang.String, Signage> cache = FXCollections.observableMap(new LinkedHashMap<>());
    private final ObservableList<Signage> list = FXCollections.observableArrayList();
    private final PdbController dbController;

    public SignageCachedDaoImpl(PdbController dbController) {
        this.dbController = dbController;
        cache.addListener((MapChangeListener<java.lang.String, Signage>) c -> {
            if (c.wasRemoved()) {
                list.remove(c.getValueRemoved());
            } else if (c.wasAdded()) {
                list.add(c.getValueAdded());
            }
        });
        initCache();
        this.dbController.addPropertyChangeListener(this);
    }

    public void add(Signage signage) {
        if (!cache.containsKey(signage.getLongName()))
            cache.put(signage.getLongName(), signage);
    }

    public void update(Signage signage) {
        cache.put(signage.getLongName(), signage);
    }

    public void remove(Signage signage) {
        cache.remove(signage.getLongName());
    }

    private void initCache() {
        try (var rs = dbController.searchQuery(TableType.SIGNAGE)) {
            while (rs.next()) {
                Signage req = new Signage(
                    rs.getObject("longName", java.lang.String.class),
                    edu.wpi.punchy_pegasi.schema.Signage.DirectionType.valueOf(rs.getString("directionType")));
                add(req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
    }

    @Override
    public Optional<Signage> get(java.lang.String key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public Map<java.lang.String, Signage> get(Signage.Field column, Object value) {
        return get(new Signage.Field[]{column}, new Object[]{value});
    }

    @Override
    public Map<java.lang.String, Signage> get(Signage.Field[] params, Object[] value) {
        var map = new HashMap<java.lang.String, Signage>();
        if (params.length != value.length) return map;
        cache.values().forEach(v -> {
            var include = true;
            for (int i = 0; i < params.length; i++)
                include &= Objects.equals(params[i].getValue(v), value[i]);
            if (include)
                map.put(v.getLongName(), v);
        });
        return map;
    }

    @Override
    public ObservableMap<java.lang.String, Signage> getAll() {
        return cache;
    }

    @Override
    public ObservableList<Signage> getAllAsList() {
        return list;
    }

    @Override
    public void save(Signage signage) {
        Object[] values = {signage.getLongName(), signage.getDirectionType()};
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
            dbController.updateQuery(TableType.SIGNAGE, "longName", signage.getLongName(), Arrays.stream(params).map(Signage.Field::getColName).toList().toArray(new String[params.length]), Arrays.stream(params).map(p -> p.getValue(signage)).toArray());
//            update(signage);
        } catch (PdbController.DatabaseException e) {
            log.error("Error saving", e);
        }
    }

    @Override
    public void delete(Signage signage) {
        try {
            dbController.deleteQuery(TableType.SIGNAGE, "longName", signage.getLongName());
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
}