package edu.wpi.punchy_pegasi.frontend.components;

import edu.wpi.punchy_pegasi.frontend.icons.MaterialSymbols;
import edu.wpi.punchy_pegasi.frontend.icons.PFXIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import static edu.wpi.punchy_pegasi.frontend.icons.MaterialSymbols.HEART_MINUS;

public class PFXPicker extends HBox {
    private final Label quantity = new Label("0");
    private final PFXIcon remove = new PFXIcon(MaterialSymbols.REMOVE);
    private final PFXIcon add = new PFXIcon(MaterialSymbols.ADD);
    private final PFXButton minus = new PFXButton("", remove);
    private final PFXButton plus = new PFXButton("", add);

    private PFXCardVertical parentCard = null;
    public PFXPicker(PFXCardVertical parentCard) {
        super();
        this.parentCard = parentCard;
        getStyleClass().add("pfx-picker-container");
        getChildren().addAll(minus, quantity, plus);
        minus.getStyleClass().add("pfx-picker-minus");
        plus.getStyleClass().add("pfx-picker-plus");
        HBox.setHgrow(minus, Priority.ALWAYS);
        HBox.setHgrow(quantity, Priority.ALWAYS);
        HBox.setHgrow(plus, Priority.ALWAYS);
        minus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int val = Integer.parseInt(quantity.getText()) - 1;
                if(val >= 0) {
                    minus.setDisable(false);
                    plus.setDisable(false);
                    quantity.setText(Integer.toString(val));
                    parentCard.addAvailable();
                }
                else
                    minus.setDisable(true);
            }
        });
        plus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int val = Integer.parseInt(quantity.getText()) + 1;
                if(val <= parentCard.getAvailable()) {
                    plus.setDisable(false);
                    minus.setDisable(false);
                    quantity.setText(Integer.toString(val));
                    parentCard.subtractAvailable();
                }
                else
                    plus.setDisable(true);
            }
        });
    }

    public int getQuantity() {
        return Integer.parseInt(quantity.getText());
    }
    public void clearQuantity() {
        quantity.setText("0");
        minus.setDisable(true);
    }
}
