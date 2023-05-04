package edu.wpi.punchy_pegasi.frontend.controllers.requests.adminPage;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.frontend.icons.MaterialSymbols;
import edu.wpi.punchy_pegasi.frontend.icons.PFXIcon;
import edu.wpi.punchy_pegasi.generated.Facade;
import edu.wpi.punchy_pegasi.schema.*;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTableView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.sql.SQLException;
import java.util.*;


@Slf4j
public class AdminTablePageController {

    private final Facade facade = App.getSingleton().getFacade();
    private final Map<String, AdminTable> tables = new LinkedHashMap<>() {{
        put("Node", new AdminTable<>("Node", TableType.NODES, facade::getAllAsListNode));
        put("Location", new AdminTable<>("Location", TableType.LOCATIONNAMES, facade::getAllAsListLocationName));
        put("Edge", new AdminTable<>("Edge", TableType.EDGES, facade::getAllAsListEdge));
        put("Move", new AdminTable<>("Move", TableType.MOVES, facade::getAllAsListMove));
        put("Employee", new AdminTable<>("Employee", TableType.EMPLOYEES, facade::getAllAsListEmployee));
        put("Account", new AdminTable<>("Account", TableType.ACCOUNTS, facade::getAllAsListAccount));
        put("Conference", new AdminTable<>("Conference Room Service Request", TableType.CONFERENCEREQUESTS, facade::getAllAsListConferenceRoomEntry));
        put("Office", new AdminTable<>("Office Supplies Service Request", TableType.OFFICEREQUESTS, facade::getAllAsListOfficeServiceRequestEntry));
        put("Furniture", new AdminTable<>("Furniture Service Request", TableType.FURNITUREREQUESTS, facade::getAllAsListFurnitureRequestEntry));
        put("Food", new AdminTable<>("Food Service Request", TableType.FOODREQUESTS, facade::getAllAsListFoodServiceRequestEntry));
        put("Flower", new AdminTable<>("Flower Service Request", TableType.FLOWERREQUESTS, facade::getAllAsListFlowerDeliveryRequestEntry));
        put("Alert", new AdminTable<>("Alerts", TableType.ALERT, facade::getAllAsListAlert));
        put("Signage", new AdminTable<>("Signage", TableType.SIGNAGE, facade::getAllAsListSignage));
    }};
    public PFXButton importButton;
    public PFXButton exportButton;
    public PFXButton clearButton;
    @FXML
    private MFXComboBox<String> displayTableTypeComboBox;
    @FXML
    private MFXComboBox<PdbController.Source> displayDatabaseComboBox;
    @FXML
    private Label databaseLabel;
    @FXML
    private VBox tableContainer;
    @FXML
    private PFXButton submitEditButton = new PFXButton("Submit Changes");
    @FXML
    private PFXButton addButton = new PFXButton("Add New Entry");
    @FXML
    private PFXButton removeButton = new PFXButton("Remove");
    @FXML
    private GridPane editContainer;
    private AdminTable currentTable;
    private ArrayList<TextArea> textAreas = new ArrayList<>();
    private GridPane gp = new GridPane();

    // import export stuff
    private String filePath = "";
    private File selectedFile = new File("");
    private File selectedDir = new File("");
    @FXML
    private Label fileText = new Label();
    FileChooser fileChooser = new FileChooser();
    DirectoryChooser directoryChooser = new DirectoryChooser();
    private ObservableMap<Long, edu.wpi.punchy_pegasi.schema.Node> nodes;
    private ObservableMap<Long, LocationName> locations;
    private ObservableMap<Long, Move> moves;

    private ObservableMap<Long, Account> accounts;
    PdbController pdb = App.getSingleton().getPdb();

    public void initialize() {
        VBox vC = refreshInit();
        nodes = facade.getAllNode();
        moves = facade.getAllMove();
        locations = facade.getAllLocationName();
        accounts = facade.getAllAccount();
        databaseLabel.setText("Select Database: ");
        // displayDatabaseComboBox.selectItem(App.getSingleton().getPdb().source);

        fileChooser.setTitle("File Chooser");

        ObservableList<String> displayTableTypeList = FXCollections.observableArrayList();
        tables.values().forEach(f -> {
            displayTableTypeList.add(f.humanReadableName);
        });
        displayTableTypeComboBox.setItems(displayTableTypeList);

        ObservableList<PdbController.Source> sources = FXCollections.observableArrayList(PdbController.Source.values());
        //sources.remove(App.getSingleton().getPdb().source);
        displayDatabaseComboBox.setItems(sources);
        displayDatabaseComboBox.selectItem(App.getSingleton().getPdb().source);

        initTables();
        showTable(null);

        displayTableTypeComboBox.setOnAction(e -> {
            String name = displayTableTypeComboBox.getSelectedItem();
            tables.values().stream().filter(f -> Objects.equals(f.humanReadableName, name)).forEach(f -> {
                showTable(f);
                currentTable = f;
            });
            displayEditComponent();
        });
        displayDatabaseComboBox.setOnAction(e -> {
            PdbController.Source source = displayDatabaseComboBox.getSelectedItem();

            if (!(source == App.getSingleton().getPdb().source)) {
                refreshInit();
                App.getSingleton().getExecutorService().execute(() -> Platform.runLater(() -> App.getSingleton().getLayout().showOverlay(vC, false)));


                try {
                    App.getSingleton().getFacade().switchDatabase(source);
                } catch (SQLException | PdbController.DatabaseException ex) {
                    log.error("Could not switch database");
                }
                for (var tt : TableType.values()) {
                    try {
                        pdb.initTableByType(tt);
                    } catch (PdbController.DatabaseException err) {
                        log.error("Could not init table " + tt.name());
                    }
                }
                Platform.runLater(() -> App.getSingleton().getLayout().hideOverlay());
            }
        });

        tables.values().forEach(t -> t.setRowClicked(this::populateForm));

        importButton.setOnAction(e -> {
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            selectedFile = fileChooser.showOpenDialog(App.getSingleton().getPopupStage());

            if (selectedFile != null && displayTableTypeComboBox.getSelectedItem() != null) {
                filePath = selectedFile.getAbsolutePath();
                fileText.setText(filePath);
                try {
                    pdb.importTable(currentTable.tableType, filePath);
                } catch (PdbController.DatabaseException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        exportButton.setOnAction(e -> {
            selectedDir = directoryChooser.showDialog(App.getSingleton().getPopupStage());
            if (selectedDir != null) {
                fileText.setText(selectedDir.getAbsolutePath());
                try {
                    pdb.exportTable(selectedDir + "/" + currentTable.humanReadableName + ".csv", currentTable.tableType);
                    selectedDir = null;
                } catch (PdbController.DatabaseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        submitEditButton.setOnAction(e -> {
            try {
                switch (currentTable.getTableType()) {
                    case NODES:
                        facade.updateNode(commit(edu.wpi.punchy_pegasi.schema.Node.builder()).build(), Arrays.stream(edu.wpi.punchy_pegasi.schema.Node.Field.values()).filter(f -> f != edu.wpi.punchy_pegasi.schema.Node.Field.NODE_ID).toArray(edu.wpi.punchy_pegasi.schema.Node.Field[]::new));
                        break;
                    case LOCATIONNAMES:
                        facade.updateLocationName(commit(LocationName.builder()).build(), Arrays.stream(LocationName.Field.values()).filter(f -> f != LocationName.Field.UUID).toArray(LocationName.Field[]::new));
                        break;
                    case EDGES:
                        facade.updateEdge(commit(Edge.builder()).build(), Arrays.stream(Edge.Field.values()).filter(f -> f != Edge.Field.UUID).toArray(Edge.Field[]::new));
                        break;
                    case MOVES:
                        facade.updateMove(commit(Move.builder()).build(), Arrays.stream(Move.Field.values()).filter(f -> f != Move.Field.UUID).toArray(Move.Field[]::new));
                        break;
                    case EMPLOYEES:
                        facade.updateEmployee(commit(Employee.builder()).build(), Arrays.stream(Employee.Field.values()).filter(f -> f != Employee.Field.EMPLOYEE_ID).toArray(Employee.Field[]::new));
                        break;
                    case ACCOUNTS:
                        facade.updateAccount(commit(Account.builder()).build(), Arrays.stream(Account.Field.values()).filter(f -> f != Account.Field.UUID).toArray(Account.Field[]::new));
                        break;
                    case CONFERENCEREQUESTS:
                        facade.updateConferenceRoomEntry(commit(ConferenceRoomEntry.builder()).build(), Arrays.stream(ConferenceRoomEntry.Field.values()).filter(f -> f != ConferenceRoomEntry.Field.SERVICE_ID).toArray(ConferenceRoomEntry.Field[]::new));
                        break;
                    case OFFICEREQUESTS:
                        facade.updateOfficeServiceRequestEntry(commit(OfficeServiceRequestEntry.builder()).build(), Arrays.stream(OfficeServiceRequestEntry.Field.values()).filter(f -> f != OfficeServiceRequestEntry.Field.SERVICE_ID).toArray(OfficeServiceRequestEntry.Field[]::new));
                        break;
                    case FURNITUREREQUESTS:
                        facade.updateFurnitureRequestEntry(commit(FurnitureRequestEntry.builder()).build(), Arrays.stream(FurnitureRequestEntry.Field.values()).filter(f -> f != FurnitureRequestEntry.Field.SERVICE_ID).toArray(FurnitureRequestEntry.Field[]::new));
                        break;
                    case FOODREQUESTS:
                        facade.updateFoodServiceRequestEntry(commit(FoodServiceRequestEntry.builder()).build(), Arrays.stream(FoodServiceRequestEntry.Field.values()).filter(f -> f != FoodServiceRequestEntry.Field.SERVICE_ID).toArray(FoodServiceRequestEntry.Field[]::new));
                        break;
                    case FLOWERREQUESTS:
                        facade.updateFlowerDeliveryRequestEntry(commit(FlowerDeliveryRequestEntry.builder()).build(), Arrays.stream(FlowerDeliveryRequestEntry.Field.values()).filter(f -> f != FlowerDeliveryRequestEntry.Field.SERVICE_ID).toArray(FlowerDeliveryRequestEntry.Field[]::new));
                        break;
                }
            } catch (InvalidArgumentException ex) {
                App.getSingleton().getLayout().notify("One of the fields is not valid", "");
            }
        });

        removeButton.setOnAction(e -> {
            try {
                switch (currentTable.getTableType()) {
                    case NODES:
                        facade.deleteNode(commit(edu.wpi.punchy_pegasi.schema.Node.builder()).build());
                        break;
                    case LOCATIONNAMES:
                        facade.deleteLocationName(commit(LocationName.builder()).build());
                        break;
                    case EDGES:
                        facade.deleteEdge(commit(Edge.builder()).build());
                        break;
                    case MOVES:
                        facade.deleteMove(commit(Move.builder()).build());
                        break;
                    case EMPLOYEES:
                        facade.deleteEmployee(commit(Employee.builder()).build());
                        break;
                    case ACCOUNTS:
                        facade.deleteAccount(commit(Account.builder()).build());
                        break;
                    case CONFERENCEREQUESTS:
                        facade.deleteConferenceRoomEntry(commit(ConferenceRoomEntry.builder()).build());
                        break;
                    case OFFICEREQUESTS:
                        facade.deleteOfficeServiceRequestEntry(commit(OfficeServiceRequestEntry.builder()).build());
                        break;
                    case FURNITUREREQUESTS:
                        facade.deleteFurnitureRequestEntry(commit(FurnitureRequestEntry.builder()).build());
                        break;
                    case FOODREQUESTS:
                        facade.deleteFoodServiceRequestEntry(commit(FoodServiceRequestEntry.builder()).build());
                        break;
                    case FLOWERREQUESTS:
                        facade.deleteFlowerDeliveryRequestEntry(commit(FlowerDeliveryRequestEntry.builder()).build());
                        break;
                }
            } catch (InvalidArgumentException ex) {
                System.out.println("Invalid Row Delete");
            }

        });

        addButton.setOnAction(e -> {
            try {
                switch (currentTable.getTableType()) {
                    case NODES:
                        var node = Node.builder();
                        var newNodeId = nodes.values().stream().mapToLong(edu.wpi.punchy_pegasi.schema.Node::getNodeID).max().orElse(1) + 1;
                        idCommit(node);
                        node.nodeID(newNodeId);
                        facade.saveNode(node.build());
                        break;
                    case LOCATIONNAMES:
                        var locationName = LocationName.builder();
                        var newLocationId = locations.values().stream().mapToLong(LocationName::getUuid).max().orElse(1) + 1;
                        idCommit(locationName);
                        locationName.uuid(newLocationId);
                        facade.saveLocationName(locationName.build());
                        break;
                    case EDGES:
                        var edge = Edge.builder();
                        idCommit(edge);
                        edge.uuid(UUID.randomUUID());
                        facade.saveEdge(edge.build());
                        break;
                    case MOVES:
                        var move = Move.builder();
                        var newMovevId = moves.values().stream().mapToLong(Move::getUuid).max().orElse(1) + 1;
                        idCommit(move);
                        move.uuid(newMovevId);
                        facade.saveMove(move.build());
                        break;
                    case EMPLOYEES:
                        var employee = Employee.builder();
                        var newEmployeeId = facade.getAllEmployee().values().stream().mapToLong(Employee::getEmployeeID).max().orElse(0) + 1;
                        idCommit(employee);
                        employee.employeeID(newEmployeeId);
                        facade.saveEmployee(employee.build());
                        break;
                    case ACCOUNTS:
                        var account = Account.builder();
                        var newAccountId = accounts.values().stream().mapToLong(Account::getUuid).max().orElse(1) + 1;
                        idCommit(account);
                        account.uuid(newAccountId);
                        facade.saveAccount(account.build());
                        break;
                    case CONFERENCEREQUESTS:
                        facade.saveConferenceRoomEntry(serviceIdCommit(ConferenceRoomEntry.builder()).build());
                        break;
                    case OFFICEREQUESTS:
                        facade.saveOfficeServiceRequestEntry(serviceIdCommit(OfficeServiceRequestEntry.builder()).build());
                        break;
                    case FURNITUREREQUESTS:
                        facade.saveFurnitureRequestEntry(serviceIdCommit(FurnitureRequestEntry.builder()).build());
                        break;
                    case FOODREQUESTS:
                        facade.saveFoodServiceRequestEntry(serviceIdCommit(FoodServiceRequestEntry.builder()).build());
                        break;
                    case FLOWERREQUESTS:
                        facade.saveFlowerDeliveryRequestEntry(serviceIdCommit(FlowerDeliveryRequestEntry.builder()).build());
                        break;
                }
            } catch (InvalidArgumentException ex) {
                App.getSingleton().getLayout().notify("One of the fields is not valid", "");
            }
        });
    }

    public void showTable(AdminTable<?> tableType) {
        currentTable = tableType;
        if (currentTable != null) {
            currentTable.getTable().setVisible(true);
            currentTable.getTable().setManaged(true);
            Platform.runLater(currentTable.table::autosizeColumns);
            currentTable.table.autosizeColumnsOnInitialization();
        }

        tables.values().stream().filter(f -> tableType == null || !Objects.equals(f.tableType, tableType.tableType)).forEach(f -> {
            f.getTable().setVisible(false);
            f.getTable().setManaged(false);
        });
    }

    public void initTables() {
        tables.values().forEach(AdminTable::init);
        tableContainer.getChildren().addAll(tables.values().stream().map(AdminTable::getTable).toList());
        tableContainer.getChildren().forEach(t -> {
            var table = (MFXTableView<?>) t;
            table.prefWidthProperty().bind(tableContainer.widthProperty());
            table.prefHeightProperty().bind(tableContainer.heightProperty());
        });
    }

    private List<javafx.scene.Node> form;
    private List<TextField> inputs;

    public void displayEditComponent() {
        form = new ArrayList<>();
        inputs = new ArrayList<>();
        int counter = 0;
        int rowcounter = 0;
        for (var field : Arrays.stream(currentTable.tableType.getFieldEnum().getEnumConstants()).map(f -> (IField) f).toList()) {
            var hbox = new HBox();
            var label = new Label(field.getColName());
            var input = new TextField();
            input.setPromptText(field.getColName());
            if (field.isPrimaryKey()) {
                input.setEditable(false);
            }

            //elements.add(label, 0, counter);
            //elements.add(input, 1, counter);
            if (counter == 5) {
                counter = 0;
                rowcounter += 2;
            }
            GridPane.setRowIndex(label, rowcounter);
            GridPane.setRowIndex(input, rowcounter + 1);
            GridPane.setColumnIndex(label, counter);
            GridPane.setColumnIndex(input, counter);
            counter++;
//            hbox.getStyleClass().add("admin-table-container");
//            hbox.getChildren().addAll(label, input);
            //hbox.getChildren().add(input);
            form.add(label);
            form.add(input);
            inputs.add(input);
        }
        //form.add(gp);
        editContainer.getStyleClass().add("admin-table-container");
        editContainer.getChildren().clear();
        editContainer.getChildren().addAll(form);
    }

    public void populateForm(Object entry) {
        for (var field : Arrays.stream(currentTable.tableType.getFieldEnum().getEnumConstants()).map(f -> (IField) f).toList()) {
            var input = inputs.get(field.ordinal());
            input.setText(field.getValueAsString(entry));
        }
    }

    public <T> T commit(T builder) throws InvalidArgumentException {
        for (var field : Arrays.stream(currentTable.tableType.getFieldEnum().getEnumConstants()).map(f -> (IField) f).toList()) {
            var input = inputs.get(field.ordinal());
            try {
                field.setValueFromString(builder, input.getText());
            } catch (Exception e) {
                //alert the user that the input is invalid
                throw new InvalidArgumentException("Invalid input for field " + field.getColName());
            }
        }
        return builder;
    }

    public <T> T idCommit(T builder) throws InvalidArgumentException {
        for (var field : Arrays.stream(currentTable.tableType.getFieldEnum().getEnumConstants()).map(f -> (IField) f).toList()) {
            var input = inputs.get(field.ordinal());
            try {
                if (!field.getColName().toLowerCase().contains("id")) {
                    field.setValueFromString(builder, input.getText());
                } else {
                    field.setValueFromString(builder, "1");
                }
            } catch (Exception e) {
                //alert the user that the input is invalid
                throw new InvalidArgumentException("Invalid input for field " + field.getColName());
            }
        }
        return builder;
    }

    public <T> T serviceIdCommit(T builder) throws InvalidArgumentException {
        for (var field : Arrays.stream(currentTable.tableType.getFieldEnum().getEnumConstants()).map(f -> (IField) f).toList()) {
            var input = inputs.get(field.ordinal());
            try {
                if (!field.getColName().toLowerCase().contains("serviceid")) {
                    field.setValueFromString(builder, input.getText());
                } else {
                    field.setValueFromString(builder, (UUID.randomUUID()).toString());
                }
            } catch (Exception e) {
                //alert the user that the input is invalid
                throw new InvalidArgumentException("Invalid input for field " + field.getColName());
            }
        }
        return builder;
    }

    // create an new exception class for this
    private class InvalidArgumentException extends Exception {
        public InvalidArgumentException(String message) {
            super(message);
        }
    }

    public VBox refreshInit() {

        var refresh = new PFXIcon(MaterialSymbols.REFRESH);
        var text = new Label("Refreshing...");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setTextFill(Color.BLACK);

        var vC = new VBox();
        vC.setAlignment(Pos.CENTER);
        vC.setStyle("-fx-background-color: -pfx-secondary-transparent");
        var hC = new HBox();
        hC.setAlignment(Pos.CENTER);
        vC.getChildren().add(hC);
        var c = new VBox(text, refresh);
        c.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 12;
                -fx-padding: 10;
                -fx-spacing: 10;
                -fx-alignment: CENTER;
                """);
        hC.getChildren().add(c);
        return vC;
    }
}
