package edu.wpi.punchy_pegasi.backend.pathfinding;

import edu.wpi.punchy_pegasi.schema.LocationName;

public class MapHeuristic implements IHeuristic<TypedNode> {
    @Override
    public double computeCost(TypedNode from, TypedNode to) {
        var cartesian = Math.sqrt(Math.pow(from.getXcoord() - to.getXcoord(), 2d) + Math.pow(from.getYcoord() - to.getYcoord(), 2d));
        var elevator = from.getNodeType().stream().anyMatch(r-> r == LocationName.NodeType.ELEV) && to.getNodeType().stream().anyMatch(r-> r == LocationName.NodeType.ELEV);
        var diffFloor = Math.abs(from.getFloorNum() - to.getFloorNum());
        var verticalCost = diffFloor == 0 ? 0 : Math.min(diffFloor * 1000 - (diffFloor-1), elevator ? 1500 : Double.MAX_VALUE);
        return cartesian + verticalCost;
    }
}
