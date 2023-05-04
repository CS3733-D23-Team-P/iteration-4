package edu.wpi.punchy_pegasi.schema;

import edu.wpi.punchy_pegasi.backend.SchemaID;
import lombok.*;

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder=true)
public class Node implements INode {
    @SchemaID
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("nodeid")
    private Long nodeID;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("xcoord")
    private Integer xcoord;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("ycoord")
    private Integer ycoord;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("floor")
    private String floor;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("building")
    private String building;

    @Override
    public String getId() {
        return nodeID.toString();
    }

    public int getFloorNum() {
        return switch (floor) {
            case "L1" -> 2;
            case "L2" -> 1;
            case "1" -> 3;
            case "2" -> 4;
            case "3" -> 5;
            default -> -1;
        };
    }
@lombok.RequiredArgsConstructor
public enum Field implements IField<edu.wpi.punchy_pegasi.schema.Node, edu.wpi.punchy_pegasi.schema.Node.NodeBuilder>{
        NODE_ID("nodeID", true,false),
        XCOORD("xcoord", false,false),
        YCOORD("ycoord", false,false),
        FLOOR("floor", false,false),
        BUILDING("building", false,false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;
        public Object getValue(edu.wpi.punchy_pegasi.schema.Node ref){
    return ref.getFromField(this);
}
public String getValueAsString(edu.wpi.punchy_pegasi.schema.Node ref){
    return ref.getFromFieldAsString(this);
}
    public void setValueFromString(edu.wpi.punchy_pegasi.schema.Node.NodeBuilder builder, String value){
        if(value == null)
            return;
        switch (this) {
            case NODE_ID -> builder.nodeID(Long.parseLong(value));
            case XCOORD -> builder.xcoord(Integer.parseInt(value));
            case YCOORD -> builder.ycoord(Integer.parseInt(value));
            case FLOOR -> builder.floor(value);
            case BUILDING -> builder.building(value);
            }
        }
        public int oridinal(){
            return ordinal();
        }
    }
    public Object getFromField(Field field) {
        return switch (field) {
            case NODE_ID -> getNodeID();
            case XCOORD -> getXcoord();
            case YCOORD -> getYcoord();
            case FLOOR -> getFloor();
            case BUILDING -> getBuilding();
        };
    }
    public String getFromFieldAsString(Field field) {
        if(getFromField(field) == null)
            return null;
        return switch (field) {
            case NODE_ID -> Long.toString(getNodeID());
            case XCOORD -> Integer.toString(getXcoord());
            case YCOORD -> Integer.toString(getYcoord());
            case FLOOR -> getFloor();
            case BUILDING -> getBuilding();
        };
    }

}