package edu.wpi.punchy_pegasi.frontend.map;

import com.fazecast.jSerialComm.SerialPort;
import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.backend.pathfinding.Graph;
import edu.wpi.punchy_pegasi.backend.pathfinding.PathfindingSingleton;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.frontend.icons.MaterialSymbols;
import edu.wpi.punchy_pegasi.frontend.icons.PFXIcon;
import edu.wpi.punchy_pegasi.frontend.utils.FacadeUtils;
import edu.wpi.punchy_pegasi.schema.*;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.javatuples.Pair;

import javax.xml.stream.Location;
import java.nio.charset.StandardCharsets;
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
    private VBox pathDirectionsBox;
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
    private VBox pathDirections;
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
    private ObservableMap<Node, ObservableList<Move>> nodeToMoves;
    private ObservableMap<LocationName, Node> locationToNode;
    private String selectedAlgo;
    @FXML
    private Label batteryPercent;
    private LinkedHashMap<Integer, List<DirectionalNode>> directionMap = new LinkedHashMap<>();
    private int directionFloorIndex = 0;

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
        var moves = nodeToMoves.get(node);
        if (moves.isEmpty()) return Optional.empty();
        var labelBinding = Bindings.createStringBinding(() -> moves.size() == 0 ? "" : locations.get(moves.get(0).getLocationID()).getShortName(), moves);
        var hoverBinding = Bindings.createStringBinding(() -> moves.size() == 0 ? "" : String.join("\n", moves.stream().map(e->locations.get(e.getLocationID())).filter(Objects::nonNull).map(LocationName::getLongName).toArray(String[]::new)), moves);
        return map.addNode(node, color, labelBinding, hoverBinding);
    }

    @FXML
    private void initialize() {
        map = new HospitalMap();
        root.setCenter(map.get());
        map.addLayer(container);
        container.getChildren().addAll(pathfinding, robotInfo, pathDirections);
        HBox.setHgrow(pathfinding, Priority.ALWAYS);
        invalidText.setVisible(false);
        robotInfo.setVisible(false);
        container.setPickOnBounds(false);
        pathfinding.setPickOnBounds(false);
        robotInfo.setPickOnBounds(false);
        pathDirections.setPickOnBounds(false);
        pathDirections.setVisible(false);
        pathDirections.setManaged(false);
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
        map.clearMap();
        clearDirections();
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
            clearDirections();
        });
        nodesList.forEach(n -> {
            if (!nodeToMoves.containsKey(n)) return;
            var moves = nodeToMoves.get(n).stream().map(m -> locations.get(m.getLocationID())).filter(l->l != null && isDestination.test(l)).toList();
            if (moves.isEmpty()) return;
            var pointOpt = drawNode(n, "#FFFF00");
            if (pointOpt.isEmpty()) return;
            var point = pointOpt.get();
            point.setOnMouseClicked(e -> {
                if (startSelected.get()) nodeStartCombo.selectItem(moves.get(0));
                else if (endSelected.get()) nodeEndCombo.selectItem(moves.get(0));
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
            nodeToMoves = FacadeUtils.getNodeLocations(nodes, locations, moves, adminDatePicker.valueProperty());
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

    @RequiredArgsConstructor
    private enum PathDirectionType {
        START(MaterialSymbols.STEP_OUT),
        LEFT(MaterialSymbols.KEYBOARD_ARROW_LEFT),
        RIGHT(MaterialSymbols.KEYBOARD_ARROW_RIGHT),
        UP(MaterialSymbols.KEYBOARD_DOUBLE_ARROW_UP),
        DOWN(MaterialSymbols.SOUTH_WEST),
        UPSTAIRS(MaterialSymbols.NORTH_EAST),
        DOWNSTAIRS(MaterialSymbols.SOUTH_WEST),
        END(MaterialSymbols.STEP_INTO);

        @Getter
        private final MaterialSymbols icon;
        public PFXIcon getPFXIcon(){
            return new PFXIcon(getIcon());
        }
    }

    @RequiredArgsConstructor
    @Data
    private class DirectionalNode {
        private final LocationName locationName;
        private final String floor;
        private final PathDirectionType direction;
    }

    private void pathDrawDirections() {
        pathDirections.setVisible(true);
        pathDirections.setManaged(true);
        for (var floor : directionMap.keySet()) {
            var dNodeList = directionMap.get(floor);
            if (dNodeList.isEmpty()) continue;
            VBox directionsOnFloor = new VBox(new Label("Floor " + dNodeList.get(0).getFloor()));
            for (var dNode : dNodeList) {
                var directionIcon = dNode.getDirection().getPFXIcon();
                directionIcon.setSize(15.0);
                if (Objects.requireNonNull(dNode.getLocationName().getNodeType()) == LocationName.NodeType.HALL) {
                    var directionText = new Label(getDirectionText(dNode.getDirection()) + "Hallway");
                    directionText.setWrapText(true);
                    directionsOnFloor.getChildren().add(new HBox(directionIcon, directionText));
                } else {
                    var directionText = new Label(getDirectionText(dNode.getDirection()) + dNode.getLocationName().getLongName());
                    directionText.setWrapText(true);
                    directionsOnFloor.getChildren().add(new HBox(directionIcon, directionText));
                }
            }
            pathDirectionsBox.getChildren().addAll(directionsOnFloor);
        }
    }

    private String getDirectionText(PathDirectionType direction) {
        return switch (direction) {
            case START -> "Start at ";
            case LEFT -> "Turn Left at ";
            case RIGHT -> "Turn Right at ";
            case UP -> "Go Straight at ";
            case DOWN -> "Go Back at ";
            case UPSTAIRS, DOWNSTAIRS -> "Take ";
            case END -> "Arrive at ";
        };
    }

    private void pathPutNodes(PathDirectionType direction, boolean floorWillChange, Node node, Node nextNode) {
        var floor = node.getFloor();
        var moves =  nodeToMoves.get(node);
        var currLocation = moves == null || moves.isEmpty() ? LocationName.builder().longName("Hallway").nodeType(LocationName.NodeType.HALL).build() : locations.get(moves.get(0).getLocationID());
        if (directionFloorIndex == 0) {
            directionFloorIndex++;
            directionMap.put(directionFloorIndex, new ArrayList<>());
            directionMap.get(directionFloorIndex).add(new DirectionalNode(currLocation, floor, direction));
        }
        else if (floorWillChange) {
//            switch (currLocation.getNodeType()) {
//                case ELEV -> currLocation.setLongName("Elevator to Floor " + nextNode.getFloor());
//                case STAI -> currLocation.setLongName("Stairs to Floor " + nextNode.getFloor());
//                default -> currLocation.setLongName(currLocation.getLongName() + " to Floor " + nextNode.getFloor());
//            }
            directionMap.get(directionFloorIndex).add(new DirectionalNode(currLocation, floor, direction));  // put current node as the last entry of this floor
            directionFloorIndex++;
            directionMap.put(directionFloorIndex, new ArrayList<>());
        } else {
            if (directionMap.get(directionFloorIndex).isEmpty()) {
                // if no previous location on this floor, then add the node
                directionMap.get(directionFloorIndex).add(new DirectionalNode(currLocation, floor, direction));
                return;
            }
            // when floor doesn't change, check if the direction ever changes
            var prevDirection = directionMap.get(directionFloorIndex).get(directionMap.get(directionFloorIndex).size()-1).getDirection();
            if (prevDirection.equals(direction)) {
                // if the previous location has no direction change, then don't add the node
                return;
            } else {
                // if the previous location has direction change, then add the node
                var dNode = new DirectionalNode(currLocation, floor, direction);
                directionMap.get(directionFloorIndex).add(dNode);
            }
        }
    }

    private void clearDirections() {
        pathDirectionsBox.getChildren().clear();
        directionMap.clear();
        directionFloorIndex = 0;
        pathDirections.setVisible(false);
        pathDirections.setManaged(false);
    }

    private PathDirectionType calculateDirection (Node prevNode, Node currNode, Node nextNode) {
        if (currNode.equals(nextNode))
            return PathDirectionType.END;
        else if (prevNode.equals(currNode))
            return PathDirectionType.START;

        var point1 = new Vector3D(prevNode.getXcoord().doubleValue(), prevNode.getYcoord().doubleValue(), 0.0);
        var point2 = new Vector3D(currNode.getXcoord().doubleValue(), currNode.getYcoord().doubleValue(), 0.0);
        var point3 = new Vector3D(nextNode.getXcoord().doubleValue(), nextNode.getYcoord().doubleValue(), 0.0);
        if (point1.equals(point2) || point2.equals(point3))
            return PathDirectionType.UP;
        var displacement1 = point1.subtract(point2).normalize();
        var displacement2 = point2.subtract(point3).normalize();
        var dot = displacement1.dotProduct(displacement2);

        var cross = displacement1.crossProduct(displacement2);
        if(dot > -.1 && dot < .1) {
            if (cross.getZ() > 0)
                return PathDirectionType.RIGHT;
            else
                return PathDirectionType.LEFT;
        }
        return PathDirectionType.UP;
    }

    private String pathFind(Node start, Node end) {
        var edgeList = edges.values().stream().map(v -> new Pair<>(v.getStartNode(), v.getEndNode())).toList();
        var graph = new Graph<>(nodes, edgeList);
        try {
            var path = PathfindingSingleton.SINGLETON.getAlgorithm().findPath(graph, start, end);
            map.clearMap();
            clearDirections();
            String currentFloor = path.get(0).getFloor();
            List<Node> currentPath = new ArrayList<>();
            var pathSize = path.size();
            var nodeIndex = 0;
            for (var node : path) {
                var prvNode = nodeIndex == 0 ? node : path.get(nodeIndex - 1);
                var nxtNode = nodeIndex == pathSize - 1 ? node : path.get(nodeIndex + 1);
                var thisFloor = node.getFloor();
                var nextFloor = nxtNode.getFloor();
                var willFloorChange = !thisFloor.equals(nextFloor);
                PathDirectionType nodeDirection = null;
                if (willFloorChange)
                    // TODO info user the next floor and elevator/staircase
                    if (node.getFloorNum() < nxtNode.getFloorNum()) {
                        // if the next floor is above the current floor
                        nodeDirection = PathDirectionType.UPSTAIRS;
                    } else { // if the next floor is below the current floor
                        nodeDirection = PathDirectionType.DOWNSTAIRS;
                    }
                else
                    nodeDirection = calculateDirection(prvNode, node, nxtNode);
                pathPutNodes(nodeDirection, willFloorChange, node, nxtNode);

                if (!node.getFloor().equals(currentFloor)) {
                    map.drawDirectedPath(currentPath);
                    var endNode = currentPath.get(currentPath.size() - 1);
                    map.addNode(node, "red", Bindings.createStringBinding(() -> ""), Bindings.createStringBinding(() -> "From Here"));
                    //map.drawArrow(node, endNode.getFloorNum() > node.getFloorNum()).setOnMouseClicked(e -> Platform.runLater(() -> map.showLayer(floors.get(endNode.getFloor()))));
                    map.drawArrow(endNode, endNode.getFloorNum() < node.getFloorNum()).setOnMouseClicked(e -> Platform.runLater(() -> map.showLayer(HospitalFloor.floorMap.get(node.getFloor()))));
                    currentPath = new ArrayList<>();
                    currentFloor = node.getFloor();
                }
                currentPath.add(node);
                nodeIndex++;
            }
            pathDrawDirections();
            map.drawDirectedPath(currentPath);
            map.drawYouAreHere(path.get(0));
            drawNode(path.get(path.size() - 1), "#3cb043");
            map.focusOn(path.get(0));

            for (Node node : path) {
                xCoords.add(node.getXcoord());
                yCoords.add(node.getYcoord());
            }
            robotButton.setDisable(false);
            return "Path found successfully!";
        } catch (IllegalStateException e) {
            return "Path not found";
        }
    }

    @FXML
    private void sendRobotMessage() throws InterruptedException {
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
            Thread.sleep(50);
            message = generateMessage("M", xCoords.get(i), yCoords.get(i));
            System.out.println(xCoords.get(i) + ", " + yCoords.get(i));
            comPort.writeBytes(message, message.length);
        }

        message = generateMessage("E", xCoords.get(xCoords.size() - 1), yCoords.get(yCoords.size() - 1));
        System.out.println(xCoords.get(xCoords.size() - 1) + ", " + yCoords.get(yCoords.size() - 1));
        comPort.writeBytes(message, message.length);

        // Receive Message for Battery
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);

        byte[] firstReadBuffer = new byte[1];
        byte[] secondReadBuffer = new byte[1];
        for(int i=0;i<2;i++)
        {
            if(i == 0) comPort.readBytes(firstReadBuffer, firstReadBuffer.length);
            if(i == 1) comPort.readBytes(secondReadBuffer, secondReadBuffer.length);
        }

        byte[] result = new byte[2];
        result[0] = firstReadBuffer[0];
        result[1] = secondReadBuffer[0];

        batteryPercent.setText("Battery Percentage: " + new String(result, StandardCharsets.UTF_8) + "%");

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
