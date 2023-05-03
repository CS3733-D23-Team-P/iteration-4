package edu.wpi.punchy_pegasi.schema;

import edu.wpi.punchy_pegasi.backend.SchemaID;
import lombok.*;

import java.util.UUID;

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder=true)
public class Edge {
    @SchemaID
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("uuid")
    private UUID uuid;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("startnode")
    private Long startNode;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("endnode")
    private Long endNode;
@lombok.RequiredArgsConstructor
public enum Field implements IField<edu.wpi.punchy_pegasi.schema.Edge, edu.wpi.punchy_pegasi.schema.Edge.EdgeBuilder>{
        UUID("uuid", true,false),
        START_NODE("startNode", false,false),
        END_NODE("endNode", false,false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;
        public Object getValue(edu.wpi.punchy_pegasi.schema.Edge ref){
    return ref.getFromField(this);
}
public String getValueAsString(edu.wpi.punchy_pegasi.schema.Edge ref){
    return ref.getFromFieldAsString(this);
}
    public void setValueFromString(edu.wpi.punchy_pegasi.schema.Edge.EdgeBuilder builder, String value){
            switch (this) {
            case UUID -> builder.uuid(java.util.UUID.fromString(value));
            case START_NODE -> builder.startNode(Long.parseLong(value));
            case END_NODE -> builder.endNode(Long.parseLong(value));
            }
        }
        public int oridinal(){
            return ordinal();
        }
    }
    public Object getFromField(Field field) {
        return switch (field) {
            case UUID -> getUuid();
            case START_NODE -> getStartNode();
            case END_NODE -> getEndNode();
        };
    }
    public String getFromFieldAsString(Field field) {
        return switch (field) {
            case UUID -> getUuid().toString();
            case START_NODE -> Long.toString(getStartNode());
            case END_NODE -> Long.toString(getEndNode());
        };
    }

}