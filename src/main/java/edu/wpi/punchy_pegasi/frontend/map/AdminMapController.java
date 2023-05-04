package edu.wpi.punchy_pegasi.frontend.map;

import edu.wpi.punchy_pegasi.App;
import edu.wpi.punchy_pegasi.frontend.DragController;
import edu.wpi.punchy_pegasi.frontend.components.PFXButton;
import edu.wpi.punchy_pegasi.frontend.components.PFXListView;
import edu.wpi.punchy_pegasi.frontend.icons.MaterialSymbols;
import edu.wpi.punchy_pegasi.frontend.icons.PFXIcon;
import edu.wpi.punchy_pegasi.frontend.utils.FacadeUtils;
import edu.wpi.punchy_pegasi.generated.Facade;
import edu.wpi.punchy_pegasi.schema.*;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.controlsfx.control.PopOver;

import java.time.*;
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
    private AtomicBoolean aligning = new AtomicBoolean(false);
    @FXML
    private PFXButton alignButton;
    @FXML
    private PFXButton cancelAlignButton;
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
    private Point2D firstPoint, secondPoint;
    private Polyline alignmentLine;
    private HospitalFloor.Floors firstFloor;
    private final EventHandler<MouseEvent> alignNode = e -> {
        if (alignmentLine != null) return;
        if (!isLeftClick.test(e) || e.getClickCount() != 1 || e.getTarget().getClass() != ImageView.class) return;
        var location = map.getClickLocation(e);
        if (firstPoint == null) {
            firstPoint = location;
            firstFloor = map.getLayer();
            return;
        }
        if (map.getLayer() != firstFloor) return;
        if (secondPoint == null)
            secondPoint = location;
        var line = map.drawLine(firstFloor, Arrays.asList(firstPoint, secondPoint), Color.BLUE, 3);
        firstPoint = secondPoint = null;
        alignmentLine = (Polyline) line.get();
    };
    private ObservableMap<Node, ObservableList<Move>> nodeToLocation;

    public static Point2D getClosestPointOnLine(Point2D p1, Point2D p2, Point2D p3) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double x3 = p3.getX();
        double y3 = p3.getY();

        double slope = (y2 - y1) / (x2 - x1);
        double perpendicularSlope = -1 / slope;

        double x4 = (y3 - y1 + slope * x1 - perpendicularSlope * x3) / (slope - perpendicularSlope);
        double y4 = slope * (x4 - x1) + y1;

        return new Point2D(x4, y4);
    }

    private Optional<Circle> drawNode(Node node, String color) {
        var location = nodeToLocation.get(node);
        if (location == null) location = FXCollections.observableArrayList();
        ObservableList<Move> finalLocation = location;
        var stringBinding = Bindings.createStringBinding(() -> String.join("\n", finalLocation.stream().map(e -> locations.get(e.getLocationID())).filter(Objects::nonNull).map(LocationName::getLongName).toArray(String[]::new)) + "\nNode ID: " + node.getNodeID().toString(), location);
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
        App.getSingleton().getScene().addEventHandler(KeyEvent.KEY_PRESSED, e -> {
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
        popOver.getScene().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.Z && e.isControlDown())
                Platform.runLater(() -> {
                    if (mapEdits.size() > 0)
                        mapEdits.remove(mapEdits.size() - 1).undo();
                });
        });
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
        buildingDropdown.selectedItemProperty().addListener(
//        buildingDropdown.setOnAction(
                e -> {
                    var old = node.toBuilder().build();
                    if (Objects.equals(old.getBuilding(), buildingDropdown.getValue())) return;
                    nodePoints.get(node.getNodeID()).setFill(Color.YELLOW);
                    popOver.setOnCloseRequest(null);
                    mapEdits.add(new MapEdit(MapEdit.ActionType.EDIT_NODE, node.toBuilder().building(buildingDropdown.getValue()).build(), old));
                });

        // make move
        var locationDropdown = new MFXFilterComboBox<LocationName>();
        locationDropdown.getStyleClass().add("location-dropdown");
        locationDropdown.setFloatingText("Pick Location");
        locationDropdown.getItems().addAll(locations.values().stream().sorted(Comparator.comparing(LocationName::getLongName)).toList());
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
        date.getStyleClass().add("node-popover-date");
        var makeMove = new PFXButton("Submit");
        makeMove.getStyleClass().add("node-popover-make-move");
        makeMove.setOnAction(a -> {
            if (locationDropdown.getValue() == null || date.getValue() == null) return;
            var newID = moves.values().stream().mapToLong(Move::getUuid).max().orElse(0) + 1;
            var move = moves.values().stream().filter(m -> Objects.equals(m.getNodeID(), node.getNodeID()) && Objects.equals(m.getLocationID(), locationDropdown.getValue().getUuid())).findFirst();
            if (move.isPresent()) {
                mapEdits.add(new MapEdit(MapEdit.ActionType.EDIT_MOVE, move.get().withDate(date.getValue()), move.get().toBuilder().build()));
                return;
            }
            var newMove = new Move(newID, node.getNodeID(), locationDropdown.getValue().getUuid(), date.getValue());
            mapEdits.add(new MapEdit(MapEdit.ActionType.ADD_MOVE, newMove.toBuilder().build()));
        });
        Function<Move, javafx.scene.Node> renderMove = m -> {
            var hbox = new HBox();
            var label = new Label(locations.get(m.getLocationID()).getLongName());
            var grow = new HBox();
            HBox.setHgrow(grow, Priority.ALWAYS);
            var deleteBtn = new PFXButton("", new PFXIcon(MaterialSymbols.DELETE_FOREVER));
            hbox.setOnMouseClicked(e -> {
                locationDropdown.setValue(locations.get(m.getLocationID()));
                date.setValue(m.getDate());
            });
            hbox.getStyleClass().add("move-hbox");
            deleteBtn.getStyleClass().add("move-delete-btn");
            deleteBtn.setOnAction(event -> facade.deleteMove(m));
            hbox.getChildren().addAll(label, grow, deleteBtn);
            hbox.setAlignment(Pos.CENTER);
            return hbox;
        };
        Function<Move, String> locationKey = m -> m.getUuid().toString();
        var location = nodeToLocation.get(node);
        if (location == null) location = FXCollections.observableArrayList();
        var currentMovesList = new PFXListView<>(location, renderMove, locationKey);

        var movesList = FacadeUtils.getFutureMoves(node, locations, moves, adminDatePicker.valueProperty());
        var futureMovesList = new PFXListView<>(movesList, renderMove, locationKey);

        var disabledButton = new MFXToggleButton("Disabled");
        var disableNodeDatepicker = new MFXDatePicker();
        disableNodeDatepicker.setValue(disableNodeDatepicker.getCurrentDate());
        disableNodeDatepicker.setText("Disabled Until");
        var alert = Bindings.createObjectBinding(() -> App.getSingleton().getFacade().getAllAsListAlert().stream().filter(a -> a.getAlertType() == Alert.AlertType.MAP_DISABLED && Objects.equals(a.getNodeID(), node.getNodeID())).findFirst(), App.getSingleton().getFacade().getAllAsListAlert());
        alert.addListener((o, ol, ne) -> {
            disabledButton.setSelected(alert.get().isPresent());
            alert.get().ifPresent(a -> disableNodeDatepicker.setValue(a.getEndDate().atZone(ZoneId.systemDefault()).toLocalDate()));
        });
        var setDisabledDate = new PFXButton("Set date");
        disabledButton.setSelected(alert.get().isPresent());
        disabledButton.setOnAction(e -> {
            if (disabledButton.isSelected()) {
                App.getSingleton().getFacade().saveAlert(Alert.builder()
                        .uuid(UUID.randomUUID())
                        .nodeID(node.getNodeID())
                        .readStatus(Alert.ReadStatus.UNREAD)
                        .alertType(Alert.AlertType.MAP_DISABLED)
                        .employeeID(-1L)
                        .alertTitle(nodeToLocation(node).get().getLongName())
                        .description("This location will be inaccessible until " + disableNodeDatepicker.getValue().toString())
                        .startDate(Instant.now().minus(Duration.ofMinutes(1)))
                        .endDate(LocalDateTime.of(disableNodeDatepicker.getValue(), LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().minus(Duration.ofSeconds(1)))
                        .build());
            } else if (alert.get().isPresent()) {
                App.getSingleton().getFacade().deleteAlert(alert.get().get());
            }
        });
        setDisabledDate.setOnAction(e -> {
            if (alert.get().isPresent())
                App.getSingleton().getFacade().updateAlert(alert.get().get().toBuilder().endDate(LocalDateTime.of(disableNodeDatepicker.getValue(), LocalTime.MAX).atZone(ZoneId.systemDefault()).withNano(0).toInstant()).build(), new Alert.Field[]{Alert.Field.END_DATE});
        });

        var delete = new PFXButton("Delete Node");
        delete.getStyleClass().add("node-popover-delete");
        delete.setOnAction(a -> {
            var nodePoint = nodePoints.get(node.getNodeID());
            if (nodePoint == null) return;
            nodePoint.setVisible(false);
            nodePoint.setManaged(false);
            edgeLines.get(node.getNodeID()).forEach(edge -> {
                if (edges.containsKey(edge))
                    mapEdits.add(new MapEdit(MapEdit.ActionType.REMOVE_EDGE, edges.get(edge).toBuilder().build()));
            });
//            nodes = nodes.entrySet().stream().filter(e -> !e.getKey().equals(node.getNodeID())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            mapEdits.add(new MapEdit(MapEdit.ActionType.REMOVE_NODE, node.toBuilder().build()));
        });

        nodes.addListener((MapChangeListener<? super Long, ? super Node>) e -> {
            if (e.wasAdded()) {
                var newNode = e.getValueAdded();
                if (Objects.equals(newNode.getNodeID(), node.getNodeID()))
                    Platform.runLater(() -> buildingDropdown.setValue(newNode.getBuilding()));
            } else if (e.wasRemoved()) {
                Platform.runLater(popOver::hide);
            }
        });
        var currentMove = FXCollections.observableArrayList(
                new Label("Current Move"),
                currentMovesList,
                new Separator());
        var futureMove = FXCollections.observableArrayList(
                new Label("Future Moves"),
                futureMovesList,
                new Separator());
        ObservableList<Move> finalLocation = location;
        currentMove.forEach(e->{
            var bool = Bindings.createBooleanBinding(()-> finalLocation.size() > 0, finalLocation);
            e.visibleProperty().bind(bool);
            e.managedProperty().bind(bool);
        });
        futureMove.forEach(e->{
            var bool = Bindings.createBooleanBinding(()->movesList.size() > 0, movesList);
            e.visibleProperty().bind(bool);
            e.managedProperty().bind(bool);
        });
        editNode.getChildren().addAll(
                buildingDropdown,
                new Separator(),
                new Label("Make Move"),
                locationDropdown,
                date,
                makeMove,
                new Separator());
        editNode.getChildren().addAll(currentMove);
        editNode.getChildren().addAll(futureMove);
        editNode.getChildren().addAll(
                disabledButton,
                disableNodeDatepicker,
                setDisabledDate,
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
            if (aligning.get()) {
                if (alignmentLine == null || alignmentLine.getPoints().size() != 4) return;
                //align node to line
                var point1 = new Point2D(alignmentLine.getPoints().get(0), alignmentLine.getPoints().get(1));
                var point2 = new Point2D(alignmentLine.getPoints().get(2), alignmentLine.getPoints().get(3));
                var point3 = new Point2D(point.getLayoutX(), point.getLayoutY());
                var newPoint = getClosestPointOnLine(point1, point2, point3);
                point.setLayoutX(newPoint.getX());
                point.setLayoutY(newPoint.getY());
                var old = n.get().toBuilder().build();
                mapEdits.add(new MapEdit(MapEdit.ActionType.EDIT_NODE, n.get().toBuilder().xcoord((int) point.getLayoutX()).ycoord((int) point.getLayoutY()).build(), old));
            } else {
                if (firstNode == null || nodes.get(firstNode.getNodeID()) == null) {
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
                mapEdits.add(new MapEdit(MapEdit.ActionType.ADD_EDGE, newEdge.toBuilder().build()));
                var startPoint = nodePoints.get(firstNode.getNodeID());
                if (startPoint != null) startPoint.setStroke(Color.valueOf("#000000"));
                var endPoint = nodePoints.get(secondNode.getNodeID());
                if (endPoint != null) endPoint.setStroke(Color.valueOf("#000000"));
                firstNode = secondNode = null;
            }
        });
        DragController dragController = new DragController(point, true);
        dragController.setScaleSupplier(() -> map.getZoom());
        dragController.setFilterMouseEvents(isLeftClick);
        dragController.setOnMove(node -> {
            if (n.get().getXcoord() == (int) node.getLayoutX() && n.get().getYcoord() == (int) node.getLayoutY())
                return;
            var old = n.get().toBuilder().build();
            mapEdits.add(new MapEdit(MapEdit.ActionType.EDIT_NODE, n.get().toBuilder().xcoord((int) point.getLayoutX()).ycoord((int) point.getLayoutY()).build(), old));
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
            mapEdits.add(new MapEdit(MapEdit.ActionType.REMOVE_EDGE, edge.toBuilder().build()));
            edgeLines.get(edge.getStartNode()).removeIf(edg -> edg.equals(edge.getUuid()));
            edgeLines.get(edge.getEndNode()).removeIf(edg -> edg.equals(edge.getUuid()));
        });
        edgeLines.put(edge.getStartNode(), edge.getUuid());
        edgeLines.put(edge.getEndNode(), edge.getUuid());
        return edgeLine;
    }

    @FXML
    private void alignNodes() {
        alignButton.setDisable(true);
        if (!aligning.compareAndSet(false, true)) return;
        map.get().addEventFilter(MouseEvent.MOUSE_CLICKED, alignNode);
        cancelAlignButton.setVisible(true);
        cancelAlignButton.setManaged(true);
    }

    @FXML
    private void cancelAlignNodes() {
        map.get().removeEventFilter(MouseEvent.MOUSE_CLICKED, alignNode);
        if (alignmentLine != null)
            ((Group) alignmentLine.getParent()).getChildren().remove(alignmentLine);
        cancelAlignButton.setVisible(false);
        cancelAlignButton.setManaged(false);
        alignmentLine = null;
        aligning.set(false);
        alignButton.setDisable(false);
    }

    private void editNodes() {
        map.clearMap();
        map.get().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (!isLeftClick.test(e) || e.getTarget().getClass() != ImageView.class) return;
            if (firstNode != null) {
                var startPoint = nodePoints.get(firstNode.getNodeID());
                if (startPoint != null) startPoint.setStroke(Color.valueOf("#000000"));
                firstNode = null;
            }
        });
        map.get().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            // check for double click of the primary button
            if (aligning.get()) return;
            if (!isLeftClick.test(e) || e.getClickCount() != 2 || e.getTarget().getClass() != ImageView.class) return;
            var location = map.getClickLocation(e);
            var node = new Node(nodes.values().stream().mapToLong(Node::getNodeID).max().orElse(0) + 5, (int) location.getX(), (int) location.getY(), map.getLayer().getIdentifier(), null);
            nodes.put(node.getNodeID(), node);
            mapEdits.add(new MapEdit(MapEdit.ActionType.ADD_NODE, node.toBuilder().build()));
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
            EDIT_MOVE(o -> {
                var move = (Move) o;
                App.getSingleton().getFacade().updateMove(move, new Move.Field[]{Move.Field.DATE});
            }, o -> {
                var move = (Move) o;
                App.getSingleton().getFacade().updateMove(move, new Move.Field[]{Move.Field.DATE});
            }, o -> {
                var move = (Move) o;
                return "Changed move date to " + move.getDate();
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