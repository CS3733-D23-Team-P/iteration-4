package edu.wpi.punchy_pegasi.frontend.map;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.DragController;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.frontend.utils.FacadeUtils;
import edu.wpi.punchy_pegasi.generated.Facade;
import edu.wpi.punchy_pegasi.schema.Edge;
import edu.wpi.punchy_pegasi.schema.LocationName;
import edu.wpi.punchy_pegasi.schema.Move;
import edu.wpi.punchy_pegasi.schema.Node;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.controlsfx.control.PopOver;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AdminMapController {
    private final Facade facade = App.getSingleton().getFacade();
    private final AtomicBoolean commiting = new AtomicBoolean();
    private final Predicate<MouseEvent> isLeftClick = e -> e.getButton() == MouseButton.PRIMARY && !e.isControlDown() && !e.isShiftDown();
    private final Predicate<MouseEvent> isLeftClickAndShift = e -> e.getButton() == MouseButton.PRIMARY && !e.isControlDown() && e.isShiftDown();
    private final Predicate<MouseEvent> isRightClick = e -> e.getButton() == MouseButton.SECONDARY || (e.getButton() == MouseButton.PRIMARY && e.isControlDown());
    private final ObservableList<MapEdit> mapEdits = FXCollections.observableArrayList(new MapEdit(MapEdit.ActionType.ADD_NODE, null));
    private final ObservableList<javafx.scene.Node> mapEditsNodes = FXCollections.observableArrayList(new javafx.scene.Group());
    private final Map<Long, Circle> nodePoints = new HashMap<>();
    private final MultiValuedMap<Long, UUID> edgeLines = new ArrayListValuedHashMap<>();
    @FXML
    private MFXDatePicker adminDatePicker;
    @FXML
    private VBox changes;
    private IMap<HospitalFloor.Floors> map;
    @FXML
    private BorderPane root;
    @FXML
    private MFXProgressBar commitProgress;
    @FXML
    private PFXButton commitButton;
    @FXML
    private PFXButton doneEditingButton;
    @FXML
    private PFXButton editButton;
    @FXML
    private VBox editing;
    private ObservableMap<Long, Node> nodes;
    private ObservableMap<UUID, Edge> edges;
    private ObservableMap<Long, LocationName> locations;
    private ObservableMap<Long, Move> moves;
    private Node firstNode, secondNode;
    private ObservableMap<Node, ObservableList<LocationName>> nodeToLocation;

    private Optional<Circle> drawNode(Node node, String color) {
        var location = nodeToLocation.get(node);
        if (location == null) location = FXCollections.observableArrayList();
        ObservableList<LocationName> finalLocation = location;
        var stringBinding = Bindings.createStringBinding(() -> String.join("\n", finalLocation.stream().map(LocationName::getLongName).toArray(String[]::new)) + "\nNode ID: " + node.getNodeID().toString(), location);
        return map.addNode(node, color, Bindings.createStringBinding(() -> ""), stringBinding);
    }

    private Optional<LocationName> nodeToLocation(Node node) {
        if (node == null) return Optional.empty();
        var moves = this.moves.values().stream().filter(m -> m.getNodeID().equals(node.getNodeID())).toList();
        if (moves.isEmpty()) return Optional.empty();
        return Optional.ofNullable(locations.get(moves.get(0).getLocationID()));
    }

    private void load(Runnable callback) {
        App.getSingleton().getExecutorService().execute(() -> {
            nodes = facade.getAllNode();
            edges = facade.getAllEdge();
            moves = facade.getAllMove();
            locations = facade.getAllLocationName();

            nodeToLocation = FacadeUtils.getNodeLocations(nodes, locations, moves, adminDatePicker.valueProperty());
            Platform.runLater(callback);
        });
    }

    @FXML
    private void initialize() {
        App.getSingleton().getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.Z && e.isControlDown())
                Platform.runLater(() -> {
                    if (mapEdits.size() > 0)
                        mapEdits.remove(mapEdits.size() - 1).undo();
                });
        });

        map = new HospitalMap();
        root.setCenter(map.get());
        map.addLayer(editing);
        mapEdits.addListener((ListChangeListener<MapEdit>) c -> {
            Platform.runLater(() -> {
                mapEditsNodes.clear();
                mapEditsNodes.addAll(c.getList().stream().map(MapEdit::getGraphic).toList());
            });
        });
        mapEdits.remove(0);
        Bindings.bindContentBidirectional(mapEditsNodes, changes.getChildren());
        editing.setPickOnBounds(false);
        editing.getChildren().get(0).setPickOnBounds(true);
        adminDatePicker.setValue(LocalDate.now());
        load(this::editNodes);
    }

    private PopOver nodeEditMenu(Node node) {
        var popOver = new PopOver();
        popOver.getRoot().getStylesheets().add(App.getSingleton().resolveResource("frontend/css/DefaultTheme.css").get().toExternalForm());
        var editNode = new VBox();
        editNode.getStyleClass().add("node-popover");

        var buildingDropdown = new MFXFilterComboBox<String>();
        buildingDropdown.getStyleClass().add("building-dropdown");
        var buildings = nodes.values().stream().map(Node::getBuilding).filter(Objects::nonNull).distinct().toList();
        buildingDropdown.getItems().addAll(buildings);
        buildingDropdown.setFloatingText("Pick Building");
        if (node.getBuilding() == null) {
            popOver.setOnCloseRequest(e -> {
                nodePoints.get(node.getNodeID()).setFill(Color.RED);
            });
        } else
            buildingDropdown.getSelectionModel().selectItem(node.getBuilding());
        buildingDropdown.setOnAction(e -> {
            node.setBuilding(buildingDropdown.getValue());
            nodePoints.get(node.getNodeID()).setFill(Color.YELLOW);
            popOver.setOnCloseRequest(null);
//            mapEdits.stream().filter(edit -> edit.type == MapEdit.ActionType.EDIT_NODE && Objects.equals(((Node) edit.object).getNodeID(), node.getNodeID())).findFirst().ifPresent(mapEdits::remove);
            mapEdits.add(new MapEdit(MapEdit.ActionType.EDIT_NODE, node));
        });

        // make move
        var locationDropdown = new MFXFilterComboBox<LocationName>();
        locationDropdown.getStyleClass().add("location-dropdown");
        locationDropdown.setFloatingText("Pick Location");
        locationDropdown.getItems().addAll(locations.values().stream().sorted(Comparator.comparing(LocationName::getLongName)).toList());
        nodeToLocation(node).flatMap(l -> locationDropdown.getItems().stream().filter(l2 -> Objects.equals(l2.getUuid(), l.getUuid())).findFirst()).ifPresent(l -> {
            locationDropdown.getSelectionModel().selectItem(l);
            locationDropdown.setValue(l);
        });
        locationDropdown.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocationName location) {
                return location == null ? "" : location.getLongName();
            }

            @Override
            public LocationName fromString(String string) {
                return locations.values().stream().filter(l -> l.getLongName().equals(string)).findFirst().orElse(null);
            }
        });
        var date = new MFXDatePicker();
        date.setText("Pick Effective Date");
        date.setEditable(false);
        var makeMove = new PFXButton("Make Move");
        makeMove.getStyleClass().add("node-popover-make-move");
        makeMove.setOnAction(a -> {
            if (locationDropdown.getSelectedItem() == null || date.getCurrentDate() == null) return;
            var newID = moves.values().stream().mapToLong(Move::getUuid).max().orElse(0) + 1;
            var move = new Move(newID, node.getNodeID(), locationDropdown.getSelectedItem().getUuid(), date.getCurrentDate());
//            moves.put(newID, move);
            mapEdits.add(new MapEdit(MapEdit.ActionType.ADD_MOVE, move));
        });

        var location = nodeToLocation.get(node);
        if (location == null) location = FXCollections.observableArrayList();
        // current move
        ObservableList<LocationName> finalLocation = location;
        var list = new VBox();
        var stringBinding = Bindings.createStringBinding(() -> String.join("\n", finalLocation.stream().map(LocationName::getLongName).toList()), location);
        var currentMove = new Label();
        currentMove.textProperty().bind(stringBinding);

        var movesList = FacadeUtils.getFutureMoves(node, locations, moves, adminDatePicker.valueProperty());
        var futureStringBinding = Bindings.createStringBinding(() -> String.join("\n", movesList.stream().map(LocationName::getLongName).toList()), movesList);
        var futureMoves = new Label();
        futureMoves.textProperty().bind(futureStringBinding);

        var delete = new PFXButton("Delete Node");
        delete.getStyleClass().add("node-popover-delete");
        delete.setOnAction(a -> {
            var nodePoint = nodePoints.get(node.getNodeID());
            if (nodePoint == null) return;
            nodePoint.setVisible(false);
            nodePoint.setManaged(false);
            edgeLines.get(node.getNodeID()).forEach(edge -> {
                mapEdits.add(new MapEdit(MapEdit.ActionType.REMOVE_EDGE, edges.get(edge)));
            });
//            nodes = nodes.entrySet().stream().filter(e -> !e.getKey().equals(node.getNodeID())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            mapEdits.add(new MapEdit(MapEdit.ActionType.REMOVE_NODE, node));
        });
        editNode.getChildren().addAll(
                buildingDropdown,
                new Separator(),
                new Label("Change Location"),
                locationDropdown,
                date,
                makeMove,
                new Separator(),
                new Label("Current Move"),
                new HBox(currentMove),
                new Separator(),
                new Label("Future Moves"),
                new HBox(futureMoves),
                new Separator(),
                delete);
        // sort locations by long name
        popOver.setContentNode(editNode);
        popOver.setTitle("Edit Node " + node.getNodeID().toString());
        return popOver;
    }

    private Optional<Circle> addEditableNode(Long nodeID) {
        Supplier<Node> n = () -> nodes.get(nodeID);
        var pointOpt = drawNode(n.get(), "#FFFF00");
        if (pointOpt.isEmpty()) return pointOpt;
        var point = pointOpt.get();

        point.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (!isRightClick.test(e)) return;
            nodeEditMenu(n.get()).show(point);
        });

        point.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (!isLeftClickAndShift.test(e)) return;
            if (firstNode == null) {
                firstNode = n.get();
                point.setStroke(Color.valueOf("#FF00FF"));
                return;
            }
            if (n.get() == firstNode)
                return;
            if (secondNode == null)
                secondNode = n.get();
            var newEdge = new Edge(UUID.randomUUID(), firstNode.getNodeID(), secondNode.getNodeID());
            addEditableEdge(newEdge);
            mapEdits.add(new MapEdit(MapEdit.ActionType.ADD_EDGE, newEdge));
            var startPoint = nodePoints.get(firstNode.getNodeID());
            if (startPoint != null) startPoint.setStroke(Color.valueOf("#000000"));
            var endPoint = nodePoints.get(secondNode.getNodeID());
            if (endPoint != null) endPoint.setStroke(Color.valueOf("#000000"));
            firstNode = secondNode = null;
        });
        DragController dragController = new DragController(point, true);
        dragController.setScaleSupplier(() -> map.getZoom());
        dragController.setFilterMouseEvents(isLeftClick);
        dragController.setOnMove(node -> {
            if (n.get().getXcoord() == (int) node.getLayoutX() && n.get().getYcoord() == (int) node.getLayoutY())
                return;
            n.get().setXcoord((int) node.getLayoutX());
            n.get().setYcoord((int) node.getLayoutY());
//            mapEdits.stream().filter(edit -> edit.type == MapEdit.ActionType.EDIT_NODE && Objects.equals(((Node) edit.object).getNodeID(), n.get().getNodeID())).findFirst().ifPresent(mapEdits::remove);
            mapEdits.add(new MapEdit(MapEdit.ActionType.EDIT_NODE, n.get()));
        });
        dragController.setOnEnd(node -> map.enableMove(true));
        dragController.setOnStart(node -> map.enableMove(false));
        nodePoints.put(nodeID, point);
        return pointOpt;
    }

    private Optional<Line> addEditableEdge(Edge edge) {
        var edgeLine = map.addEdge(edge);
        if (edgeLine.isEmpty()) return Optional.empty();
        // add right click event to edge to select it
        edgeLine.get().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (!isRightClick.test(e)) return;
            if (edgeLine.get().getStroke() == Color.RED) return;
            edgeLine.get().setStroke(Color.RED);
            mapEdits.add(new MapEdit(MapEdit.ActionType.REMOVE_EDGE, edge));
            edgeLines.get(edge.getStartNode()).removeIf(edg -> edg.equals(edge.getUuid()));
            edgeLines.get(edge.getEndNode()).removeIf(edg -> edg.equals(edge.getUuid()));
        });
        edgeLines.put(edge.getStartNode(), edge.getUuid());
        edgeLines.put(edge.getEndNode(), edge.getUuid());
        return edgeLine;
    }

    private void editNodes() {
        map.clearMap();
        map.get().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            // check for double click of the primary button
            if (!isLeftClick.test(e) || e.getClickCount() != 2 || e.getTarget().getClass() != ImageView.class) return;
            var location = map.getClickLocation(e);
            var node = new Node(nodes.values().stream().mapToLong(Node::getNodeID).max().orElse(0) + 5, (int) location.getX(), (int) location.getY(), map.getCurrentLayer().getIdentifier(), null);
            nodes.put(node.getNodeID(), node);
            mapEdits.add(new MapEdit(MapEdit.ActionType.ADD_NODE, node));
//            var nodePoint = addEditableNode(node);
//            if (nodePoint.isEmpty()) {
//                new PFXAlert("Error, could not add node.");
//            }
//            nodeEditMenu(node).show(nodePoints.get(node.getNodeID()));
        });
        nodes.addListener((MapChangeListener<? super Long, ? super Node>) c -> {
            if (c.wasAdded() && c.wasRemoved()) {
                var node = c.getValueAdded();
                Platform.runLater(() -> {
                    map.updateNode(node);
                });
            } else if (c.wasAdded()) {
                Platform.runLater(() -> addEditableNode(c.getValueAdded().getNodeID()));
            } else if (c.wasRemoved()) {
                Platform.runLater(() -> map.removeNode(c.getValueRemoved()));
            }
        });
        edges.addListener((MapChangeListener<? super UUID, ? super Edge>) c -> {
            if (c.wasAdded()) {
                Platform.runLater(() -> addEditableEdge(c.getValueAdded()));
            }
            if (c.wasRemoved()) {
                Platform.runLater(() -> map.removeEdge(c.getValueRemoved()));
            }
        });
        nodes.values().stream().map(Node::getNodeID).forEach(this::addEditableNode);
        edges.values().forEach(this::addEditableEdge);
    }

    @FXML
    private void commitChanges() {
//        if (!commiting.compareAndSet(false, true)) return;
//        var totalEdited = mapEdits.size();
//        commitButton.setDisable(true);
//        commitProgress.setProgress(0);
//        AtomicInteger counter = new AtomicInteger(0);
//        App.getSingleton().getExecutorService().execute(() -> {
//            var mapEditsCopy = new ArrayList<>(mapEdits);
//            mapEditsCopy.forEach(e -> {
//                e.execute();
//                Platform.runLater(() -> {
//                    mapEdits.remove(e);
//                    commitProgress.setProgress((double) counter.incrementAndGet() / totalEdited);
//                });
//            });
//
//            Platform.runLater(() -> {
//                edgeLines.values().forEach(e -> {
//                    if (e.getStroke() == Color.RED) {
//                        e.setVisible(false);
//                        e.setManaged(false);
//                    }
//                });
//                mapEdits.clear();
//                commitButton.setDisable(false);
//            });
//            commiting.set(false);
//        });
    }

    @Getter
    public static class MapEdit {
        private final ActionType type;
        private final Object object;
        private final javafx.scene.Node graphic;
        private final Object previous;

        public MapEdit(ActionType type, Object object) {
            this(type, object, null);
        }

        public MapEdit(ActionType type, Object object, Object previous) {
            this.type = type;
            this.object = object;
            this.graphic = new Label(object == null ? "" : type.getName.apply(object));
            this.previous = previous;
            if (object != null)
                execute();
        }

        public void execute() {
            type.action.accept(object);
        }

        public void undo() {
            if (type.name().toLowerCase().contains("edit"))
                type.undo.accept(previous);
            else
                type.undo.accept(object);
        }

        @RequiredArgsConstructor
        private enum ActionType {
            ADD_NODE(o -> {
                var node = (Node) o;
                App.getSingleton().getFacade().saveNode(node);
            }, o -> {
                var node = (Node) o;
                App.getSingleton().getFacade().deleteNode(node);
            }, o -> "Added node " + ((Node) o).getNodeID()),
            EDIT_NODE(o -> {
                var node = (Node) o;
                App.getSingleton().getFacade().updateNode(node, new Node.Field[]{Node.Field.XCOORD, Node.Field.YCOORD, Node.Field.BUILDING});
            }, o -> {
                var node = (Node) o;
                App.getSingleton().getFacade().updateNode(node, new Node.Field[]{Node.Field.XCOORD, Node.Field.YCOORD, Node.Field.BUILDING});
            }, o -> "Edited node " + ((Node) o).getNodeID()),
            REMOVE_NODE(o -> {
                var node = (Node) o;
                App.getSingleton().getFacade().deleteNode(node);
            }, o -> {
                var node = (Node) o;
                App.getSingleton().getFacade().saveNode(node);
            }, o -> "Removed node " + ((Node) o).getNodeID()),
            ADD_EDGE(o -> {
                var edge = (Edge) o;
                App.getSingleton().getFacade().saveEdge(edge);
            }, o -> {
                var edge = (Edge) o;
                App.getSingleton().getFacade().deleteEdge(edge);
            }, o -> "Added edge"),
            REMOVE_EDGE(o -> {
                var edge = (Edge) o;
                App.getSingleton().getFacade().deleteEdge(edge);
            }, o -> {
                var edge = (Edge) o;
                App.getSingleton().getFacade().saveEdge(edge);
            }, o -> "Removed edge"),
            ADD_MOVE(o -> {
                var move = (Move) o;
                App.getSingleton().getFacade().saveMove(move);
            }, o -> {
                var move = (Move) o;
                App.getSingleton().getFacade().deleteMove(move);
            }, o -> {
                var move = (Move) o;
                return "Node " + move.getNodeID().toString() + " to " + move.getLocationID() + " on " + move.getDate().toString();
            }),
            REMOVE_MOVE(o -> {
                var move = (Move) o;
                App.getSingleton().getFacade().deleteMove(move);
            }, o -> {
                var move = (Move) o;
                App.getSingleton().getFacade().saveMove(move);
            }, o -> "Move deleted");
            private final Consumer<Object> action;
            private final Consumer<Object> undo;
            public final Function<Object, String> getName;
        }
    }
}