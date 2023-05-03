package edu.wpi.punchy_pegasi.frontend.controllers.requests.adminPage;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.components.PFXAlertCard;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.frontend.components.PFXListView;
import edu.wpi.punchy_pegasi.generated.Facade;
import edu.wpi.punchy_pegasi.schema.Alert;
import edu.wpi.punchy_pegasi.schema.Employee;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class AdminAlertPageController {
    @FXML
    public HBox timePickerContainer;
    @FXML
    public MFXDatePicker endDatePicker;
    @FXML
    private TextField alertDescription;
    @FXML
    private TextField alertTitle;
    @FXML
    private VBox alertsContainer;
    @FXML
    private PFXButton sendButton;
    @FXML
    private VBox activeAlertsContainer;
    @FXML
    private BorderPane container;
    @FXML
    private PFXButton removeButton;
    @FXML
    private MFXComboBox<Alert.AlertType> alertTypeComboBox;
    @FXML
    private MFXComboBox<String> endTimeComboBox;
    @FXML
    private HBox employeePickerContainer;
    @FXML
    private MFXFilterComboBox<Employee> employeeComboBox;
    private final Facade facade = App.getSingleton().getFacade();
    private ObservableList<Alert> alerts;
    private ObservableList<Employee> employees;


    private final ObservableList<String> timeList = FXCollections.observableArrayList("12:00am", "12:30am", "1:00am", "1:30am", "2:00am", "2:30am", "3:00am", "3:30am", "4:00am", "4:30am", "5:00am", "5:30am", "6:00am", "6:30am", "7:00am", "7:30am", "8:00am", "8:30am", "9:00am", "9:30am", "10:00am", "10:30am", "11:00am", "11:30am", "12:00pm", "12:30pm", "12:00pm", "12:30pm", "1:00pm", "1:30pm", "2:00pm", "2:30pm", "3:00pm", "3:30pm", "4:00pm", "4:30pm", "5:00pm", "5:30pm", "6:00pm", "6:30pm", "7:00pm", "7:30pm", "8:00pm", "8:30pm", "9:00pm", "9:30pm", "10:00pm", "10:30pm", "11:00pm", "11:30pm");

    public void initialize() {
        var employeeToName = new StringConverter<Employee>() {

            @Override
            public String toString(Employee employee) {
                if (employee == null) return "";
                return employee.getFirstName() + " " + employee.getLastName();
            }

            @Override
            public Employee fromString(String string) {
                return null;
            }
        };
        employeeComboBox.setConverter(employeeToName);

        timePickerContainer.setVisible(false);
        timePickerContainer.setManaged(false);
        employeePickerContainer.setVisible(false);
        employeePickerContainer.setManaged(false);
        endTimeComboBox.setItems(timeList);


        App.getSingleton().getExecutorService().execute(() -> {
            alerts = facade.getAllAsListAlert();
            employees = facade.getAllAsListEmployee();
            Platform.runLater(() -> {
                alertTypeComboBox.setItems(FXCollections.observableArrayList(Alert.AlertType.values()));
                employeeComboBox.setItems(employees);

                var filteredAlerts = alerts.filtered(a -> true);
                activeAlertsContainer = new PFXListView<>(filteredAlerts, PFXAlertCard::new, a -> a.getUuid().toString());
                activeAlertsContainer.setStyle("-fx-spacing: 15");

                alertTypeComboBox.setOnAction(e -> {
                    filteredAlerts.setPredicate(a -> a.getAlertType() == alertTypeComboBox.getValue());

                    var mapType = alertTypeComboBox.getValue().equals(Alert.AlertType.MAP) || alertTypeComboBox.getValue().equals(Alert.AlertType.MAP_DISABLED);
                    timePickerContainer.setVisible(mapType);
                    timePickerContainer.setManaged(mapType);
                    var employeeType = alertTypeComboBox.getValue().equals(Alert.AlertType.EMPLOYEE);
                    employeePickerContainer.setVisible(employeeType);
                    employeePickerContainer.setManaged(employeeType);
                });
                removeButton.setOnAction(e -> {
//                    facade.deleteAlert();
                });
                sendButton.setOnAction(e -> {
                    if (alertTypeComboBox.getValue() == null) return;
                    Alert.AlertBuilder builder = Alert.builder().uuid(UUID.randomUUID()).alertType(alertTypeComboBox.getValue()).alertTitle(alertTitle.getText()).description(alertDescription.getText()).readStatus(Alert.ReadStatus.UNREAD).startDate(Instant.now()).endDate(Instant.now()).employeeID(App.getSingleton().getAccount().getEmployeeID());
                    var alert = switch (alertTypeComboBox.getSelectedItem()) {
                        case MAP -> {
                            String time = endTimeComboBox.getText();
                            yield endDatePicker.getValue() == null || time.isBlank() ? null : builder.endDate(convertDateTime(endDatePicker.getValue(), time)).build();
                        }
                        case EMPLOYEE -> {
                            var employee = employeeComboBox.getSelectedItem();
                            yield employee == null ? null : builder.alertType(Alert.AlertType.EMPLOYEE).employeeID(employee.getEmployeeID()).build();
                        }
                        default -> builder.build();
                    };
                    if (alert == null) {
                        // TODO: Let use know that the alert couldn't be added as required fields weren't set
                        return;
                    }
                    facade.saveAlert(alert);
                    alerts.add(alert);
                });
            });
        });
    }

    public Instant convertDateTime(LocalDate date, String time) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("h:mma");
        LocalTime tempTime = LocalTime.parse(time.toUpperCase(), inputFormatter);
        return LocalDateTime.of(date, tempTime).atZone(ZoneId.systemDefault()).toInstant();
    }
}
