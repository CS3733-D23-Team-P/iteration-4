package edu.wpi.punchy_pegasi.backend.pathfinding;

import edu.wpi.punchy_pegasi.schema.LocationName;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapHeuristic implements IHeuristic<TypedNode> {
    private final double stairWeight;
    private final double elevatorWeight;
    MapHeuristic(){
        elevatorWeight = 1500;
        stairWeight = 1000;
    }
    @Override
    public double computeCost(TypedNode from, TypedNode to) {
        var cartesian = Math.sqrt(Math.pow(from.getXcoord() - to.getXcoord(), 2d) + Math.pow(from.getYcoord() - to.getYcoord(), 2d));
        var elevator = from.getNodeType().stream().anyMatch(r-> r == LocationName.NodeType.ELEV) && to.getNodeType().stream().anyMatch(r-> r == LocationName.NodeType.ELEV);
        var stair =  from.getNodeType().stream().anyMatch(r-> r == LocationName.NodeType.STAI) && to.getNodeType().stream().anyMatch(r-> r == LocationName.NodeType.STAI);
        var diffFloor = Math.abs(from.getFloorNum() - to.getFloorNum());
        var verticalCost = 0.0;
        if(!stair && !elevator) verticalCost = 1000 * diffFloor;
        else verticalCost = Math.min(stair ? diffFloor * stairWeight - (diffFloor-1) : Double.POSITIVE_INFINITY, elevator ? elevatorWeight : Double.POSITIVE_INFINITY);
        return cartesian + verticalCost;
    }
}
