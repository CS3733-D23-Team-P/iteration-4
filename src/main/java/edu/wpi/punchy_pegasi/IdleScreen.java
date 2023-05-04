package edu.wpi.punchy_pegasi;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdleScreen extends VBox {
    private final double idleTimeSeconds;
    private boolean isIdle = false;
    private boolean enabled = false;

    public IdleScreen(double idleTimeSeconds) {
        this.idleTimeSeconds = idleTimeSeconds;

        Label lab = new Label("Press Anywhere to Continue");
        setStyle("-fx-background-color: -pfx-background");
        lab.setStyle("-fx-font-size: 24; -fx-text-fill: -pfx-text");
        setAlignment(Pos.CENTER);
        var image = new VBox();
        image.getStyleClass().add("splash-logo");
        image.setPrefHeight(200);
        var imageCont = new VBox(image);
        imageCont.setPadding(new Insets(0, 20, 0, 20));
        getChildren().addAll(imageCont, lab);
    }
}
