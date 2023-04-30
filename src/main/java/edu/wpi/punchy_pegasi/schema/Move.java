package edu.wpi.punchy_pegasi.schema;

import edu.wpi.punchy_pegasi.backend.SchemaID;import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder=true)
public class Move {
    @SchemaID
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("uuid")
    private Long uuid;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("nodeid")
    private Long nodeID;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("locationid")
    private Long locationID;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("date")
    private LocalDate date;
@lombok.RequiredArgsConstructor
public enum Field implements IField<edu.wpi.punchy_pegasi.schema.Move>{
        UUID("uuid", true,false),
        NODE_ID("nodeID", false,false),
        LOCATION_ID("locationID", false,false),
        DATE("date", false,false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;
        public Object getValue(edu.wpi.punchy_pegasi.schema.Move ref){
    return ref.getFromField(this);
}
public String getValueAsString(edu.wpi.punchy_pegasi.schema.Move ref){
    return ref.getFromFieldAsString(this);
}
    public void setValueFromString(edu.wpi.punchy_pegasi.schema.Move ref, String value){
            ref.setFieldFromString(this, value);
        }
        public int oridinal(){
            return ordinal();
        }
    }
    public Object getFromField(Field field) {
        return switch (field) {
            case UUID -> getUuid();
            case NODE_ID -> getNodeID();
            case LOCATION_ID -> getLocationID();
            case DATE -> getDate();
        };
    }
    public void setFieldFromString(Field field, String value) {
        switch (field) {
            case UUID -> setUuid(Long.parseLong(value));
            case NODE_ID -> setNodeID(Long.parseLong(value));
            case LOCATION_ID -> setLocationID(Long.parseLong(value));
            case DATE -> setDate(LocalDate.parse(value));
        };
    }
    public String getFromFieldAsString(Field field) {
        return switch (field) {
            case UUID -> Long.toString(getUuid());
            case NODE_ID -> Long.toString(getNodeID());
            case LOCATION_ID -> Long.toString(getLocationID());
            case DATE -> getDate().toString();
        };
    }

}