package edu.wpi.punchy_pegasi.frontend.controllers.requests;

import edu.wpi.punchy_pegasi.frontend.FoodServiceRequestEntry;
import edu.wpi.punchy_pegasi.frontend.navigation.Navigation;
import edu.wpi.punchy_pegasi.frontend.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;

public class FoodServiceRequestController extends RequestController<FoodServiceRequestEntry> {
    @FXML
    TextField dietaryRestrictions;
    @FXML
    RadioButton hot;
    @FXML
    RadioButton warm;
    @FXML
    RadioButton cold;
    @FXML
    CheckBox utensils;
    @FXML
    CheckBox napkins;
    @FXML
    CheckBox glass;
    @FXML
    MFXComboBox<String> mealDropdown;

    public static BorderPane create() {
        return RequestController.create(new FoodServiceRequestController(), "views/FoodServiceRequest.fxml");
    }

    @FXML
    public void init() {
        ObservableList<String> mealList =
                FXCollections.observableArrayList(
                        "Mac and Cheese", "Steak", "Chicken and Rice", "Meatloaf");
        mealDropdown.setItems(mealList);
    }

    @FXML
    public void submitEntry() {
        String tempType = "";
        ArrayList<String> extras = new ArrayList<String>();
        if (hot.isSelected()) {
            tempType = "hot";
        } else if (warm.isSelected()) {
            tempType = "cold";
        } else if (cold.isSelected()) {
            tempType = "cold";
        }

        if (utensils.isSelected()) {
            extras.add("utensils");
        }
        if (napkins.isSelected()) {
            extras.add("napkins");
        }
        if (glass.isSelected()) {
            extras.add("glass");
        }
        String restrictions;
        try {
            restrictions = dietaryRestrictions.getText();
        } catch (NullPointerException e) {
            restrictions = "";
        }

        //makes sure shared fields aren't empty
        if (this.checkSumbit())
            return;
        requestEntry =
                new FoodServiceRequestEntry(
                        patientName.getText(), roomNumber.getText(), additionalNotes.getText(), mealDropdown.getSelectedItem(), tempType, extras, restrictions);
        Navigation.navigate(Screen.HOME);
    }
}