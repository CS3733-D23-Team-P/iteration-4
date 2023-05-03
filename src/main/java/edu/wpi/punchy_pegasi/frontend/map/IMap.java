package edu.wpi.punchy_pegasi.frontend.map;

import edu.wpi.punchy_pegasi.schema.Edge;
import edu.wpi.punchy_pegasi.schema.Node;
import javafx.beans.value.ObservableStringValue;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Optional;

public interface IMap<Layer> {
    double getZoom();

    void setZoom(double zoomScale);

    void setZoomAndFocus(double zoomScale, Node node);

    void enableMove(boolean enabled);

    javafx.scene.Node get();

    void addLayer(javafx.scene.Node n);

    Point2D getClickLocation(MouseEvent event);

    void clearMap();

    void initialize();

    void showLayer(Layer layer);

    Layer getLayer();

    Optional<javafx.scene.Node> drawYouAreHere(Node node);

    Optional<javafx.scene.Node> drawLine(Layer layer, List<Point2D> points, Color color, double stroke);
    void drawDirectedPath(List<Node> nodes);

    VBox makeTooltip(javafx.scene.Node parent, ObservableStringValue text);

    Optional<Circle> addNode(Node node, String color, ObservableStringValue labelText, ObservableStringValue hoverText);

    void updateNode(Node node);

    void removeNode(Node node);

    Optional<Line> addEdge(Edge edge);

    void removeEdge(Edge edge);

    javafx.scene.Node drawArrow(Node node, boolean up);

    void focusOn(Node node);

    void showRectangle(Rectangle rect);
    void setDefaultOverlaysVisible(boolean isVisible);
    void setAnimate(boolean animate);
}
