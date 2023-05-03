package edu.wpi.punchy_pegasi.frontend.map;


import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.schema.Account;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;



public class HospitalFloor {
    @RequiredArgsConstructor
    public enum Floors {
        L2("frontend/assets/map/00_thelowerlevel2.png", "frontend/assets/mapDark/00_thelowerlevel2.png", "Lower Level 2", "L2"),
        L1("frontend/assets/map/00_thelowerlevel1.png", "frontend/assets/mapDark/00_thelowerlevel1.png", "Lower Level 1", "L1"),
        F1("frontend/assets/map/01_thefirstfloor.png", "frontend/assets/mapDark/01_thefirstfloor.png", "First Layer", "1"),
        F2("frontend/assets/map/02_thesecondfloor.png", "frontend/assets/mapDark/02_thesecondfloor.png", "Second Layer", "2"),
        F3("frontend/assets/map/03_thethirdfloor.png", "frontend/assets/mapDark/03_thethirdfloor.png", "Third Layer", "3");

        @Getter
        private final String path;
        @Getter
        private final String darkPath;
        @Getter
        private final String humanReadableName;
        @Getter
        private final String identifier;
    }

    private static final Map<String, Image> imageCache = new ConcurrentHashMap<>();
    private static final Map<String, Image> darkImageCache = new ConcurrentHashMap<>();
    public static Map<String, Floors> floorMap = Arrays.stream(Floors.values()).map(f -> Map.entry(f.getIdentifier(), f)).collect(
            ConcurrentHashMap::new,
            (m, v) -> m.put(v.getKey(), v.getValue()),
            ConcurrentHashMap::putAll
    );
    @Getter
    private final Floors floor;
    @Getter
    private final PFXButton button = new PFXButton();
    @Getter
    private final Group root = new Group();
    @Getter
    private final Group lineCanvas = new Group();
    @Getter
    private final Group nodeCanvas = new Group();
    @Getter
    private final Group tooltipCanvas = new Group();
    private final ImageView imageView = new ImageView();
    public HospitalFloor(Floors floor) {
        this.floor = floor;
        init();
    }
    public void init() {
        tooltipCanvas.setPickOnBounds(false);
        root.getChildren().addAll(imageView, lineCanvas, nodeCanvas, tooltipCanvas);
        button.setText(floor.identifier);
        button.getStyleClass().add("floor-selector");
        App.getSingleton().getExecutorService().execute(() -> loadImage(App.getSingleton().getAccount().getTheme()));
    }

    void loadImage(Account.Theme theme) {
        Image image;
        var cache = switch (theme) {
            case DARK -> darkImageCache;
            default -> imageCache;
        };
        var path = switch (theme) {
            case DARK -> floor.getDarkPath();
            default -> floor.getPath();
        };

        if (cache.containsKey(floor.getIdentifier()))
            image = cache.get(floor.getIdentifier());
        else {
            image = new Image(Objects.requireNonNull(App.class.getResourceAsStream(path)));
            cache.put(floor.getIdentifier(), image);
        }
        Platform.runLater(() -> {
            imageView.imageProperty().set(image);
            imageView.setFitHeight(image.getHeight());
            imageView.setFitWidth(image.getWidth());
        });
    }

    void clearFloor() {
        lineCanvas.getChildren().clear();
        tooltipCanvas.getChildren().clear();
        nodeCanvas.getChildren().clear();
    }

    void setTheme(Account.Theme theme) {
        loadImage(theme);
    }
}