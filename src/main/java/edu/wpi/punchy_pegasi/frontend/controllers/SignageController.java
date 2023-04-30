package edu.wpi.punchy_pegasi.frontend.controllers;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.frontend.components.PFXListView;
import edu.wpi.punchy_pegasi.frontend.icons.MaterialSymbols;
import edu.wpi.punchy_pegasi.frontend.icons.PFXIcon;
import edu.wpi.punchy_pegasi.frontend.map.HospitalFloor;
import edu.wpi.punchy_pegasi.frontend.map.HospitalMap;
import edu.wpi.punchy_pegasi.frontend.map.IMap;
import edu.wpi.punchy_pegasi.generated.Facade;
import edu.wpi.punchy_pegasi.schema.Account;
import edu.wpi.punchy_pegasi.schema.LocationName;
import edu.wpi.punchy_pegasi.schema.Signage;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public class SignageController {
    private static final Facade facade = App.getSingleton().getFacade();
    private static final ObservableList<String> signageNames = FXCollections.observableArrayList();
    private static boolean editing = false;
    private final String lightTheme = Objects.requireNonNull(getClass().getResource("/edu/wpi/punchy_pegasi/frontend/css/SignageLight.css")).toExternalForm();
    private final String darkTheme = Objects.requireNonNull(getClass().getResource("/edu/wpi/punchy_pegasi/frontend/css/SignageDark.css")).toExternalForm();
    private final Scene myScene = App.getSingleton().getScene();
    private final PFXIcon iconUp = new PFXIcon(MaterialSymbols.ARROW_UPWARD);
    private final PFXIcon iconDown = new PFXIcon(MaterialSymbols.ARROW_DOWNWARD);
    private final PFXIcon iconLeft = new PFXIcon(MaterialSymbols.ARROW_BACK);
    private final PFXIcon iconRight = new PFXIcon(MaterialSymbols.ARROW_FORWARD);
    private final PFXIcon iconHere = new PFXIcon(MaterialSymbols.DISTANCE);
    private final PFXIcon iconTime = new PFXIcon(MaterialSymbols.SCHEDULE);
    private final HBox signageHeaderLeft = new HBox();
    private final HBox signageHeaderMid = new HBox();
    private final HBox signageHeaderRight = new HBox();
    private final Label signageDateTime = new Label();
    private final VBox editingVbox = new VBox();
    private final List<Consumer<String>> filterUpdaters = new ArrayList<>();
    private final MFXComboBox<String> signageNameCB = new MFXComboBox<>();
    private final MFXFilterComboBox<LocationName> locNameCB = new MFXFilterComboBox<>();
    private final MFXComboBox<Signage.DirectionType> directionCB = new MFXComboBox<>();
    private final PFXButton submitButton = new PFXButton("Submit");
    private String prefSignageName;
    @FXML
    private VBox headerEdit;
    @FXML
    private VBox headerNormal;
    @FXML
    private HBox viewEdit;
    @FXML
    private HBox viewNormal;
    @FXML
    private HBox signageBody;
    @FXML
    private StackPane signageBodyStackPane;
    @FXML
    private VBox signageBodyLeft;
    @FXML
    private HBox signageHeader;

    @FXML
    private void initialize() {
        var admin = App.getSingleton().getAccount().getAccountType().getShieldLevel() >= Account.AccountType.ADMIN.getShieldLevel();
        editing = admin;
        configTimer(1000);
        initIcons();
        initHeader();
        buildSignage();
        signageBody.getStyleClass().add("signage-body");
        initSignSelector();
        if (editing) {
            buildEditSignage();
        } else {
            buildSignageMap();
        }

        Platform.runLater(() -> {
            setFullScreen(false);
        });
        myScene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.F11))
                setFullScreen(true);
            else if (event.getCode().equals(KeyCode.ESCAPE))
                setFullScreen(false);
        });
    }

    private void buildSignageMap() {
        IMap<HospitalFloor.Floors> hospitalMap = new HospitalMap();
        hospitalMap.setDefaultOverlaysVisible(false);
        hospitalMap.enableMove(false);
        hospitalMap.showRectangle(new Rectangle(1000, 1000, 1000, 1000));
        signageBodyStackPane.getChildren().add(hospitalMap.get());
        signageBodyStackPane.setMaxWidth(1000);
    }

    private static void initSignSelector() {
        ObservableList<Signage> signageList = facade.getAllAsListSignage();
        signageNames.clear();
        signageNames.addAll(signageList.stream().map(Signage::getSignName).distinct().sorted().toList());
        signageList.addListener((ListChangeListener<Signage>) c -> Platform.runLater(() -> {
            while (c.next()) {
                if (c.wasAdded())
                    for (Signage signage : c.getAddedSubList())
                        if (!signageNames.contains(signage.getSignName()))
                            signageNames.add(signage.getSignName());
                if (c.wasRemoved())
                    for (Signage signage : c.getRemoved())
                        if (c.getList().stream().map(Signage::getSignName).filter(signage.getSignName()::equals).count() == 0)
                            signageNames.remove(signage.getSignName());
            }
        }));
    }

    private static void addDelButton(HBox hbox, Signage signage) {
        if (editing) {
            var deleteBtn = new PFXButton("", new PFXIcon(MaterialSymbols.DELETE_FOREVER));
            deleteBtn.getStyleClass().add("signage-delete-btn");

            deleteBtn.setOnAction(event -> {
                facade.deleteSignage(signage);
            });
            hbox.getChildren().add(deleteBtn);
            deleteBtn.visibleProperty().bind(App.getSingleton().getPrimaryStage().fullScreenProperty().not());
            deleteBtn.managedProperty().bind(App.getSingleton().getPrimaryStage().fullScreenProperty().not());
        }
    }

//    @NotNull
//    private static VBox getSignageTableView(ObservableList<Signage> rightList) {
//        var vBox = new VBox();
////        Bindings.bindContent(vBox.getChildren(), new MappedList<>(rightList, signage ->{
////            var hbox = new HBox(new Label(signage.getLongName()));
////            hbox.setId(signage.getUuid().toString());
////            addDelButton(hbox, signage);
////            return hbox;
////        }));
//        rightList.addListener((ListChangeListener<? super Signage>) c -> {
//            while (c.next()) {
//                if (c.wasAdded())
//                    for (Signage signage : c.getAddedSubList()) {
//                        var hbox = new HBox(new Label(signage.getLongName()));
//                        hbox.setId(signage.getUuid().toString());
//                        addDelButton(hbox, signage);
//                        Platform.runLater(() ->
//                                vBox.getChildren().add(hbox)
//                        );
//                    }
//                if (c.wasRemoved())
//                    for (Signage signage : c.getRemoved())
//                        Platform.runLater(() ->
//                                vBox.getChildren().removeIf(node -> node.getId().equals(signage.getUuid().toString()))
//                        );
//            }
//        });
////        init the vBox
//        for (Signage signage : rightList) {
//            var hbox = new HBox(new Label(signage.getLongName()));
//            addDelButton(hbox, signage);
//            hbox.setId(signage.getUuid().toString());
//            vBox.getChildren().add(hbox);
//        }
//        return vBox;
//    }

    private void setFullScreen(boolean setFullScreen) {
        if (setFullScreen) {
            switchTheme(true);
            App.getSingleton().getPrimaryStage().setFullScreen(true);
            App.getSingleton().getLayout().showLeftLayout(false);
            App.getSingleton().getLayout().showTopLayout(false);
        } else {
            switchTheme(false);
            App.getSingleton().getPrimaryStage().setFullScreen(false);
            App.getSingleton().getLayout().showLeftLayout(true);
            App.getSingleton().getLayout().showTopLayout(true);
        }
    }

    private void initIcons() {
        iconUp.getStyleClass().add("signage-icon");
        iconDown.getStyleClass().add("signage-icon");
        iconLeft.getStyleClass().add("signage-icon");
        iconRight.getStyleClass().add("signage-icon");
        iconHere.getStyleClass().add("signage-icon-header");
        iconTime.getStyleClass().add("signage-icon-header");
        iconTime.setOutlined(true);
    }

    private void initHeader() {
        var fullscreenButton = new PFXButton("", new PFXIcon(MaterialSymbols.OPEN_IN_FULL));
        fullscreenButton.getStyleClass().add("signage-fullscreen-button");
        fullscreenButton.setOnAction(e -> {
            setFullScreen(true);
        });
        fullscreenButton.visibleProperty().bind(App.getSingleton().getPrimaryStage().fullScreenProperty().not());
        fullscreenButton.managedProperty().bind(App.getSingleton().getPrimaryStage().fullScreenProperty().not());

        // Set up left header
        signageHeader.getChildren().add(fullscreenButton);
        signageHeaderLeft.getStyleClass().add("signage-header-left");

        // Set up right header
        signageHeaderRight.getStyleClass().add("signage-header-right");
        HBox.setHgrow(signageHeaderRight, Priority.ALWAYS);
        signageHeaderRight.getChildren().add(iconTime);
        signageDateTime.getStyleClass().add("signage-text-DateTime");
        updateTime();
        signageHeaderRight.getChildren().add(signageDateTime);
        var signageNameSelector = new MFXComboBox<>(signageNames);
        signageNameSelector.setPromptText("Signage Name");
        signageNameSelector.getStyleClass().add("signage-combo-box");
        signageNameSelector.setFloatMode(FloatMode.DISABLED);
        signageNameSelector.setOnAction(e -> {
            setSignageName(signageNameSelector.getValue());
        });
        signageHeaderMid.getChildren().add(signageNameSelector);
        signageHeaderMid.getStyleClass().add("signage-header-mid");
        signageHeaderMid.visibleProperty().bind(App.getSingleton().getPrimaryStage().fullScreenProperty().not());
        signageHeaderMid.managedProperty().bind(App.getSingleton().getPrimaryStage().fullScreenProperty().not());

        // Combine left and right header in signageHeader HBox
        signageHeader.getChildren().add(signageHeaderLeft);
        signageHeader.getChildren().add(signageHeaderMid);
        signageHeader.getChildren().add(signageHeaderRight);
        signageHeader.getStyleClass().add("signage-header");
    }

    private void updateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        signageDateTime.setText(formatter.format(date));
    }

    private void configTimer(final long interuptPeriodMill) {
        long currTime = System.currentTimeMillis();
        long startDelay = interuptPeriodMill - (currTime % interuptPeriodMill);
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    updateTime();
                });
            }
        }, startDelay, interuptPeriodMill);
    }

    private void buildEditSignage() {
        signageNameCB.setFloatingText("Signage Name");
        signageNameCB.setFloatMode(FloatMode.INLINE);
        signageNameCB.textProperty().addListener((observable, oldValue, newValue) -> {
            validateSubmit();
        });
        locNameCB.setFloatingText("Location Name");
        locNameCB.setFloatMode(FloatMode.INLINE);
        locNameCB.setOnAction(e -> {
            validateSubmit();
        });
        directionCB.setFloatingText("Direction");
        directionCB.setFloatMode(FloatMode.INLINE);
        directionCB.setOnAction(e -> {
            validateSubmit();
        });
        submitButton.setDisable(true);
        submitButton.getStyleClass().add("signage-submit-btn");
        submitButton.setOnAction(e -> {
            facade.saveSignage(new Signage(facade.getAllAsListSignage().stream().mapToLong(Signage::getUuid).max().orElse(0) + 1, signageNameCB.getText(), locNameCB.getValue().getLongName(), directionCB.getValue()));
        });

        signageNameCB.setItems(signageNames);
        signageNameCB.setEditable(true);
        signageNameCB.setText("");
        locNameCB.setItems(facade.getAllAsListLocationName());
        locNameCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocationName object) {
                return object == null ? "" : object.getLongName();
            }

            @Override
            public LocationName fromString(String string) {
                return null;
            }
        });
        directionCB.setItems(FXCollections.observableArrayList(Signage.DirectionType.values()));

        VBox editingOptVBox = new VBox(
                new Label("Signage Name: "),
                signageNameCB,
                new Label("Location Name: "),
                locNameCB,
                new Label("Direction: "),
                directionCB);

        editingOptVBox.getStyleClass().add("signage-edit-options");

        editingVbox.getChildren().add(editingOptVBox);
        editingVbox.getChildren().add(submitButton);
        editingVbox.getStyleClass().add("signage-right-edit-vbox");
        VBox blankVBoxHi = new VBox();
        VBox blankVBoxLo = new VBox();
        VBox.setVgrow(blankVBoxHi, Priority.ALWAYS);
        VBox.setVgrow(blankVBoxLo, Priority.ALWAYS);

        VBox outerVbox = new VBox(blankVBoxHi, editingVbox, blankVBoxLo);
        VBox.setVgrow(outerVbox, Priority.ALWAYS);
        outerVbox.getStyleClass().add("signage-right-edit-outer-vbox");
        signageBodyStackPane.getChildren().add(outerVbox);
        outerVbox.visibleProperty().bind(App.getSingleton().getPrimaryStage().fullScreenProperty().not());
        outerVbox.managedProperty().bind(App.getSingleton().getPrimaryStage().fullScreenProperty().not());
    }

    private void validateSubmit() {
        boolean invalid = locNameCB.getValue() == null || directionCB.getValue() == null;
        invalid |= signageNameCB.getText().isBlank();
        submitButton.setDisable(invalid);
    }

    private void setSignageName(String name) {
        filterUpdaters.forEach(updater -> updater.accept(name));
    }

    private void buildSignage() {
        for (var direction : Signage.DirectionType.values()) {
            var signageList = facade.getAllAsListSignage().filtered(s -> true);
            Consumer<String> updated = s ->
                    signageList.setPredicate(signage -> signage.getDirectionType() == direction && signage.getSignName().equals(s));
            filterUpdaters.add(updated);
            updated.accept(prefSignageName);
            var table = new PFXListView<>(signageList, s -> {
                var hbox = new HBox(new Label(s.getLongName()));
                hbox.setId(s.getUuid().toString());
                addDelButton(hbox, s);
                return hbox;
            }, s -> s.getUuid().toString()); //getSignageTableView(signageList);
            table.getStyleClass().add("signage-label");
            HBox signageHB = new HBox();
            signageHB.visibleProperty().bind(Bindings.greaterThan(Bindings.size(signageList), 0));
            signageHB.managedProperty().bind(Bindings.greaterThan(Bindings.size(signageList), 0));
            signageHB.getStyleClass().add("signage-list");
            switch (direction) {
                case UP -> {
                    signageHB.getChildren().add(iconUp);
                    signageHB.getChildren().add(table);
                }
                case DOWN -> {
                    signageHB.getChildren().add(iconDown);
                    signageHB.getChildren().add(table);
                }
                case LEFT -> {
                    signageHB.getChildren().add(iconLeft);
                    signageHB.getChildren().add(table);
                }
                case RIGHT -> {
                    signageHB.getChildren().add(iconRight);
                    signageHB.getChildren().add(table);
                }
                case HERE -> {
                    table.getStyleClass().add("signage-label-Here");
                    var label = new Label();
                    label.fontProperty().bind(Bindings.createObjectBinding(() ->
                            Font.font(App.getSingleton().getPrimaryStage().getWidth()/20), App.getSingleton().getPrimaryStage().widthProperty()));
                    signageHeaderLeft.getChildren().add(iconHere);
                    signageHeaderLeft.getChildren().add(table);
                    signageHeaderLeft.visibleProperty().bind(Bindings.greaterThan(Bindings.size(signageList), 0));
                    signageHeaderLeft.managedProperty().bind(Bindings.greaterThan(Bindings.size(signageList), 0));
                    continue;
                }
            }
            signageBodyLeft.getChildren().add(signageHB);
            signageBodyLeft.getStyleClass().add("signage-body-left");
            Separator separator = new Separator();
            separator.visibleProperty().bind(Bindings.greaterThan(Bindings.size(signageList), 0));
            separator.managedProperty().bind(Bindings.greaterThan(Bindings.size(signageList), 0));
            signageBodyLeft.getChildren().add(separator);
        }
        signageBodyLeft.getChildren().remove(signageBodyLeft.getChildren().size() - 1);  // remove the last separator
    }

    private void switchTheme(boolean setDark) {
        if (setDark) {
            myScene.getStylesheets().remove(lightTheme);
            myScene.getStylesheets().add(darkTheme);
        } else {
            myScene.getStylesheets().remove(darkTheme);
            myScene.getStylesheets().add(lightTheme);
        }
    }

}
