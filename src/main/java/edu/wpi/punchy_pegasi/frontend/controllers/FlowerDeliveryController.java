package edu.wpi.punchy_pegasi.frontend.controllers;

import edu.wpi.punchy_pegasi.frontend.FlowerDeliveryRequestEntry;
import edu.wpi.punchy_pegasi.frontend.navigation.Navigation;
import edu.wpi.punchy_pegasi.frontend.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class FlowerDeliveryController {

    @FXML
    Button submitButton;
    @FXML
    MFXComboBox<String> flowerTypeComboBox;
    @FXML
    TextField flowerAmountField;
    @FXML
    TextField patientNameField;
    @FXML
    TextField roomNumberField;
    @FXML
    TextField additionalNotesField;
    @FXML
    RadioButton small;
    @FXML
    RadioButton medium;
    @FXML
    RadioButton large;

    FlowerDeliveryRequestEntry requestEntry;

    @FXML
    public void initialize() {

        ObservableList<String> flowerTypesList =
                FXCollections.observableArrayList("Rose", "Tulip", "Lavender");
        flowerTypeComboBox.setItems(flowerTypesList);
    }

    @FXML
    public void submitEntry() {
        String size = "";

        if (small.isSelected()) {
            size = "Small";
        } else if (medium.isSelected()) {
            size = "Medium";
        } else if (large.isSelected()) {
            size = "Large";
        }

        requestEntry = new FlowerDeliveryRequestEntry(patientNameField.getText(), additionalNotesField.getText(), size, roomNumberField.getText(), flowerAmountField.getText(), flowerTypeComboBox.getSelectedItem());

        Navigation.navigate(Screen.HOME);
    }
}
