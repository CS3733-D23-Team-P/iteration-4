package edu.wpi.punchy_pegasi.frontend.controllers.requests;


import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.Screen;
import edu.wpi.punchy_pegasi.frontend.components.PFXAlert;
import edu.wpi.punchy_pegasi.frontend.components.PFXCardHolder;
import edu.wpi.punchy_pegasi.frontend.components.PFXCardVertical;
import edu.wpi.punchy_pegasi.schema.Alert;
import edu.wpi.punchy_pegasi.schema.OfficeServiceRequestEntry;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import edu.wpi.punchy_pegasi.frontend.components.PFXCardVertical;
import edu.wpi.punchy_pegasi.frontend.components.PFXCardHorizontal;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class OfficeServiceRequestController extends RequestController<OfficeServiceRequestEntry> implements PropertyChangeListener {
    @FXML
    PFXCardHolder cardHolder;
    @FXML
    VBox container = new VBox();
    ArrayList<PFXCardVertical> items = new ArrayList<>();
    @FXML
    private PFXCardVertical pencils = new PFXCardVertical();
    @FXML
    private PFXCardVertical pens = new PFXCardVertical();
    @FXML
    private PFXCardVertical paper = new PFXCardVertical();
    @FXML
    private PFXCardVertical stapler = new PFXCardVertical();
    @FXML
    private BorderPane root;
    ScrollPane scrollPane;


    public static BorderPane create(String path) {
        return RequestController.create(new OfficeServiceRequestController(), path);
    }

    @FXML
    @Override
    public void init() {
        submit.setDisable(true);
        ImageView pencilPic = new ImageView("edu/wpi/punchy_pegasi/frontend/assets/officeSupplies/colored-pencils.jpg");
        pencils = new PFXCardVertical("Pencils", "wood and graphite", 50, pencilPic.getImage());

        ImageView penPic = new ImageView("edu/wpi/punchy_pegasi/frontend/assets/officeSupplies/pen.jpg");
        pens = new PFXCardVertical("Pens", "It has ink", 30, penPic.getImage());

        ImageView paperPic = new ImageView("edu/wpi/punchy_pegasi/frontend/assets/officeSupplies/paper.jpg");
        paper = new PFXCardVertical("Paper", "Thin and soft", 100, paperPic.getImage());

        ImageView staplerPic = new ImageView("edu/wpi/punchy_pegasi/frontend/assets/officeSupplies/stapler.jpg");
        stapler = new PFXCardVertical("Stapler", "Staples papers together", 5, staplerPic.getImage());

        submit.setDisable(true);
        this.addPropertyChangeListener(this);

        var flowPane = new FlowPane(pencils, pens, paper, stapler);
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
        requestEntry = new OfficeServiceRequestEntry(locationName.getSelectedItem().getUuid(), staffAssignment.getSelectedItem().getEmployeeID(), additionalNotes.getText(), cardHolder.getChosenItems(), 1L);
        App.getSingleton().getFacade().saveOfficeServiceRequestEntry(requestEntry);
        App.getSingleton().getFacade().saveAlert(new Alert(UUID.randomUUID(), staffAssignment.getSelectedItem().getEmployeeID(), "Service Request", "Office Service Request", Instant.now(), Alert.ReadStatus.UNREAD));
        PFXAlert pfxPopup = new PFXAlert("Your request has been submitted!", ()->App.getSingleton().navigate(Screen.HOME));
    }

    @Override
    protected void fieldChanged(String id, Object oldValue, Object newValue) {
        validateEntry();
    }

    @FXML
    public void validateEntry() {
        var validate = validateGeneric();
        submit.setDisable(validate);
    }

    @FXML
    public void clearEntry() {
        clearGeneric();
        cardHolder.clear();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().endsWith("TextChanged")) validateEntry();
    }
}
