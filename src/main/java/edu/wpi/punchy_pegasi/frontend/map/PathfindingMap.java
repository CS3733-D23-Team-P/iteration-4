package edu.wpi.punchy_pegasi.frontend.map;

import com.fazecast.jSerialComm.SerialPort;
import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.pathfinding.Graph;
import edu.wpi.punchy_pegasi.backend.pathfinding.PathfindingSingleton;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.frontend.utils.FacadeUtils;
import edu.wpi.punchy_pegasi.schema.*;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.javatuples.Pair;

import javax.imageio.ImageIO;
import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static edu.wpi.punchy_pegasi.frontend.utils.FacadeUtils.isDestination;

public class PathfindingMap {
    private final AtomicBoolean startSelected = new AtomicBoolean(false);
    private final AtomicBoolean endSelected = new AtomicBoolean(false);
    private final AtomicBoolean selectingGraphically = new AtomicBoolean(false);
    private final StringConverter<LocationName> locationToString = new StringConverter<>() {
        @Override
        public String toString(LocationName locationName) {
            return locationName == null ? "" : locationName.getLongName();
        }

        @Override
        public LocationName fromString(String string) {
            return null;
        }
    };
    private final ArrayList<Integer> xCoords = new ArrayList<Integer>();
    private final ArrayList<Integer> yCoords = new ArrayList<Integer>();
    private final HBox container = new HBox();
    private final LocalDate movesDate = LocalDate.now();
    @FXML
    private MFXDatePicker adminDatePicker;
    @FXML
    private PFXButton selectGraphicallyCancel;
    @FXML
    private Label invalidText;
    @FXML
    private PFXButton selectGraphically;
    @FXML
    private MFXFilterComboBox<String> selectAlgo;
    @FXML
    private PFXButton robotButton;
    @FXML
    private BorderPane root;
    private IMap<HospitalFloor.Floors> map;
    @FXML
    private VBox pathfinding;
    @FXML
    private VBox robotInfo;
    @FXML
    private MFXFilterComboBox<LocationName> nodeEndCombo;
    @FXML
    private MFXFilterComboBox<LocationName> nodeStartCombo;
    @FXML
    private PFXButton pathfindButton;
    @FXML
    private Text pathfindStatus;
    private ObservableMap<Long, Node> nodes;
    private ObservableMap<UUID, Edge> edges;
    private ObservableMap<Long, LocationName> locations;
    private ObservableMap<Long, Move> moves;
    private ObservableList<Node> nodesList;
    private ObservableList<Edge> edgesList;
    private ObservableList<LocationName> locationsList;
    private ObservableList<Move> movesList;
    private ObservableMap<Node, ObservableList<LocationName>> nodeToLocation;
    private ObservableMap<LocationName, Node> locationToNode;
    private String selectedAlgo;

    public static byte[] generateMessage(String str, Integer startPos, Integer endPos) {
        byte[] strArray = str.getBytes();
        byte[] tempStartArray = Integer.toString(startPos).getBytes();
        byte[] tempEndArray = Integer.toString(endPos).getBytes();
        byte[] startIntArray = {(byte) '0', (byte) '0', (byte) '0', (byte) '0'};
        byte[] endIntArray = {(byte) '0', (byte) '0', (byte) '0', (byte) '0'};

        for (int i = startIntArray.length - 1, j = tempStartArray.length - 1; j >= 0; i--, j--) {
            startIntArray[i] = tempStartArray[j];
        }

        for (int i = endIntArray.length - 1, j = tempEndArray.length - 1; j >= 0; i--, j--) {
            endIntArray[i] = tempEndArray[j];
        }


        byte[] result = new byte[strArray.length + startIntArray.length + endIntArray.length + 1];

        int pos = 0;
        for (byte element : strArray) {
            result[pos] = element;
            pos++;
        }
        for (byte element : startIntArray) {
            result[pos] = element;
            pos++;
        }
        for (byte element : endIntArray) {
            result[pos] = element;
            pos++;
        }
        result[result.length - 1] = '\n';

        return result;
    }

    private Optional<Circle> drawNode(Node node, String color) {
        var location = nodeToLocation.get(node);
        if (location.isEmpty()) return Optional.empty();
        var labelBinding = Bindings.createStringBinding(() -> location.get(0).getShortName(), location);
        var hoverBinding = Bindings.createStringBinding(() -> String.join("\n", location.stream().map(LocationName::getLongName).toArray(String[]::new)), location);
        return map.addNode(node, color, labelBinding, hoverBinding);
    }

    @FXML
    private void initialize() {
        map = new HospitalMap();
        root.setCenter(map.get());
        map.addLayer(container);
        container.getChildren().addAll(pathfinding, robotInfo);
        HBox.setHgrow(pathfinding, Priority.ALWAYS);
        invalidText.setVisible(false);
        robotInfo.setVisible(false);
        container.setPickOnBounds(false);
        pathfinding.setPickOnBounds(false);
        robotInfo.setPickOnBounds(false);
        var date = adminDatePicker.getValue();
        adminDatePicker.setValue(LocalDate.now());
        // Check account status for admin features
        if (App.getSingleton().getAccount().getAccountType().getShieldLevel() >= Account.AccountType.ADMIN.getShieldLevel()) {
            robotInfo.setVisible(true);
        }

        selectAlgo.setItems(FXCollections.observableArrayList("AStar", "Depth-First Search", "Breadth-First Search", "Dijkstra"));
        selectGraphically.setDisable(true);
        pathfindStatus.managedProperty().bind(Bindings.createBooleanBinding(() -> !pathfindStatus.textProperty().get().isBlank(), pathfindStatus.textProperty()));
        pathfindStatus.visibleProperty().bind(Bindings.createBooleanBinding(() -> !pathfindStatus.textProperty().get().isBlank(), pathfindStatus.textProperty()));
        robotButton.setDisable(true);
        selectGraphicallyCancel.setVisible(false);
        selectGraphicallyCancel.setManaged(false);

        PathfindingSingleton.SINGLETON.setAlgorithm(PathfindingSingleton.SINGLETON.getAStar());
        load(() -> {
            // set zoom to average of all nodes
            var minX = nodesList.stream().mapToDouble(Node::getXcoord).min().orElse(0);
//            var averageX = nodesList.stream().mapToDouble(Node::getXcoord).sum() / nodesList.size();
            var maxX = nodesList.stream().mapToDouble(Node::getXcoord).max().orElse(0);
            var minY = nodesList.stream().mapToDouble(Node::getYcoord).min().orElse(0);
//            var averageY = nodesList.stream().mapToDouble(Node::getYcoord).sum() / nodesList.size();
            var maxY = nodesList.stream().mapToDouble(Node::getYcoord).max().orElse(0);
//            map.focusOn(new Node(0L, (int)averageX, (int)averageY, "1", ""));
//            map.setZoom(1);
            map.showRectangle(new Rectangle(minX, minY, maxX - minX, maxY - minY));

            var filteredSorted = locationsList.filtered(isDestination).sorted(Comparator.comparing(LocationName::getLongName));
            nodeStartCombo.setItems(filteredSorted);
            nodeStartCombo.setFilterFunction(s -> n -> locationToString.toString(n).toLowerCase().contains(s.toLowerCase()));
            nodeStartCombo.setConverter(locationToString);
            nodeStartCombo.pressedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
                if (newPropertyValue && !selectingGraphically.get()) {
                    startSelected.set(true);
                    endSelected.set(false);
                    Platform.runLater(() -> {
                        selectGraphically.setText("Select Start Graphically");
                        selectGraphically.setDisable(false);
                    });
                }
            });
            nodeEndCombo.setItems(filteredSorted);
            nodeEndCombo.setFilterFunction(s -> n -> locationToString.toString(n).toLowerCase().contains(s.toLowerCase()));
            nodeEndCombo.setConverter(locationToString);
            nodeEndCombo.pressedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
                if (newPropertyValue) {
                    startSelected.set(false);
                    endSelected.set(true);
                    Platform.runLater(() -> {
                        selectGraphically.setText("Select End Graphically");
                        selectGraphically.setDisable(false);
                    });
                }
            });
        });
    }

    @FXML
    private void graphicalSelect() {
        if (!selectingGraphically.compareAndSet(false, true)) return;
        nodeStartCombo.setDisable(true);
        nodeEndCombo.setDisable(true);
        pathfindButton.setDisable(true);
        selectGraphicallyCancel.setVisible(true);
        selectGraphicallyCancel.setManaged(true);
        selectGraphicallyCancel.setOnAction(e -> {
            selectGraphicallyCancel.setOnAction(null);
            nodeStartCombo.setDisable(false);
            nodeEndCombo.setDisable(false);
            pathfindButton.setDisable(false);
            selectGraphicallyCancel.setVisible(false);
            selectGraphicallyCancel.setManaged(false);
            selectingGraphically.set(false);
            map.clearMap();
        });
        nodesList.forEach(n -> {
            var location = nodeToLocation.get(n);
            if (location == null || location.isEmpty()) return;
            if (!isDestination.test(location.get(0))) return;
            var pointOpt = drawNode(n, "#FFFF00");
            if (pointOpt.isEmpty()) return;
            var point = pointOpt.get();
            point.setOnMouseClicked(e -> {
                if (startSelected.get()) nodeStartCombo.selectItem(location.get(0));
                else if (endSelected.get()) nodeEndCombo.selectItem(location.get(0));
                selectGraphicallyCancel.fire();
            });
        });
    }

    private void load(Runnable callback) {
        App.getSingleton().getExecutorService().execute(() -> {
            nodes = App.getSingleton().getFacade().getAllNode();
            edges = App.getSingleton().getFacade().getAllEdge();
            moves = App.getSingleton().getFacade().getAllMove();
            locations = App.getSingleton().getFacade().getAllLocationName();
            nodesList = App.getSingleton().getFacade().getAllAsListNode();
            edgesList = App.getSingleton().getFacade().getAllAsListEdge();
            movesList = App.getSingleton().getFacade().getAllAsListMove();
            locationsList = App.getSingleton().getFacade().getAllAsListLocationName();
            nodeToLocation = FacadeUtils.getNodeLocations(nodes, locations, moves, adminDatePicker.valueProperty());
            locationToNode = FacadeUtils.getLocationNode(nodes, locations, moves, adminDatePicker.valueProperty());
            Platform.runLater(callback);
        });
    }

    @FXML
    private void pathFindWithSelectedNodes() {
        var startLocation = nodeStartCombo.getSelectedItem();
        var endLocation = nodeEndCombo.getSelectedItem();
        if (startLocation == null || endLocation == null) return;
        var startNode = locationToNode.get(startLocation);
        var endNode = locationToNode.get(endLocation);
        if (startNode == null) {
            pathfindStatus.setText("The starting location is not currently available");
            return;
        }
        if (endNode == null) {
            pathfindStatus.setText("The ending location is not currently available");
            return;
        }
        pathfindStatus.setText(pathFind(startNode, endNode));
    }

    private String pathFind(Node start, Node end) {
        var edgeList = edges.values().stream().map(v -> new Pair<>(v.getStartNode(), v.getEndNode())).toList();
        var graph = new Graph<>(nodes, edgeList);

        try {
            var path = PathfindingSingleton.SINGLETON.getAlgorithm().findPath(graph, start, end);
            map.clearMap();
            String currentFloor = path.get(0).getFloor();
            List<Node> currentPath = new ArrayList<>();
            List<Pair<Rectangle2D, HospitalFloor.Floors>> rectangles = new ArrayList<>();
            boolean multiFloor = true;
            for (var node : path) {
                if (!node.getFloor().equals(currentFloor)) {
                    var minX = currentPath.stream().mapToDouble(Node::getXcoord).min().orElse(0);
                    var maxX = currentPath.stream().mapToDouble(Node::getXcoord).max().orElse(0);
                    var minY = currentPath.stream().mapToDouble(Node::getYcoord).min().orElse(0);
                    var maxY = currentPath.stream().mapToDouble(Node::getYcoord).max().orElse(0);


                    Rectangle2D rect = new Rectangle2D(minX - 100, minY - 100, maxX-minX + 200,maxY-minY + 200);
                    rectangles.add(new Pair<>(rect, map.getCurrentLayer()));

                    map.drawLine(currentPath);
                    var endNode = currentPath.get(currentPath.size() - 1);
                    map.addNode(node, "red", Bindings.createStringBinding(() -> ""), Bindings.createStringBinding(() -> "From Here"));
                    //map.drawArrow(node, endNode.getFloorNum() > node.getFloorNum()).setOnMouseClicked(e -> Platform.runLater(() -> map.showLayer(floors.get(endNode.getFloor()))));
                    map.drawArrow(endNode, endNode.getFloorNum() < node.getFloorNum()).setOnMouseClicked(e -> Platform.runLater(() -> map.showLayer(map.getCurrentLayer())));
                    currentPath = new ArrayList<>();
                    currentFloor = node.getFloor();
                    multiFloor = false;
                }
                currentPath.add(node);
            }

            if (multiFloor) {
                var minX = path.stream().mapToDouble(Node::getXcoord).min().orElse(0);
                var maxX = path.stream().mapToDouble(Node::getXcoord).max().orElse(0);
                var minY = path.stream().mapToDouble(Node::getYcoord).min().orElse(0);
                var maxY = path.stream().mapToDouble(Node::getYcoord).max().orElse(0);

                Rectangle2D rect = new Rectangle2D(minX - 100, minY - 100, maxX-minX + 200,maxY-minY + 200);
                rectangles.add(new Pair<>(rect, map.getCurrentLayer()));
            }

            map.drawLine(currentPath);
            map.drawYouAreHere(path.get(0));
            drawNode(path.get(path.size() - 1), "#3cb043");
            map.focusOn(path.get(0));

            for (Node node : path) {
                xCoords.add(node.getXcoord());
                yCoords.add(node.getYcoord());
            }
            robotButton.setDisable(false);

            App.getSingleton().getExecutorService().execute(()->{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    return;
                }
                Platform.runLater(() -> {
                    for (var pair : rectangles){
                        screenShot(pair.getValue0(), pair.getValue1());
                    }
                });
            });

            return "Path found successfully!";
        } catch (IllegalStateException e) {
            return "Path not found";
        }
    }

    @FXML
    private void screenShot(Rectangle2D rectangle, HospitalFloor.Floors floor) {
        try{
            SnapshotParameters params = new SnapshotParameters();
            params.setViewport(rectangle);
            javafx.scene.Node currentFloor = map.getLayerNode(floor);
            WritableImage screenShot = currentFloor.snapshot(params, null);
            File file = new File(System.getProperty("user.home") + "/Desktop/" + screenShot + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(screenShot, null), "png", file);

        } catch (Exception e){
            System.out.println(e);
        }
    }


    @FXML
    private void sendRobotMessage() {
        SerialPort comPort = null;
        SerialPort[] ports = SerialPort.getCommPorts();

        for (int i = 0; i < ports.length; i++) {
            if (ports[i].getPortDescription().equals("Pololu A-Star 32U4")) {
                comPort = ports[i];
            }
        }
        try {
            comPort.openPort();
            robotButton.setDisable(true);
            invalidText.setVisible(false);
        } catch (Exception e) {
            invalidText.setVisible(true);
            return;
        }
        byte[] message = generateMessage("S", xCoords.get(0), yCoords.get(0));
        System.out.println(xCoords.get(0) + ", " + yCoords.get(0));
        comPort.writeBytes(message, message.length);

        for (int i = 1; i < xCoords.size() - 1; i++) {
            message = generateMessage("M", xCoords.get(i), yCoords.get(i));
            System.out.println(xCoords.get(i) + ", " + yCoords.get(i));
            comPort.writeBytes(message, message.length);
        }

        message = generateMessage("E", xCoords.get(xCoords.size() - 1), yCoords.get(yCoords.size() - 1));
        System.out.println(xCoords.get(xCoords.size() - 1) + ", " + yCoords.get(yCoords.size() - 1));
        comPort.writeBytes(message, message.length);
        comPort.closePort();
    }

    @FXML
    private void setAlgo() {
        selectedAlgo = selectAlgo.getSelectedItem();
        if (selectedAlgo.equals("Depth-First Search")) {
            PathfindingSingleton.SINGLETON.setAlgorithm(PathfindingSingleton.SINGLETON.getDFS());
        } else if (selectedAlgo.equals("Breadth-First Search")) {
            PathfindingSingleton.SINGLETON.setAlgorithm(PathfindingSingleton.SINGLETON.getBFS());
        } else if (selectedAlgo.equals("Dijkstra")) {
            PathfindingSingleton.SINGLETON.setAlgorithm(PathfindingSingleton.SINGLETON.getDijkstra());
        } else {
            PathfindingSingleton.SINGLETON.setAlgorithm(PathfindingSingleton.SINGLETON.getAStar());
        }
    }
}
