package edu.wpi.punchy_pegasi.frontend.components;

import animatefx.animation.Shake;
import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.animations.QuickShake;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

public class PFXAlert extends VBox {
    @Getter
    private final String text;

    @Setter
    private Runnable exit;

    public PFXAlert(String text) {
        super();
        this.text = text;

        var hContainer = new HBox();
        var alert = new VBox();
        var label = new Label();
        var exit = new PFXButton("Exit");

        label.setText(text);
        exit.setOnAction(event -> {
            App.getSingleton().getLayout().hideOverlay();
            if (this.exit != null) this.exit.run();
        });

        getStyleClass().add("pfx-alert-container");
        alert.getStyleClass().add("pfx-alert");

        getChildren().add(hContainer);
        hContainer.getChildren().add(alert);
        alert.getChildren().add(label);
        alert.getChildren().add(exit);
        var shake = new QuickShake(alert);
        setOnMouseClicked(e -> {
            shake.play();
        });

        App.getSingleton().getLayout().showOverlay(this, false);
    }
    public PFXAlert(String text, Runnable exit) {
        this(text);
        setExit(exit);
    }
}