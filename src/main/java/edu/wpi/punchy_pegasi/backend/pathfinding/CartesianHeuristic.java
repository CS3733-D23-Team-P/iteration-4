package edu.wpi.punchy_pegasi.backend.pathfinding;

import edu.wpi.punchy_pegasi.schema.Node;

import java.util.Objects;

public class CartesianHeuristic implements IHeuristic<Node> {

    @Override
    public double computeCost(Node from, Node to) {
        return Math.sqrt(Math.pow(from.getXcoord() - to.getXcoord(), 2d) + Math.pow(from.getYcoord() - to.getYcoord(), 2d)) + (!Objects.equals(from.getFloor(), to.getFloor()) ? 500 : 0);
    }
}
