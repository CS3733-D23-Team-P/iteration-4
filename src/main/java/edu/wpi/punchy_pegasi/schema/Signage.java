package edu.wpi.punchy_pegasi.schema;

import edu.wpi.punchy_pegasi.backend.SchemaID;
import lombok.*;

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder=true)
public class Signage {
    @SchemaID
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("uuid")
    private Long uuid;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("signname")
    private String signName;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("longname")
    private String longName;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("directiontype")
    private DirectionType directionType;

    public enum DirectionType{
        UP,
        DOWN,
        LEFT,
        RIGHT,
        HERE
    }
@lombok.RequiredArgsConstructor
public enum Field implements IField<edu.wpi.punchy_pegasi.schema.Signage, edu.wpi.punchy_pegasi.schema.Signage.SignageBuilder>{
        UUID("uuid", true,false),
        SIGN_NAME("signName", false,false),
        LONG_NAME("longName", false,false),
        DIRECTION_TYPE("directionType", false,false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;
        public Object getValue(edu.wpi.punchy_pegasi.schema.Signage ref){
    return ref.getFromField(this);
}
public String getValueAsString(edu.wpi.punchy_pegasi.schema.Signage ref){
    return ref.getFromFieldAsString(this);
}
    public void setValueFromString(edu.wpi.punchy_pegasi.schema.Signage.SignageBuilder builder, String value){
            switch (this) {
            case UUID -> builder.uuid(Long.parseLong(value));
            case SIGN_NAME -> builder.signName(value);
            case LONG_NAME -> builder.longName(value);
            case DIRECTION_TYPE -> builder.directionType(DirectionType.valueOf(value));
            }
        }
        public int oridinal(){
            return ordinal();
        }
    }
    public Object getFromField(Field field) {
        return switch (field) {
            case UUID -> getUuid();
            case SIGN_NAME -> getSignName();
            case LONG_NAME -> getLongName();
            case DIRECTION_TYPE -> getDirectionType();
        };
    }
    public String getFromFieldAsString(Field field) {
        return switch (field) {
            case UUID -> Long.toString(getUuid());
            case SIGN_NAME -> getSignName();
            case LONG_NAME -> getLongName();
            case DIRECTION_TYPE -> getDirectionType().name();
        };
    }

}