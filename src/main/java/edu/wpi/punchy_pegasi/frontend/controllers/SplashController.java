package edu.wpi.punchy_pegasi.frontend.controllers;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.PdbController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class SplashController {
    @FXML
    public MFXButton exit;
    @FXML
    public VBox container;
    @FXML
    private Text statusText;
    @FXML
    private MFXProgressSpinner progressBar;
    @FXML
    private MFXComboBox<PdbController.Source> databaseSourceSelection;
    @FXML
    private MFXButton submit;
    private Consumer<PdbController> onConnection;

    private PdbController pdb;

    private boolean attemptDataBaseConnection(PdbController.Source source) {
        try {
            setStatusText("");
            setLoading(true);
            setShowExit(false);
            setShowSelect(false);
            pdb = new PdbController(source);
            setLoading(false);
            setStatusText("Connected to \"" + source + "\"!");
            Thread.sleep(100); //give UI thread time to update
            return true;
        } catch (SQLException | ClassNotFoundException | PdbController.DatabaseException e) {
            setStatusText("Failed to connect to " + source + ", try selecting an alternate database:");
            setLoading(false);
            setShowExit(true);
            setShowSelect(true);
            return false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getConnection() {
        if (onConnection == null) return false;
        App.getSingleton().getExecutorService().execute(() -> {
            try {
                var genericResource = this.getClass().getResource("");
                PdbController.Source source;
                if (genericResource != null && Objects.equals(genericResource.getProtocol(), "jar"))
                    source = PdbController.Source.Wong;
                else
                    source = PdbController.Source.Blake;
                while (!attemptDataBaseConnection(source)) {
                    source = null;
                    while (source == null) {
                        CountDownLatch latch = new CountDownLatch(1);
                        submit.setOnMouseClicked(e -> {
                            submit.setOnMouseClicked(null);
                            latch.countDown();
                        });
                        latch.await();
                        source = databaseSourceSelection.getSelectedItem();
                    }
                }
                onConnection.accept(pdb);
            } catch (InterruptedException e) {
                submit.setOnMouseClicked(null);
                throw new RuntimeException(e);
            }
        });
        return true;
    }

    @FXML
    private void initialize() {
        InputStream imageResource;
        if ((imageResource = App.class.getResourceAsStream("frontend/assets/BW-logo.png")) != null) {
            var imageView = new ImageView(new Image(imageResource));
            container.getChildren().add(0, imageView);
        }
        exit.setOnMouseClicked(e -> App.getSingleton().exit());
        databaseSourceSelection.setItems(FXCollections.observableArrayList(PdbController.Source.values()));
        databaseSourceSelection.selectedItemProperty().addListener((v, o, n) -> {
            submit.setDisable(n == null);
        });
        setShowSelect(false);
        setShowExit(false);
    }

    public void setShowSelect(boolean dropDown) {
        Platform.runLater(() -> {
            databaseSourceSelection.setVisible(dropDown);
            databaseSourceSelection.setManaged(dropDown);
            submit.setVisible(dropDown);
            submit.setManaged(dropDown);
            if (dropDown) submit.setDisable(databaseSourceSelection.getSelectedItem() == null);
        });
    }

    public void setStatusText(String text) {
        Platform.runLater(() -> {
            statusText.setManaged(!text.isBlank());
            statusText.setText(text);
        });
    }

    public void setShowExit(boolean show) {
        Platform.runLater(() -> {
            exit.setVisible(show);
            exit.setManaged(show);
        });
    }

    public void setLoading(boolean isLoading) {
        Platform.runLater(() -> {
            progressBar.setVisible(isLoading);
            progressBar.setManaged(isLoading);
        });
    }

    public void setOnConnection(Consumer<PdbController> callback) {
        onConnection = callback;
    }
}
