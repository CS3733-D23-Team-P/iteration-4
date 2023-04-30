package edu.wpi.punchy_pegasi.schema;

import edu.wpi.punchy_pegasi.backend.SchemaID;import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
            case "L1" -> 1;
            case "L2" -> 2;
            case "1" -> 3;
            case "2" -> 4;
            case "3" -> 5;
            default -> -1;
        };
    }
@lombok.RequiredArgsConstructor
public enum Field implements IField<edu.wpi.punchy_pegasi.schema.Node>{
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
    public void setValueFromString(edu.wpi.punchy_pegasi.schema.Node ref, String value){
            ref.setFieldFromString(this, value);
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
    public void setFieldFromString(Field field, String value) {
        switch (field) {
            case NODE_ID -> setNodeID(Long.parseLong(value));
            case XCOORD -> setXcoord(Integer.parseInt(value));
            case YCOORD -> setYcoord(Integer.parseInt(value));
            case FLOOR -> setFloor(value);
            case BUILDING -> setBuilding(value);
        };
    }
    public String getFromFieldAsString(Field field) {
        return switch (field) {
            case NODE_ID -> Long.toString(getNodeID());
            case XCOORD -> Integer.toString(getXcoord());
            case YCOORD -> Integer.toString(getYcoord());
            case FLOOR -> getFloor();
            case BUILDING -> getBuilding();
        };
    }

}