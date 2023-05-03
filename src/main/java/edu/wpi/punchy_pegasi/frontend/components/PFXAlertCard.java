package edu.wpi.punchy_pegasi.frontend.components;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.icons.MaterialSymbols;
import edu.wpi.punchy_pegasi.frontend.icons.PFXIcon;
import edu.wpi.punchy_pegasi.schema.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PFXAlertCard extends HBox {
    private final Button read;
    private final Label titleLabel;
    private final Label description;
    private Long uuid;
    private VBox textContainer = new VBox();
    private PFXIcon icon;
    private MaterialSymbols active;
    private Alert alert;


    public PFXAlertCard(Alert alert) {
        super();
        this.alert = alert;
        this.active = MaterialSymbols.NOTIFICATIONS_ACTIVE;
        Alert.ReadStatus readStatus = alert.getReadStatus();
        if(readStatus == Alert.ReadStatus.READ) {
            icon = new PFXIcon(MaterialSymbols.TASK_ALT);
            getStyleClass().add("pfx-alert-card-container-read");
        } else {
            icon = new PFXIcon(active);
            getStyleClass().add("pfx-alert-card-container-unread");
        }
        titleLabel = new Label(alert.getAlertTitle());
        description = new Label(alert.getDescription());
        read = new Button("", icon);

        VBox.setVgrow(textContainer, Priority.ALWAYS);
        HBox hboxSpacer = new HBox();
        HBox.setHgrow(hboxSpacer, Priority.ALWAYS);
        HBox.setHgrow(read, Priority.ALWAYS);
        HBox.setHgrow(description, Priority.ALWAYS);
        Label alertType = new Label(alert.getAlertType().toString());
        description.setWrapText(true);
        getChildren().addAll(textContainer, hboxSpacer, read);
        textContainer.getChildren().addAll(titleLabel, alertType, description);
        alertType.getStyleClass().add("pfx-alert-card-type-text");
        titleLabel.getStyleClass().add("pfx-alert-card-title-text");


        if (alert.getAlertType() == Alert.AlertType.MAP) {
            Label endDateTimeLabel = new Label();
            endDateTimeLabel.setText("End Date Time: " + alert.getEndDate().toString());

        } else if (alert.getAlertType() == Alert.AlertType.MAP_DISABLED) {
            Label endDateTimeLabel = new Label();
            endDateTimeLabel.setText("End Date Time: " + alert.getEndDate().toString());

        } else if (alert.getAlertType() == Alert.AlertType.EMPLOYEE) {
            Label endDateTimeLabel = new Label();
            endDateTimeLabel.setText("End Date Time: " + alert.getEndDate().toString());
        }
        read.setOnAction(e -> toggleRead());
    }

    public PFXAlertCard(String title, String description, boolean isRead) {
        super();
        this.titleLabel = new Label(title);
        this.description = new Label(description);
        this.read = new Button("Mark read");

        getStyleClass().add("pfx-alert-card-container-unread");
        this.description.getStyleClass().add("pfx-alert-card-description");
        read.getStyleClass().add("pfx-alert-card-read-button");
        HBox.setHgrow(read, Priority.ALWAYS);
        HBox.setHgrow(this.description, Priority.ALWAYS);
        getChildren().addAll(this.titleLabel, this.description, read);

    }

    public String getTitle() {
        return titleLabel.getText();
    }
    public String getDescription(){ return description.getText(); }

    public void toggleRead() {
        var n = alert.toBuilder().readStatus(switch (alert.getReadStatus()){
            case READ -> Alert.ReadStatus.UNREAD;
            case UNREAD -> Alert.ReadStatus.READ;
        }).build();
        App.getSingleton().getFacade().updateAlert(n, new Alert.Field[]{Alert.Field.READ_STATUS});
        if(alert.getReadStatus() == Alert.ReadStatus.READ) {
            getStyleClass().remove("pfx-alert-card-container-unread");
            icon.setIcon(MaterialSymbols.TASK_ALT);
            getStyleClass().add("pfx-alert-card-container-read");
        }
        else{
            getStyleClass().remove("pfx-alert-card-container-read");
            icon.setIcon(active);
            getStyleClass().add("pfx-alert-card-container-unread");
        }
    }

    public Alert getAlert(){return this.alert;}
}
