package edu.wpi.punchy_pegasi.frontend.controllers;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.components.PFXAlertCard;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.frontend.components.PFXListView;
import edu.wpi.punchy_pegasi.generated.Facade;
import edu.wpi.punchy_pegasi.schema.*;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.PopOver;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class HomePageController {
    private final Facade facade = App.getSingleton().getFacade();
    private final ObservableMap<Long, LocationName> locationNames = facade.getAllLocationName();
    private final Map<Long, Employee> employees = facade.getAllEmployee();
    @FXML
    MFXTableView<RequestEntry> requestTable;
    @FXML
    private VBox tableContainer;
    @FXML
    private PieChart piechart;

    @FXML
    private VBox alertsContainer;
    @FXML
    private ScrollPane alertScrollPane;
    @FXML
    private Label noAlertsLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private VBox dateTimeBox;
    @FXML
    private AnchorPane dateTimeAnchor;

    private ObservableList<Alert> alerts;

    private FilteredList<Alert> read;

    @FXML
    private void initialize() {
        configTimer(1000);

        requestTable.prefWidthProperty().bind(tableContainer.widthProperty());
        requestTable.prefHeightProperty().bind(tableContainer.heightProperty());
        ObservableList<RequestEntry> requestEntries = facade.getAllAsListRequestEntry();
        var doneSlice = new PieChart.Data("Done", 0);
        var processingSlice =  new PieChart.Data("Processing", 0);
        doneSlice.pieValueProperty().bind(Bindings.createDoubleBinding(() -> requestEntries.stream().mapToDouble(r -> r.getStatus() == RequestEntry.Status.DONE ? 1 : 0).sum(), requestEntries));
        processingSlice.pieValueProperty().bind(Bindings.createDoubleBinding(() -> requestEntries.stream().mapToDouble(r -> r.getStatus() == RequestEntry.Status.PROCESSING ? 1 : 0).sum(), requestEntries));
        piechart.setData(FXCollections.observableArrayList(doneSlice, processingSlice));
        piechart.setTitle("Service Request");
        piechart.setLegendVisible(false);

        alerts = App.getSingleton().getFacade().getAllAsListAlert().filtered(a -> a.getEmployeeID().equals(App.getSingleton().getAccount().getEmployeeID()) && a.getAlertType().equals(Alert.AlertType.EMPLOYEE));
        alerts.addListener((ListChangeListener<? super Alert>) e -> {
            System.out.println("");
        });
        read = alerts.filtered(a -> a.getReadStatus() == Alert.ReadStatus.READ);
//        read.setPredicate(a-> a.getReadStatus() == Alert.ReadStatus.READ);
        read.addListener((ListChangeListener<? super Alert>) e -> {
            System.out.println("");
        });
//        var listViewUnread = new PFXListView<>(alerts.filtered(a-> a.getReadStatus() == Alert.ReadStatus.UNREAD), PFXAlertCard::new, a -> a.getUuid().toString());
//        var listViewRead = new PFXListView<>(alerts.filtered(a-> a.getReadStatus() == Alert.ReadStatus.READ), PFXAlertCard::new, a -> a.getUuid().toString());
        alertScrollPane.setContent(new PFXListView<>(alerts, PFXAlertCard::new, a -> a.getUuid().toString()));
        noAlertsLabel.visibleProperty().bind(Bindings.createBooleanBinding(alerts::isEmpty, alerts));
        noAlertsLabel.managedProperty().bind(Bindings.createBooleanBinding(alerts::isEmpty, alerts));
        alertScrollPane.visibleProperty().bind(Bindings.createBooleanBinding(() -> !alerts.isEmpty(), alerts));
        alertScrollPane.managedProperty().bind(Bindings.createBooleanBinding(() -> !alerts.isEmpty(), alerts));

        initRequestTable();
        showServiceRequestTable(true);
    }

    private void showServiceRequestTable(boolean show) {
        requestTable.setVisible(show);
        requestTable.setManaged(show);
    }

    private void rowClicked(GenericRequestEntry entry) {
        var original = entry.originalEntry;
        var tt = Arrays.stream(TableType.values()).filter(f -> f.getClazz() == original.getClass()).findFirst().get();
    }

    private void initRequestTable() {
        var employeeID = App.getSingleton().getAccount().getEmployeeID();
        ObservableList<RequestEntry> requestEntries = facade.getAllAsListRequestEntry()
                .filtered(e ->
                        App.getSingleton().getAccount().getAccountType().getShieldLevel() >= Account.AccountType.ADMIN.getShieldLevel()
                                || Objects.equals(e.getStaffAssignment(), employeeID));

        MFXTableColumn<RequestEntry> locationCol = new MFXTableColumn<>("Location", true);
        MFXTableColumn<RequestEntry> employeeCol = new MFXTableColumn<>("Employee", true);
        MFXTableColumn<RequestEntry> additionalCol = new MFXTableColumn<>("Additional Notes", true);
        MFXTableColumn<RequestEntry> statusCol = new MFXTableColumn<>("Status", true);
        MFXTableColumn<RequestEntry> typeCol = new MFXTableColumn<>("Request Type", true);
//        MFXTableColumn<PFXButton> buttonCol = new MFXTableColumn<>();
//        location = locationNames.getOrDefault(re.getLocationName(), new LocationName(null, "Unknown location", "", null)).getLongName();
//        assigned = employees.getOrDefault(re.getStaffAssignment(), new Employee(0L, "Unknown", "Employee")).getFullName();
//        additionalNotes = re.getAdditionalNotes();
//        status = re.getStatus();
//        tableType = Arrays.stream(TableType.values())
//                .filter(tt -> tt.getClazz() == re.getClass())
//                .findFirst()
//                .orElseGet(() -> TableType.GENERIC);
        locationCol.setRowCellFactory(r -> new MFXTableRowCell<>(re -> locationNames.getOrDefault(re.getLocationName(), new LocationName(null, "Unknown location", "", null)).getLongName()));
        employeeCol.setRowCellFactory(r -> new MFXTableRowCell<>(re -> employees.getOrDefault(re.getStaffAssignment(), new Employee(0L, "Unknown", "Employee")).getFullName()));
        additionalCol.setRowCellFactory(r -> new MFXTableRowCell<>(RequestEntry::getAdditionalNotes));
        statusCol.setRowCellFactory(r -> {
            var cell = new MFXTableRowCell<>(RequestEntry::getStatus);

            return cell;
        });
        typeCol.setRowCellFactory(r -> new MFXTableRowCell<>(re -> Arrays.stream(TableType.values())
                .filter(tt -> tt != TableType.REQUESTS && tt.getClazz() == re.getClass())
                .findFirst()
                .orElse(TableType.GENERIC)));


        requestTable.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
            var rO = newValue.entrySet().stream().findFirst();
            if (rO.isEmpty()) {
                return;
            }
            var popover = new PopOver();
            var r = rO.get().getValue();
            var i = rO.get().getKey();
            VBox vbox = new VBox();
            popover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(10));
            PFXButton button = new PFXButton();
            button.setMinWidth(200);
            button.setMaxHeight(200);

            button.setOnAction(m -> {
                if (button.getText().equalsIgnoreCase("change to done")) {
                    button.setText("Change To Processing");
                    button.setStyle("-fx-background-color: -pfx-danger");
                    facade.updateRequestEntry(r.withStatus(RequestEntry.Status.DONE), new RequestEntry.Field[]{RequestEntry.Field.STATUS});
                    requestTable.update();
                } else if (button.getText().equalsIgnoreCase("change to processing")) {
                    button.setText("Change To Done");
                    button.setStyle("-fx-background-color: -pfx-success");
                    facade.updateRequestEntry(r.withStatus(RequestEntry.Status.PROCESSING), new RequestEntry.Field[]{RequestEntry.Field.STATUS});
                    requestTable.update();
                }
                });

            if (r.getStatus() == RequestEntry.Status.DONE) {
                button.setText("Change To Processing");
                button.setStyle("-fx-background-color: -pfx-danger");
            } else if (r.getStatus() == RequestEntry.Status.PROCESSING) {
                button.setText("Change To Done");
                button.setStyle("-fx-background-color: -pfx-success");
            }

            vbox.getChildren().add(button);
            vbox.getStyleClass().add("homepage-popup");
            popover.setContentNode(vbox);
            popover.show(requestTable.getCell(i));
        });


        requestTable.getTableColumns().addAll(locationCol, employeeCol, additionalCol, statusCol, typeCol);
        requestTable.setItems(requestEntries);
        requestTable.prefWidthProperty().bind(tableContainer.widthProperty());
        requestTable.prefHeightProperty().bind(tableContainer.heightProperty());
        requestTable.autosizeColumnsOnInitialization();
    }

    private void updateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        timeLabel.setText(formatter.format(date));
    }

    private void configTimer(final long interuptPeriodMill) {
        long currTime = System.currentTimeMillis();
        long startDelay = interuptPeriodMill - (currTime % interuptPeriodMill);
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> updateTime());
            }
        }, startDelay, interuptPeriodMill);
    }

    @FXML
    private void resizeColumns() {
        requestTable.autosizeColumns();
    }

    private class GenericRequestEntry {
        RequestEntry originalEntry;
        String location;
        String assigned;
        String additionalNotes;
        @Setter
        RequestEntry.Status status;
        TableType tableType;

        GenericRequestEntry(RequestEntry re) {
            originalEntry = re;
            location = locationNames.getOrDefault(re.getLocationName(), new LocationName(null, "Unknown location", "", null)).getLongName();
            assigned = employees.getOrDefault(re.getStaffAssignment(), new Employee(0L, "Unknown", "Employee")).getFullName();
            additionalNotes = re.getAdditionalNotes();
            status = re.getStatus();
            tableType = Arrays.stream(TableType.values())
                    .filter(tt -> tt.getClazz() == re.getClass())
                    .findFirst()
                    .orElse(TableType.GENERIC);
        }
    }
}