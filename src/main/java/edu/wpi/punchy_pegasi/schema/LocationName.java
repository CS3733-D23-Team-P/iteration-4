package edu.wpi.punchy_pegasi.schema;

import edu.wpi.punchy_pegasi.backend.SchemaID;
import lombok.*;

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder=true)
public class LocationName {
    @SchemaID
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("uuid")
    private Long uuid;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("longname")
    private String longName;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("shortname")
    private String shortName;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("nodetype")
    private NodeType nodeType;
    public enum NodeType {
        HALL,
        ELEV,
        REST,
        STAI,
        DEPT,
        LABS,
        INFO,
        CONF,
        EXIT,
        RETL,
        SERV,
        BATH
    }
@lombok.RequiredArgsConstructor
public enum Field implements IField<edu.wpi.punchy_pegasi.schema.LocationName, edu.wpi.punchy_pegasi.schema.LocationName.LocationNameBuilder>{
        UUID("uuid", true,false),
        LONG_NAME("longName", false,false),
        SHORT_NAME("shortName", false,false),
        NODE_TYPE("nodeType", false,false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;
        public Object getValue(edu.wpi.punchy_pegasi.schema.LocationName ref){
    return ref.getFromField(this);
}
public String getValueAsString(edu.wpi.punchy_pegasi.schema.LocationName ref){
    return ref.getFromFieldAsString(this);
}
    public void setValueFromString(edu.wpi.punchy_pegasi.schema.LocationName.LocationNameBuilder builder, String value){
        if(value == null)
            return;
        switch (this) {
            case UUID -> builder.uuid(Long.parseLong(value));
            case LONG_NAME -> builder.longName(value);
            case SHORT_NAME -> builder.shortName(value);
            case NODE_TYPE -> builder.nodeType(NodeType.valueOf(value));
            }
        }
        public int oridinal(){
            return ordinal();
        }
    }
    public Object getFromField(Field field) {
        return switch (field) {
            case UUID -> getUuid();
            case LONG_NAME -> getLongName();
            case SHORT_NAME -> getShortName();
            case NODE_TYPE -> getNodeType();
        };
    }
    public String getFromFieldAsString(Field field) {
        if(getFromField(field) == null)
            return null;
        return switch (field) {
            case UUID -> Long.toString(getUuid());
            case LONG_NAME -> getLongName();
            case SHORT_NAME -> getShortName();
            case NODE_TYPE -> getNodeType().name();
        };
    }

}