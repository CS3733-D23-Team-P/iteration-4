package edu.wpi.punchy_pegasi.frontend.map;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.animations.Bobbing;
import edu.wpi.punchy_pegasi.frontend.animations.FollowPath;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.frontend.icons.MaterialSymbols;
import edu.wpi.punchy_pegasi.frontend.icons.PFXIcon;
import edu.wpi.punchy_pegasi.schema.Edge;
import edu.wpi.punchy_pegasi.schema.Node;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.Interpolator;
import javafx.beans.binding.Bindings;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.kurobako.gesturefx.GesturePane;
import net.kurobako.gesturefx.GesturePaneOps;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.*;
import java.util.stream.Collectors;

public class HospitalMap extends StackPane implements IMap<HospitalFloor> {
    private static final Image downArrow = new Image(Objects.requireNonNull(App.class.getResourceAsStream("frontend/assets/double-chevron-down-512.png")));
    private final StackPane maps = new StackPane();
    private final GesturePane gesturePane = new GesturePane(maps);
    private final BorderPane overlay = new BorderPane();
    private final HBox overlayBottom = new HBox();
    private final HBox overlayTop = new HBox();
    private HospitalFloor currentFloor;
    private boolean animate = true;

    @AllArgsConstructor
    @Data
    private class RenderedNode {
        Circle circle;
        HospitalFloor floor;
        VBox tooltip;
        VBox label;
    }

    private record RenderedEdge(Line line, HospitalFloor floor) {
    }

    private final Map<Long, RenderedNode> nodeCircles = new HashMap<>();
    private final Map<UUID, RenderedEdge> edgeLines = new HashMap<>();
    private final MultiValuedMap<Long, UUID> nodeEdges = new ArrayListValuedHashMap<>();

    public HospitalMap() {
        VBox.setVgrow(gesturePane, Priority.ALWAYS);
        getChildren().addAll(new VBox(gesturePane), overlay);
        maps.setAlignment(Pos.TOP_LEFT);
        overlay.setPickOnBounds(false);
        overlay.setBottom(overlayBottom);
        overlayBottom.getStyleClass().add("hospital-map-overlay-bottom");
        overlay.setTop(overlayTop);
        gesturePane.getStyleClass().add("hospital-map-gesture-pane");
        gesturePane.minScaleProperty().bind(Bindings.createDoubleBinding(() -> {
            var minZoom = Math.max(gesturePane.getWidth() / 5000, gesturePane.getHeight() / 3400);
            Platform.runLater(() -> {
                if (gesturePane.getCurrentScale() < minZoom)
                    gesturePane.zoomTo(minZoom, gesturePane.targetPointAtViewportCentre());
            });
            return minZoom;
        }, gesturePane.widthProperty(), gesturePane.heightProperty()));
        gesturePane.setMaxScale(3.4);
        gesturePane.setScrollBarPolicy(GesturePane.ScrollBarPolicy.NEVER);

        var spinner = new MFXProgressSpinner(-1);
        spinner.setRadius(100);
        spinner.setStyle("-fx-arc-width: 25;-fx-arc-height: 25");
        maps.getChildren().add(new StackPane(new Rectangle(5000, 3400, Color.TRANSPARENT), spinner));

        var floorContainer = new HBox();
        Arrays.stream(HospitalFloor.values()).forEach(HospitalFloor::init);
        Arrays.stream(HospitalFloor.values()).forEach(f -> {
            f.button.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showLayer(f));
            floorContainer.getChildren().add(f.button);
            maps.getChildren().add(f.root);
        });
        floorContainer.getStyleClass().add("hospital-map-floor-container");
        var separator = new HBox();
        HBox.setHgrow(separator, Priority.ALWAYS);
        var zoomModal = new VBox();
        zoomModal.setPickOnBounds(true);

        var zoomIn = new PFXButton("", new PFXIcon(MaterialSymbols.ZOOM_IN, 30));
        var zoomOut = new PFXButton("", new PFXIcon(MaterialSymbols.ZOOM_OUT, 30));
        zoomIn.setOnAction(e -> withAnimation().zoomBy(gesturePane.getCurrentScale(), gesturePane.targetPointAtViewportCentre()));
        zoomOut.setOnAction(e -> withAnimation().zoomBy(-gesturePane.getCurrentScale() * .5, gesturePane.targetPointAtViewportCentre()));
        zoomModal.getChildren().addAll(zoomIn, zoomOut);
        zoomModal.getStyleClass().add("hospital-map-zoom-modal");

        // TODO: Figure out what point to set as target
//        gesturePane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
//            if (e.getButton().equals(MouseButton.PRIMARY))
//                if (e.getClickCount() == 2)
//                    gesturePane.zoomBy(gesturePane.getCurrentScale() * .5, new Point2D(e.getX(), e.getY()));
//        });

        overlayBottom.getChildren().addAll(floorContainer, separator, zoomModal);
        separator.setPickOnBounds(false);
        overlayBottom.setPickOnBounds(false);

        showLayer(HospitalFloor.F1);
    }

    private GesturePaneOps withAnimation() {
        return animate ? gesturePane.animate(new Duration(200)).interpolateWith(Interpolator.EASE_BOTH) : gesturePane;
    }

    @Override
    public double getZoom() {
        return gesturePane.getCurrentScale();
    }

    @Override
    public void setZoom(double zoomScale) {
        withAnimation().zoomTo(zoomScale, new Point2D(gesturePane.getCurrentX(), gesturePane.getCurrentY()));
    }

    @Override
    public void enableMove(boolean enabled) {
        gesturePane.setGestureEnabled(enabled);
    }

    @Override
    public javafx.scene.Node get() {
        return this;
    }

    @Override
    public void addLayer(javafx.scene.Node n) {
        getChildren().add(n);
    }

    @Override
    public Point2D getClickLocation(MouseEvent event) {
        var x = event.getX() / gesturePane.getCurrentScaleX() - gesturePane.getCurrentX();
        var y = event.getY() / gesturePane.getCurrentScaleY() - gesturePane.getCurrentY();
        return new Point2D(x, y);
    }

    @FXML
    @Override
    public void clearMap() {
        nodeCircles.values().forEach(n -> n.floor.root.getChildren().removeAll(n.circle, n.tooltip, n.label));
        nodeCircles.clear();
        edgeLines.values().forEach(e -> e.floor.root.getChildren().remove(e.line));
        edgeLines.clear();
        nodeEdges.clear();
        Arrays.stream(HospitalFloor.values()).forEach(HospitalFloor::clearFloor);
    }

    @FXML
    @Override
    public void initialize() {
    }

    @Override
    public void showLayer(HospitalFloor floor) {
        currentFloor = floor;
        floor.root.setVisible(true);
        floor.button.setSelected(true);
        Arrays.stream(HospitalFloor.values()).filter(f -> !Objects.equals(f.identifier, floor.identifier)).forEach(f -> {
            f.button.setSelected(false);
            f.root.setVisible(false);
        });
    }

    @Override
    public HospitalFloor getLayer() {
        return currentFloor;
    }

    @Override
    public void drawYouAreHere(Node node) {
        var floor = HospitalFloor.floorMap.get(node.getFloor());
        if (floor == null)
            return;
        var icon = new PFXIcon(MaterialSymbols.LOCATION_ON, 60);
        icon.setFill(Color.valueOf("#f40000"));
        icon.setTranslateX(node.getXcoord() - 30);
        icon.setTranslateY(node.getYcoord());
        floor.nodeCanvas.getChildren().add(icon);
    }

    @Override
    public void drawLine(List<Node> nodes) {
        if (nodes.size() < 2) return;
        var floor = HospitalFloor.floorMap.get(nodes.get(0).getFloor());
        if (floor == null || nodes.stream().map(Node::getFloor).collect(Collectors.toSet()).size() > 1)
            return;
        // create animated arrow which follows the path of the line
        var totalDistance = 0.0;
        for (int i = 0; i < nodes.size() - 1; i++)
            totalDistance += Math.sqrt(Math.pow(nodes.get(i).getXcoord() - nodes.get(i + 1).getXcoord(), 2) + Math.pow(nodes.get(i).getYcoord() - nodes.get(i + 1).getYcoord(), 2));
        var speed = 20;
        var numArrows = (int) (totalDistance / 20); // one arrow every 20 image pixels
        var totalTime = totalDistance * speed;
        var timeSpacing = totalTime / numArrows;
        var points = nodes.stream().map(n -> new Point2D(n.getXcoord(), n.getYcoord())).toList();
        for (int i = 0; i < numArrows; i++) {
            var arrow = new PFXIcon(MaterialSymbols.NEAR_ME, 20);
            arrow.setStyle(arrow.getStyle() + ";-fx-fill: -pfx-navy;");
            var animation = new FollowPath(arrow, points, -10, 10, 45, speed);
            animation.getTimeline().jumpTo(Duration.millis(i * timeSpacing));
            animation.play();
            floor.lineCanvas.getChildren().add(0, arrow);
        }
    }

    @Override
    public VBox makeTooltip(javafx.scene.Node parent, ObservableStringValue text) {
        var toolTip = new VBox();
        var textLabel = new Label();
        textLabel.textProperty().bind(text);
        // tool tip text styling
        textLabel.setTextFill(Color.valueOf("#ffffff"));
        textLabel.setTextAlignment(TextAlignment.CENTER);
        textLabel.layout();
        toolTip.setPickOnBounds(false);
        //tool tip styling
        toolTip.getChildren().add(textLabel);
        toolTip.setPadding(new Insets(5));
        toolTip.setAlignment(Pos.CENTER);
        toolTip.setStyle("-fx-background-color: #000000aa; -fx-background-radius: 5");
        toolTip.layoutXProperty().bind(parent.layoutXProperty());
        toolTip.layoutYProperty().bind(parent.layoutYProperty());
        toolTip.layout();
        toolTip.visibleProperty().bind(Bindings.createBooleanBinding(() -> toolTip.isManaged() && !text.get().isBlank(), toolTip.managedProperty(), text));
        return toolTip;
    }

    @Override
    public Optional<Circle> addNode(Node node, String color, ObservableStringValue labelText, ObservableStringValue hoverText) {
        var floor = HospitalFloor.floorMap.get(node.getFloor());
        if (floor == null || nodeCircles.containsKey(node.getNodeID())) return Optional.empty();
        var circle = new Circle(0, 0, 15);
        circle.setLayoutX(node.getXcoord());
        circle.setLayoutY(node.getYcoord());
        circle.setFill(Color.valueOf(color));
        circle.setRadius(10);
        circle.setStrokeWidth(2);
        circle.setStroke(Color.valueOf("#000000"));
        var toolTip = makeTooltip(circle, hoverText);
        var shortNameTooltip = makeTooltip(circle, labelText);
        toolTip.translateYProperty().bind(toolTip.heightProperty().multiply(-1).subtract(15));
        toolTip.translateXProperty().bind(toolTip.widthProperty().multiply(-.5));
        shortNameTooltip.translateYProperty().bind(shortNameTooltip.heightProperty().multiply(-1).subtract(15));
        shortNameTooltip.translateXProperty().bind(shortNameTooltip.widthProperty().multiply(-.5));

        circle.setOnMouseEntered(e -> {
            toolTip.setManaged(true);
            shortNameTooltip.setManaged(false);
        });
        circle.setOnMouseExited(e -> {
            toolTip.setManaged(false);
            shortNameTooltip.setManaged(true);
        });
        toolTip.setManaged(false);
        shortNameTooltip.setManaged(true);

        floor.nodeCanvas.getChildren().add(circle);
        floor.tooltipCanvas.getChildren().add(toolTip);
        floor.tooltipCanvas.getChildren().add(shortNameTooltip);
        nodeCircles.put(node.getNodeID(), new RenderedNode(circle, floor, toolTip, shortNameTooltip));
        // TODO: render un-rendered edges
//        nodeEdges.get(node.getNodeID()).forEach(e->{
//            if(!edgeLines.containsKey(e))
//                addEdge()
//        });
        return Optional.of(circle);
    }

    public void updateNode(Node node) {
        Optional.ofNullable(nodeCircles.get(node.getNodeID())).ifPresent(n -> {
            n.circle.setLayoutX(node.getXcoord());
            n.circle.setLayoutY(node.getYcoord());
            var newFloor = HospitalFloor.floorMap.get(node.getFloor());
            if (n.floor != newFloor) {
                n.floor.nodeCanvas.getChildren().remove(n.circle);
                n.floor.tooltipCanvas.getChildren().remove(n.tooltip);
                n.floor.tooltipCanvas.getChildren().remove(n.label);
                n.floor = newFloor;
                n.floor.nodeCanvas.getChildren().add(n.circle);
                n.floor.tooltipCanvas.getChildren().add(n.tooltip);
                n.floor.tooltipCanvas.getChildren().add(n.label);
            }
        });
    }

    @Override
    public void removeNode(Node node) {
        Optional.ofNullable(nodeCircles.remove(node.getNodeID())).ifPresent(n -> {
            n.floor.nodeCanvas.getChildren().remove(n.circle);
            n.floor.tooltipCanvas.getChildren().remove(n.tooltip);
            n.floor.tooltipCanvas.getChildren().remove(n.label);
            // TODO: For ui performance if edges must be removed, we can remove the preemptively here
        });
    }

    @Override
    public Optional<Line> addEdge(Edge edge) {
        var startNode = nodeCircles.get(edge.getStartNode());
        var endNode = nodeCircles.get(edge.getEndNode());
        if (startNode == null || endNode == null || !Objects.equals(startNode.floor, endNode.floor) || edgeLines.containsKey(edge.getUuid()))
            return Optional.empty();
        var line = new Line();
        line.startXProperty().bind(startNode.circle.layoutXProperty());
        line.startYProperty().bind(startNode.circle.layoutYProperty());
        line.endXProperty().bind(endNode.circle.layoutXProperty());
        line.endYProperty().bind(endNode.circle.layoutYProperty());
        line.setFill(Color.valueOf("#000000"));
        line.setStrokeWidth(3);
        startNode.floor.lineCanvas.getChildren().add(line);
        edgeLines.put(edge.getUuid(), new RenderedEdge(line, startNode.floor));
        nodeEdges.put(edge.getStartNode(), edge.getUuid());
        nodeEdges.put(edge.getEndNode(), edge.getUuid());
        return Optional.of(line);
    }

    @Override
    public void removeEdge(Edge edge) {
        nodeEdges.get(edge.getStartNode()).removeIf(e -> e.equals(edge.getUuid()));
        nodeEdges.get(edge.getEndNode()).removeIf(e -> e.equals(edge.getUuid()));
        Optional.ofNullable(edgeLines.remove(edge.getUuid()))
                .ifPresent(e -> e.floor.lineCanvas.getChildren().remove(e.line));
    }

    @Override
    public javafx.scene.Node drawArrow(Node node, boolean up) {
        var floor = HospitalFloor.floorMap.get(node.getFloor());
        if (floor == null) return null;
        var group = new Group();
        group.setLayoutX(node.getXcoord());
        group.setLayoutY(node.getYcoord());
        var box = new Rectangle(-12.5, -12.5, 25, 25);
        box.setFill(Color.valueOf("#1040f0"));
        var arrow = new ImageView(downArrow);
        if (up)
            arrow.setRotate(180);
        arrow.setFitWidth(15);
        arrow.setFitHeight(15);
        arrow.setLayoutX(-7.5);
        arrow.setLayoutY(-7.5);
        group.getChildren().addAll(box, arrow);
        group.setCursor(Cursor.HAND);
        floor.nodeCanvas.getChildren().add(group);
        new Bobbing(arrow).play();
        return group;
    }

    @Override
    public void focusOn(Node node) {
        var floor = HospitalFloor.floorMap.get(node.getFloor());
        if (floor == null) return;
        showLayer(floor);
        withAnimation().centreOn(new Point2D(node.getXcoord(), node.getYcoord()));
    }

    @Override
    public void showRectangle(Rectangle rect) {
        var scaleX = gesturePane.getWidth() / rect.getWidth();
        var scaleY = gesturePane.getHeight() / rect.getHeight();
        var scale = Math.min(scaleX, scaleY);
        var pivot = new Point2D(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
        withAnimation().zoomTo(scale, pivot);
    }
}
