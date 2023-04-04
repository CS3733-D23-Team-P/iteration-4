package edu.wpi.punchy_pegasi.backend.pathfinding;

import org.javatuples.Pair;

import java.util.*;

public class Graph<K, T extends INode> {
    private final Map<T, Set<T>> connections;

    public Graph(Map<K, T> nodes, List<Pair<K, K>> connections) {
        this.connections = new HashMap<>();
        for (var node : nodes.values())
            this.connections.put(node, new HashSet<>());
        for (var entry : connections) {
            this.connections.get(nodes.get(entry.getValue0())).add(nodes.get(entry.getValue1()));
            this.connections.get(nodes.get(entry.getValue1())).add(nodes.get(entry.getValue0()));
        }
    }

    public Set<T> getConnections(T node) {
        return connections.get(node);
    }
}
