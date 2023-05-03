package edu.wpi.punchy_pegasi.backend.pathfinding;

import edu.wpi.punchy_pegasi.schema.Node;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum PathfindingSingleton {
    SINGLETON;
    @Setter
    private IPathFind<Long, TypedNode> algorithm;
    private AStar<Long, TypedNode> aStar = new AStar<>(new MapHeuristic(), new MapHeuristic());
    private AStar<Long, TypedNode> aStarAvoidStairs = new AStar<>(new MapHeuristic(Double.MAX_VALUE/256, 1000), new MapHeuristic(Double.MAX_VALUE/256, 1000));
    private DFS<Long, TypedNode> DFS = new DFS<>();
    private BFS<Long, TypedNode> BFS = new BFS<>();
    private Dijkstra<Long, TypedNode> dijkstra = new Dijkstra<Long, TypedNode>(new CartesianHeuristic());
}
