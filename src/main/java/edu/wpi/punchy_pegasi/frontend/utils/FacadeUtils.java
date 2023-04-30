package edu.wpi.punchy_pegasi.frontend.utils;

import edu.wpi.punchy_pegasi.schema.Edge;
import edu.wpi.punchy_pegasi.schema.LocationName;
import edu.wpi.punchy_pegasi.schema.Move;
import edu.wpi.punchy_pegasi.schema.Node;
import javafx.application.Platform;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FacadeUtils {
    public static final Predicate<LocationName> isDestination = location -> location.getNodeType() != LocationName.NodeType.HALL && location.getNodeType() != LocationName.NodeType.STAI && location.getNodeType() != LocationName.NodeType.ELEV;

    public static Stream<Move> validMoves(Map<Long, Node> nodes, Map<Long, LocationName> locations, Map<Long, Move> moves, LocalDate date) {
        return moves.values().stream()
                // filter by moves which reference valid locations and nodes
                .filter(m -> locations.containsKey(m.getLocationID()))
                .filter(m -> nodes.containsKey(m.getNodeID()))
                // first filter out all future dates
                .filter(m -> m.getDate().isBefore(date) || m.getDate().isEqual(date))
                // group by location
                .collect(Collectors.groupingBy(Move::getLocationID)).values().stream()
                // get max date for each location
                .flatMap(m -> m.stream().max(Comparator.comparing(Move::getDate)).stream());
    }

    public static Map<Node, List<Move>> calculateMoves(Map<Long, Node> nodes, Map<Long, LocationName> locations, Map<Long, Move> moves, LocalDate date) {
        return validMoves(nodes, locations, moves, date)
                // group by node id
                .collect(Collectors.groupingBy(Move::getNodeID)).entrySet().stream()
                // map to node and move
                .collect(Collectors.toMap(e -> nodes.get(e.getKey()), Map.Entry::getValue));
    }

    public static Map<LocationName, Node> calculateNodes(Map<Long, Node> nodes, Map<Long, LocationName> locations, Map<Long, Move> moves, LocalDate date) {
        return validMoves(nodes, locations, moves, date)
                // get map of location to node
                .collect(Collectors.toMap(e -> locations.get(e.getLocationID()), e -> nodes.get(e.getNodeID())));
    }

    public static ObservableMap<Node, ObservableList<Move>> getNodeLocations(ObservableMap<Long, Node> nodes, ObservableMap<Long, LocationName> locations, ObservableMap<Long, Move> moves, ObservableObjectValue<LocalDate> date) {
        ObservableMap<Node, ObservableList<Move>> nodeLocations = FXCollections.observableMap(new LinkedHashMap<>());
        Runnable update = () -> {
            nodeLocations.forEach((n, l) -> Platform.runLater(()->{
                if (nodeLocations.containsKey(n))
                    nodeLocations.get(n).clear();
            }));
            calculateMoves(nodes, locations, moves, date.get()).forEach((n, ms) -> {
                ms.forEach(e -> {
                    if (!nodeLocations.containsKey(n))
                        Platform.runLater(()->nodeLocations.put(n, FXCollections.observableArrayList()));
                    Platform.runLater(()->nodeLocations.get(n).add(e));
                });
            });
        };
        nodes.forEach((l, n) -> nodeLocations.put(n, FXCollections.observableArrayList()));
        update.run();
        // add event filters to all the maps to update the MultiValuedMap
        nodes.addListener((MapChangeListener<Long, Node>) change -> {
            if (change.wasRemoved())
                Platform.runLater(()->nodeLocations.remove(change.getValueRemoved()));
            if (change.wasAdded() && !nodeLocations.containsKey(change.getValueAdded()))
                Platform.runLater(()->nodeLocations.put(change.getValueAdded(), FXCollections.observableArrayList()));
            update.run();
        });
        locations.addListener((MapChangeListener<Long, LocationName>) change -> update.run());
        moves.addListener((MapChangeListener<Long, Move>) change -> update.run());
        date.addListener((observable, oldValue, newValue) -> update.run());

        // return the list of locations
        return nodeLocations;
    }

    public static ObservableMap<LocationName, Node> getLocationNode(ObservableMap<Long, Node> nodes, ObservableMap<Long, LocationName> locations, ObservableMap<Long, Move> moves, ObservableObjectValue<LocalDate> date) {
        ObservableMap<LocationName, Node> locationNode = FXCollections.observableMap(calculateNodes(nodes, locations, moves, date.get()));

        // add event filters to all the maps to update the MultiValuedMap
        nodes.addListener((MapChangeListener<Long, Node>) change -> Platform.runLater(()->{
            locationNode.clear();
            locationNode.putAll(calculateNodes(nodes, locations, moves, date.get()));
        }));
        locations.addListener((MapChangeListener<Long, LocationName>) change -> Platform.runLater(()->{
            locationNode.clear();
            locationNode.putAll(calculateNodes(nodes, locations, moves, date.get()));
        }));
        moves.addListener((MapChangeListener<Long, Move>) change -> Platform.runLater(()->{
            locationNode.clear();
            locationNode.putAll(calculateNodes(nodes, locations, moves, date.get()));
        }));
        date.addListener((observable, oldValue, newValue) -> Platform.runLater(()->{
            locationNode.clear();
            locationNode.putAll(calculateNodes(nodes, locations, moves, date.get()));
        }));
        return locationNode;
    }

    private static Stream<Move> futureMoves(Node node,  Map<Long, LocationName> locations, Map<Long, Move> moves, LocalDate date) {
        return moves.values().stream()
                .filter(m -> locations.containsKey(m.getLocationID()))
                .filter(m -> Objects.equals(m.getNodeID(), node.getNodeID()) && m.getDate().isAfter(date));
    }

    public static ObservableList<Move> getFutureMoves(Node node,  ObservableMap<Long, LocationName> locations, ObservableMap<Long, Move> moves, ObservableObjectValue<LocalDate> date) {
        ObservableList<Move> list = FXCollections.observableArrayList();
        list.addAll(futureMoves(node, locations, moves, date.get()).toList());
        locations.addListener((MapChangeListener<Long, LocationName>) change -> Platform.runLater(()->{
            list.clear();
            list.addAll(futureMoves(node, locations, moves, date.get()).toList());
        }));
        moves.addListener((MapChangeListener<Long, Move>) change -> Platform.runLater(()->{
            list.clear();
            list.addAll(futureMoves(node, locations, moves, date.get()).toList());
        }));
        date.addListener((observable, oldValue, newValue) -> Platform.runLater(()->{
            list.clear();
            list.addAll(futureMoves(node, locations, moves, date.get()).toList());
        }));
        return list;
    }
}
