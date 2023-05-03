package edu.wpi.punchy_pegasi.frontend.controllers;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.AppSearch;
import edu.wpi.punchy_pegasi.frontend.Screen;
import edu.wpi.punchy_pegasi.frontend.components.PFXListView;
import edu.wpi.punchy_pegasi.frontend.icons.MaterialSymbols;
import edu.wpi.punchy_pegasi.frontend.icons.PFXIcon;
import edu.wpi.punchy_pegasi.schema.Account;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.tools.Platform;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


public class HeaderController extends HBox implements PropertyChangeListener {

//    @FXML
//    MFXButton troll;

    @FXML
    HBox headerSearch;



    public VBox resultsVBox(FilteredList<AppSearch.SearchableItem> filtered){
        if(filtered.isEmpty()){
            var emptyVBox = new VBox();
            var emptyLabel = new Label("No results");
            emptyVBox.getChildren().add(emptyLabel);
            return emptyVBox;
        }
        Map<String, VBox> items = new LinkedHashMap<>();
        for (AppSearch.SearchableItem s: filtered) {
            var name = s.getScreen().getReadable();
            VBox item = items.get(name);
            if(s.getScreen().getShield().getShieldLevel() <= App.getSingleton().getAccount().getAccountType().getShieldLevel()){
                if (item == null) {
                    item = new VBox();
                    item.getStyleClass().add("search-items");
                    item.setId(name);
                    var separator = new Separator();
                    var nameLabel = new Label(name);
                    nameLabel.getStyleClass().add("search-items-big-font");
                    nameLabel.setWrapText(true);
                    nameLabel.setOnMouseClicked(e -> s.getNavigate().run());
                    items.put(name, item);
                    var description = new Label(s.getDescription());
                    description.setWrapText(true);
                    description.getStyleClass().add("search-items-descriptions");
                    description.setOnMouseClicked(e -> s.getNavigate().run());
                    item.getChildren().addAll(nameLabel, separator, description);
                } else {
                    item.getStyleClass().add("search-items");
                    var description = new Label(s.getDescription());
                    description.setWrapText(true);
                    description.getStyleClass().add("search-items-descriptions");
                    description.setOnMouseClicked(e -> s.getNavigate().run());
                    if (!item.getChildren().contains(description)) {
                        item.getChildren().add(description);
                    }
                }
            }
        }

        return new VBox(items.values().toArray(VBox[]::new));
    }
    public void initialize() {
        App.getSingleton().addPropertyChangeListener(this);
        setAccount(App.getSingleton().getAccount());

        //for each page, list its functions??
        ObservableList<AppSearch.SearchableItem> list = FXCollections.observableArrayList();
        list.add(new AppSearch.SearchableItem("View the home page", Screen.HOME, () -> App.getSingleton().navigate(Screen.HOME)));
        list.add(new AppSearch.SearchableItem("Check your outstanding service requests", Screen.HOME, () -> App.getSingleton().navigate(Screen.HOME)));
        list.add(new AppSearch.SearchableItem("View service request pie chart", Screen.HOME, () -> App.getSingleton().navigate(Screen.HOME)));
        list.add(new AppSearch.SearchableItem("View service request table", Screen.HOME, () -> App.getSingleton().navigate(Screen.HOME)));
        list.add(new AppSearch.SearchableItem("View alerts", Screen.HOME, () -> App.getSingleton().navigate(Screen.HOME)));
        list.add(new AppSearch.SearchableItem("Mark alert as read", Screen.HOME, () -> App.getSingleton().navigate(Screen.HOME)));
        list.add(new AppSearch.SearchableItem("Mark alert as unread", Screen.HOME, () -> App.getSingleton().navigate(Screen.HOME)));
        list.add(new AppSearch.SearchableItem("Change status of service requests", Screen.HOME, () -> App.getSingleton().navigate(Screen.HOME)));
        list.add(new AppSearch.SearchableItem("Mark service request as done", Screen.HOME, () -> App.getSingleton().navigate(Screen.HOME)));
        list.add(new AppSearch.SearchableItem("Mark service request as processing", Screen.HOME, () -> App.getSingleton().navigate(Screen.HOME)));

        list.add(new AppSearch.SearchableItem("Pathfind between two locations on the hospital map", Screen.MAP_PAGE, () -> App.getSingleton().navigate(Screen.MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("View the hospital map", Screen.MAP_PAGE, () -> App.getSingleton().navigate(Screen.MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Select a Start Node", Screen.MAP_PAGE, () -> App.getSingleton().navigate(Screen.MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Select an End Node", Screen.MAP_PAGE, () -> App.getSingleton().navigate(Screen.MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Select a pathfinding algorithm", Screen.MAP_PAGE, () -> App.getSingleton().navigate(Screen.MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Select nodes graphically", Screen.MAP_PAGE, () -> App.getSingleton().navigate(Screen.MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("View the hospital map for a specific date", Screen.MAP_PAGE, () -> App.getSingleton().navigate(Screen.MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Choose date for hospital map", Screen.MAP_PAGE, () -> App.getSingleton().navigate(Screen.MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Start robot for pathfinding through hospital", Screen.MAP_PAGE, () -> App.getSingleton().navigate(Screen.MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("View robot battery", Screen.MAP_PAGE, () -> App.getSingleton().navigate(Screen.MAP_PAGE)));

        list.add(new AppSearch.SearchableItem("Create a service request", Screen.SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Submit a service request", Screen.SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Create a flower delivery service request", Screen.FLOWER_DELIVERY_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Create a conference room service request", Screen.CONFERENCE_ROOM_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Create a office supplies service request", Screen.OFFICE_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Create a food delivery service request", Screen.FOOD_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Create a furniture service request", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a staff", Screen.SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Enter a patient name", Screen.SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a location name", Screen.SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Add additional notes", Screen.SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));

        list.add(new AppSearch.SearchableItem("Order a daisy/daisies", Screen.FLOWER_DELIVERY_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order lavendar", Screen.FLOWER_DELIVERY_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order red roses", Screen.FLOWER_DELIVERY_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order sunflowers", Screen.FLOWER_DELIVERY_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order a daisy/daisies", Screen.FLOWER_DELIVERY_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a staff", Screen.FLOWER_DELIVERY_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Enter a patient name", Screen.FLOWER_DELIVERY_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a location name", Screen.FLOWER_DELIVERY_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Add additional notes", Screen.FLOWER_DELIVERY_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));

        list.add(new AppSearch.SearchableItem("Order Mac and Cheese", Screen.FOOD_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Chicken and Rice", Screen.FOOD_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Meatloaf", Screen.FOOD_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Steak", Screen.FOOD_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a staff", Screen.FOOD_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Enter a patient name", Screen.FOOD_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a location name", Screen.FOOD_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Add additional notes", Screen.FOOD_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));

        list.add(new AppSearch.SearchableItem("Order Pencils", Screen.OFFICE_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Pens", Screen.OFFICE_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Paper", Screen.OFFICE_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Staplers", Screen.OFFICE_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a staff", Screen.OFFICE_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Enter a patient name", Screen.OFFICE_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a location name", Screen.OFFICE_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Add additional notes", Screen.OFFICE_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));

        list.add(new AppSearch.SearchableItem("Order Beds", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Office Chairs", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Blankets", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Frames", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Pillows", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Order Rugs", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a staff", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Enter a patient name", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a location name", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Add additional notes", Screen.FURNITURE_DELIVERY_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));

        list.add(new AppSearch.SearchableItem("Book a Conference Room", Screen.CONFERENCE_ROOM_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a staff", Screen.CONFERENCE_ROOM_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Enter a patient name", Screen.CONFERENCE_ROOM_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Assign a location name", Screen.CONFERENCE_ROOM_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Add additional notes", Screen.CONFERENCE_ROOM_SERVICE_REQUEST, () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));

        list.add(new AppSearch.SearchableItem("Select a table type to display", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Select a table to import", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Select a table to export", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to a table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from a table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from a table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from a table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to a table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear a table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the node table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the node table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the node table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the node table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the node table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the node table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the node table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the node table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the node table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the location table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the location table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the location table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the location table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the location table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the location table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the location table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the location table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the location table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the edge table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the edge table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the edge table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the edge table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the edge table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the edge table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the edge table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the edge table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the edge table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the move table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the move table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the move table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the move table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the move table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the move table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the move table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the move table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the move table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the employee table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the employee table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the employee table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the employee table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the employee table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the employee table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the employee table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the employee table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the employee table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the Account table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the Account table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the Account table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the Account table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the Account table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the Account table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the Account table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the Account table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the Account table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the Conference Room Service Request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the Conference Room Service Request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the Conference Room Service Request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the Conference Room Service Request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the Conference Room Service Request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the Conference Room Service Request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the Conference Room Service Request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the Conference Room Service Request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the Conference Room Service Request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the office supplies service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the office supplies service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the office supplies service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the office supplies service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the office supplies service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the office supplies service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the office supplies service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the office supplies service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the office supplies service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the furniture service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the furniture service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the furniture service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the furniture service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the furniture service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the furniture service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the furniture service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the furniture service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the furniture service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the food service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the food service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the food service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the food service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the food service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the food service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the food service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the food service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the food service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Display the flower service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Import the flower service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Export the flower service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Add an entry to the flower service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove an entry from the flower service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete an entry from the flower service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit an entry from the flower service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Submit an entry to the flower service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Clear the flower service request table", Screen.ADMIN_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        list.add(new AppSearch.SearchableItem("Edit the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit moves on the map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Edit locations on the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Add nodes to the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Add edges to the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove nodes from the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Remove edges from the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("View edges on the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("View nodes on the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("View moves on the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("View location names on the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Pick building for a node on the map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("View location names on the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Make a move on the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Pick a location for a move", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Pick an effective date for a move", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("View current moves", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("View future moves", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete current moves", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete future moves", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Commit map editing changes", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete nodes from the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Delete edges from the hospital map", Screen.EDIT_MAP_PAGE, () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));

        list.add(new AppSearch.SearchableItem("View the signage page", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));
        list.add(new AppSearch.SearchableItem("Edit the signage page", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));
        list.add(new AppSearch.SearchableItem("Change the sign location", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));
        list.add(new AppSearch.SearchableItem("Change the sign name", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));
        list.add(new AppSearch.SearchableItem("Fullscreen the signage page", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));
        list.add(new AppSearch.SearchableItem("Add location names to the sign", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));
        list.add(new AppSearch.SearchableItem("Add directions to the sign", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));
        list.add(new AppSearch.SearchableItem("Remove location names to the sign", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));
        list.add(new AppSearch.SearchableItem("Remove directions to the sign", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));
        list.add(new AppSearch.SearchableItem("Delete location names to the sign", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));
        list.add(new AppSearch.SearchableItem("Delete directions to the sign", Screen.SIGNAGE, () -> App.getSingleton().navigate(Screen.SIGNAGE)));

        list.add(new AppSearch.SearchableItem("View the about page", Screen.ABOUT, () -> App.getSingleton().navigate(Screen.INFO)));
        list.add(new AppSearch.SearchableItem("Meet the team", Screen.ABOUT, () -> App.getSingleton().navigate(Screen.INFO)));
        list.add(new AppSearch.SearchableItem("Acknowledgements", Screen.ABOUT, () -> App.getSingleton().navigate(Screen.INFO)));

        list.add(new AppSearch.SearchableItem( "View the credits page", Screen.CREDITS, () -> App.getSingleton().navigate(Screen.INFO)));
        list.add(new AppSearch.SearchableItem( "View resources", Screen.CREDITS, () -> App.getSingleton().navigate(Screen.INFO)));
        list.add(new AppSearch.SearchableItem( "View hyperlinks for resources", Screen.CREDITS, () -> App.getSingleton().navigate(Screen.INFO)));

        list.add(new AppSearch.SearchableItem("View the alerts page", Screen.ADMIN_ALERT_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Create a new alert", Screen.ADMIN_ALERT_PAGE, () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));

        var filtered = list.filtered(s -> true);

        var container = new VBox();
        var resultScrollContainer = new VBox();

        var scrollPane = new MFXScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        var searchField = new TextField("");
        var searchBox = new HBox();
        searchBox.setPadding(new Insets(0, 0, 0, 18));
        searchBox.setStyle("-fx-background-radius: 40; -fx-background-color: -pfx-background; -fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 40");
        searchBox.prefHeightProperty().bind(headerSearch.heightProperty());
        searchBox.maxHeightProperty().bind(headerSearch.heightProperty());
        searchBox.minHeightProperty().bind(headerSearch.heightProperty());
        searchBox.prefWidthProperty().bind(headerSearch.widthProperty());
        searchBox.maxWidthProperty().bind(headerSearch.widthProperty());
        searchBox.minWidthProperty().bind(headerSearch.widthProperty());
        resultScrollContainer.setMaxHeight(500);
        resultScrollContainer.paddingProperty().setValue(new Insets(5,5,5,5));
        resultScrollContainer.setStyle(" -fx-background-color: -pfx-background;");
        searchField.setStyle(" -fx-background-color: -pfx-background;");
        searchBox.getStyleClass().add("search-box");
        searchField.setStyle("-fx-font-size: 18; -fx-text-fill: -pfx-text; -fx-background-color: -pfx-background;");
        searchField.setPrefHeight(33);
        searchField.setMaxHeight(33);
        searchField.setMinHeight(33);
        var icon = new PFXIcon();
        icon.setTranslateY(-1);
        icon.setTranslateX(1);
        icon.setIcon(MaterialSymbols.SEARCH);
        icon.setSize(30.0);
        icon.setStyle("-fx-fill: -pfx-text; -fx-font-size: 30;");
        searchBox.getChildren().addAll(icon, searchField);
        resultScrollContainer.getStyleClass().add("search-popup");
        resultScrollContainer.setMaxHeight(500);
        container.getChildren().addAll(searchBox, resultScrollContainer);
        resultScrollContainer.setTranslateX(headerSearch.getLayoutX());
        resultScrollContainer.getChildren().addAll(scrollPane);


        headerSearch.setOnMouseClicked(e -> {
            scrollPane.setContent(resultsVBox(filtered));
            var spacer = new HBox();
            spacer.setPrefWidth(App.getSingleton().getLayout().getLeftLayout().getWidth());
            var overlayHBox = new HBox();
            var overlayVBox = new VBox();
            overlayVBox.setStyle("-fx-background-color: -pfx-secondary-transparent");
            overlayHBox.getChildren().add(spacer);
            overlayHBox.getChildren().add(container);
            overlayHBox.setPadding(new Insets(10,0,0,10));
            overlayVBox.getChildren().add(overlayHBox);
            App.getSingleton().getLayout().showOverlay(overlayVBox, true);
            searchField.requestFocus();
            searchField.positionCaret(searchField.getText().length());
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtered.setPredicate(s -> true);
            if (newValue.isBlank()) {
                scrollPane.setContent(resultsVBox(filtered));
                return;
            }
            var preppedWords = Arrays.stream(newValue.trim().split("\s+")).distinct().map(w -> String.join(".?",
                    w.chars().mapToObj(c -> String.valueOf((char) c))
                            .toList())
            ).map(w -> Pattern.compile(w, Pattern.CASE_INSENSITIVE)).toList();
            list.forEach(si -> si.setCurrentWeight(( // weight 5
                    preppedWords.stream().mapToInt(regex ->
                            regex.matcher(si.getScreen().name()).find() ? 1 : 0
                    ).sum()) * 5
                    + ( // weight 1
                    preppedWords.stream().mapToInt(regex ->
                            regex.matcher(si.getDescription()).find() ? 1 : 0
                    ).sum())));
            FXCollections.sort(list, Comparator.comparing(AppSearch.SearchableItem::getCurrentWeight).reversed());
            filtered.setPredicate(si -> si.getCurrentWeight() > 0);
            scrollPane.setContent(resultsVBox(filtered));
        });
    }

    public HeaderController() {
        super();
        try {
            App.getSingleton().loadWithCache("frontend/components/Header.fxml", this, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAccount(Account account) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Objects.equals(evt.getPropertyName(), "account")) {
            setAccount((Account) evt.getNewValue());
        }
    }

    private List<String> performSearchQuery(String query) {

        ArrayList<String> list1 = new ArrayList<>();
        list1.add("hey");
        return list1;
    }
}
