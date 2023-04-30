package edu.wpi.punchy_pegasi.frontend.controllers;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.AppSearch;
import edu.wpi.punchy_pegasi.frontend.Screen;
import edu.wpi.punchy_pegasi.frontend.components.PFXListView;
import edu.wpi.punchy_pegasi.schema.Account;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.util.Strings;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


public class HeaderController extends HBox implements PropertyChangeListener {

    @FXML
    MFXButton troll;

    @FXML
    HBox headerSearch;


    public void initialize() {
        App.getSingleton().addPropertyChangeListener(this);
        setAccount(App.getSingleton().getAccount());

        //for each page, list its functions??
        ObservableList<AppSearch.SearchableItem> list = FXCollections.observableArrayList();
        list.add(new AppSearch.SearchableItem("Pathfinding", "Pathfind between two different locations on the hospital map", () -> App.getSingleton().navigate(Screen.MAP_PAGE)));
        list.add(new AppSearch.SearchableItem("Info", "See the credits and the about page for the application", () -> App.getSingleton().navigate(Screen.ABOUT)));
        list.add(new AppSearch.SearchableItem("Editing", "Edit any table information for the app", () -> App.getSingleton().navigate(Screen.ADMIN_PAGE)));
        list.add(new AppSearch.SearchableItem("Flower Delivery", "Order flowers for delivery", () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Conference Room", "Book a conference room for a meeting", () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Office Supplies", "Order office supplies", () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Food Delivery", "Order food for delivery", () -> App.getSingleton().navigate(Screen.SERVICE_REQUEST)));
        list.add(new AppSearch.SearchableItem("Map Editing", "Edit the map and its features", () -> App.getSingleton().navigate(Screen.EDIT_MAP_PAGE)));

        var filtered = list.filtered(s -> true);

        var container = new VBox();
        container.setStyle("-fx-background-color: -pfx-secondary-transparent");
        var containerV2 = new VBox();
        var listView = new PFXListView<>(filtered, s -> {
            var item = new VBox();
            var separator = new Separator();
            var name = new Label(s.getName());
            name.setStyle("-fx-cursor: hand; -fx-fill: -pfx-accent; -fx-underline: true");
            name.setOnMouseClicked(e->s.getNavigate().run());
            var description = new Label(s.getDescription());
            item.getChildren().addAll(name, separator, description);
            return item;
        }, AppSearch.SearchableItem::getName);
        var searchField = new TextField("Testing");
        containerV2.getStyleClass().add("search-popup");
        containerV2.prefWidthProperty().bind(headerSearch.widthProperty());
        containerV2.maxWidthProperty().bind(headerSearch.widthProperty());
        containerV2.minWidthProperty().bind(headerSearch.widthProperty());
        container.getChildren().add(containerV2);
        containerV2.setTranslateX(headerSearch.getLayoutX());
        containerV2.getChildren().addAll(searchField, listView);
        headerSearch.setOnMouseClicked(e -> {
            App.getSingleton().getLayout().showOverlay(container, true);
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtered.setPredicate(s -> true);
            if (newValue.isBlank())
                return;
            var preppedWords = Arrays.stream(newValue.trim().split("\s+")).distinct().map(w -> String.join(".?",
                    w.chars().mapToObj(c -> String.valueOf((char) c))
                            .toList())
            ).map(w -> Pattern.compile(w, Pattern.CASE_INSENSITIVE)).toList();
            list.forEach(si -> si.setCurrentWeight(( // weight 5
                    preppedWords.stream().mapToInt(regex ->
                            regex.matcher(si.getName()).find() ? 1 : 0
                    ).sum()) * 5
                    + ( // weight 1
                    preppedWords.stream().mapToInt(regex ->
                            regex.matcher(si.getDescription()).find() ? 1 : 0
                    ).sum())));
            FXCollections.sort(list, Comparator.comparing(AppSearch.SearchableItem::getCurrentWeight).reversed());
            filtered.setPredicate(si -> si.getCurrentWeight() > 0);
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
