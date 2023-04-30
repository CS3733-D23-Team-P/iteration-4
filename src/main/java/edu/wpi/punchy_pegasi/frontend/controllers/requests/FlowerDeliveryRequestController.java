package edu.wpi.punchy_pegasi.frontend.controllers.requests;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.Screen;
import edu.wpi.punchy_pegasi.frontend.components.*;
import edu.wpi.punchy_pegasi.schema.Alert;
import edu.wpi.punchy_pegasi.schema.Account;
import edu.wpi.punchy_pegasi.schema.FlowerDeliveryRequestEntry;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Instant;
import java.util.UUID;

public class FlowerDeliveryRequestController extends RequestController<FlowerDeliveryRequestEntry> {
    TextField patientName = new TextField();
    boolean filtered = false;
    @FXML
    private BorderPane root;
    private VBox container;
    ScrollPane scrollPane;
    public static BorderPane create(String path) {
        return RequestController.create(new FlowerDeliveryRequestController(), path);
    }

    @FXML
    public void init() {
        addTextField(patientName);


        submit.setDisable(true);
        PFXCardVertical card1 = new PFXCardVertical("Daisy", "Beautiful flower", 20, new Image("edu/wpi/punchy_pegasi/frontend/assets/flower/daisy.jpg"));
        PFXCardVertical card2 = new PFXCardVertical("Lavendar", "Amazing smell!", 20, new Image("edu/wpi/punchy_pegasi/frontend/assets/flower/lavendar.jpg"));
        PFXCardVertical card3 = new PFXCardVertical("Red Rose", "Flower of love", 20, new Image("edu/wpi/punchy_pegasi/frontend/assets/flower/red-roses.jpg"));
        PFXCardVertical card4 = new PFXCardVertical("Sunflower", "Looks great!", 20, new Image("edu/wpi/punchy_pegasi/frontend/assets/flower/sunflower.jpg"));
        filter.setOnAction(e -> toggleFilter());
        var flowPane = new FlowPane(card1, card2, card3, card4);
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setStyle("-fx-border-width: 0px; -fx-background-color: -pfx-background");
        flowPane.setAlignment(Pos.CENTER_LEFT);
        scrollPane = new ScrollPane(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-border-width: 0px; -fx-background-color: -pfx-background;");

        root.setCenter(scrollPane);
    }

    @FXML
    public void submitEntry() {
        // TODO: need a way to get the employeeID of the person making the request entry
        requestEntry = new FlowerDeliveryRequestEntry(patientName.getText(), locationName.getSelectedItem().getUuid(), staffAssignment.getSelectedItem().getEmployeeID(), additionalNotes.getText(), "", "", "", 1L);
        App.getSingleton().getFacade().saveFlowerDeliveryRequestEntry(requestEntry);
        App.getSingleton().getFacade().saveAlert(new Alert(UUID.randomUUID(), staffAssignment.getSelectedItem().getEmployeeID(), "Service Request", "Flower Service Request", Instant.now(), Alert.ReadStatus.UNREAD));
        PFXAlert pfxPopup = new PFXAlert("Your request has been submitted!", ()->App.getSingleton().navigate(Screen.HOME));
    }

    @FXML
    public void validateEntry() {
        boolean validate = validateGeneric() || patientName.getText().isBlank();
        submit.setDisable(validate);
    }

    @FXML
    public void clearEntry() {
        clearGeneric();
        patientName.clear();
//        cardHolder.clear();
    }

    @FXML
    public void toggleFilter() {
        if(filtered) {
            filtered = false;
//            filteredContainer.setManaged(true);
//            filteredContainer.setVisible(true);
            scrollPane.setManaged(false);
            scrollPane.setVisible(false);
        } else {
            filtered = true;
//            filteredContainer.setManaged(false);
//            filteredContainer.setVisible(false);
            scrollPane.setManaged(true);
            scrollPane.setVisible(true);
        }
    }
}
