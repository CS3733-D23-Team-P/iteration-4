package edu.wpi.punchy_pegasi.backend.pathfinding;

import edu.wpi.punchy_pegasi.schema.LocationName;
import edu.wpi.punchy_pegasi.schema.Node;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.List;

public class TypedNode extends Node {
    @Getter
    private final List<LocationName.NodeType> nodeType;
    public TypedNode(Node node, List<LocationName.NodeType> nodeType) {
        super(node.getNodeID(), node.getXcoord(), node.getYcoord(), node.getFloor(), node.getBuilding());
        this.nodeType = nodeType;
    }
}
