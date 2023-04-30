package edu.wpi.punchy_pegasi.frontend.controllers.requests;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.Screen;
import edu.wpi.punchy_pegasi.frontend.components.PFXAlert;
import edu.wpi.punchy_pegasi.frontend.components.PFXCardHolder;
import edu.wpi.punchy_pegasi.frontend.components.PFXCardVertical;
import edu.wpi.punchy_pegasi.schema.Alert;
import edu.wpi.punchy_pegasi.schema.FoodServiceRequestEntry;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FoodServiceRequestController extends RequestController<FoodServiceRequestEntry> implements PropertyChangeListener {
    FoodServiceRequestEntry entry;

    TextField patientName = new TextField();
    @FXML
    PFXCardHolder cardHolder;
    @FXML
    VBox container = new VBox();
    ScrollPane scrollPane;

    @FXML
    private BorderPane root;


    public static BorderPane create(String path) {
        return RequestController.create(new FoodServiceRequestController(), path);
    }

    @FXML
    public void init() {
        PFXCardVertical card1 = new PFXCardVertical("Mac and Cheese", "Delicious mac", 20, new Image("edu/wpi/punchy_pegasi/frontend/assets/food/mac-and-cheese.jpg"));
        PFXCardVertical card2 = new PFXCardVertical("Chicken and Rice", "Artisan-crafted meal", 20, new Image("edu/wpi/punchy_pegasi/frontend/assets/food/chicken-and-rice.jpg"));
        PFXCardVertical card3 = new PFXCardVertical("Meatloaf", "A delightful dish", 20, new Image("edu/wpi/punchy_pegasi/frontend/assets/food/meatloaf.jpg"));
        PFXCardVertical card4 = new PFXCardVertical("Steak", "Pan-seared goodness", 20, new Image("edu/wpi/punchy_pegasi/frontend/assets/food/steak.jpg"));
        addTextField(patientName);
        submit.setDisable(true);
        this.addPropertyChangeListener(this);

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
        //makes sure shared fields aren't empty
        requestEntry = entry = new FoodServiceRequestEntry(
                locationName.getSelectedItem().getUuid(),
                staffAssignment.getSelectedItem().getEmployeeID(),
                additionalNotes.getText(),
                cardHolder.getChosenItems(),
                "",
                new ArrayList<>(List.of("")),
                "",
                "",
                patientName.getText(), 1L);
        App.getSingleton().getFacade().saveFoodServiceRequestEntry(requestEntry);
        App.getSingleton().getFacade().saveAlert(new edu.wpi.punchy_pegasi.schema.Alert(UUID.randomUUID(), staffAssignment.getSelectedItem().getEmployeeID(), "Service Request", "Food Service Request", Instant.now(), Alert.ReadStatus.UNREAD));
        PFXAlert pfxPopup = new PFXAlert("Your request has been submitted!", ()->App.getSingleton().navigate(Screen.HOME));
    }

    @Override
    protected void fieldChanged(String id, Object oldValue, Object newValue) {
        validateEntry();
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
        cardHolder.clear();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().endsWith("TextChanged")) validateEntry();
    }
}
