package edu.wpi.punchy_pegasi.frontend.controllers.requests.adminPage;

import edu.wpi.punchy_pegasi.schema.IField;
import edu.wpi.punchy_pegasi.schema.TableType;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AdminTable<T> {
    final String humanReadableName;
    @Getter
    final MFXTableView<T> table;
    @Getter
    final TableType tableType;
    @Getter
    final Supplier<ObservableList<T>> getAll;

    @Getter
    @Setter
    private Consumer<T> rowClicked;

    public AdminTable(String humanReadableName, TableType tableType, Supplier<ObservableList<T>> getAll) {
        this.humanReadableName = humanReadableName;
        table = new MFXTableView<>();
        this.tableType = tableType;
        this.getAll = getAll;
    }

    public void init() {
        table.setItems(getAll.get());
        table.autosizeColumnsOnInitialization();
        // Create columns
        for (Object field : tableType.getFieldEnum().getEnumConstants()) {
            var iField = (IField<T>) field;
            MFXTableColumn<T> col = new MFXTableColumn<>(iField.getColName(), true);
            col.setPickOnBounds(false);

            col.setRowCellFactory(p -> {
                var cell = new MFXTableRowCell<>(iField::getValue);
//                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
//                    if (!(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1)) return;
//                    if (rowClicked != null)
//                        rowClicked.accept(p);
//                });
                return cell;
            });
            table.getTableColumns().add(col);
        }
//        table.setTableRowFactory(r -> {
//            var row = new MFXTableRow<>(table, r);
//            row.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
//                if (!(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1)) return;
//                if (rowClicked != null)
//                    rowClicked.accept(r);
//            });
//            return row;
//        });
        table.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
            if (rowClicked != null)
                newValue.values().stream().findFirst().ifPresent(r -> rowClicked.accept(r));
        });
    }
}

