package edu.wpi.punchy_pegasi.backend.pathfinding;

import edu.wpi.punchy_pegasi.schema.Node;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum PathfindingSingleton {
    SINGLETON;
    @Setter
    private IPathFind<Long, Node> algorithm;
    private AStar<Long, Node> aStar = new AStar<>(new CartesianHeuristic(), new CartesianHeuristic());
    private DFS<Long, Node> DFS = new DFS<>();
    private BFS<Long, Node> BFS = new BFS<>();
    private Dijkstra<Long, Node> dijkstra = new Dijkstra<>(new CartesianHeuristic());
}
