package edu.wpi.punchy_pegasi.schema;

import edu.wpi.punchy_pegasi.backend.SchemaID;import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder=true)
public class Alert {
    @SchemaID
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("uuid")
    private UUID uuid;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("employeeid")
    private Long employeeID;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("alerttitle")
    private String alertTitle;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("description")
    private String description;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("datetime")
    private Instant dateTime;
    @Setter
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("readstatus")
    private ReadStatus readStatus;

    public enum ReadStatus {
        READ,
        UNREAD
    }
@lombok.RequiredArgsConstructor
public enum Field implements IField<edu.wpi.punchy_pegasi.schema.Alert>{
        UUID("uuid", true,false),
        EMPLOYEE_ID("employeeID", false,false),
        ALERT_TITLE("alertTitle", false,false),
        DESCRIPTION("description", false,false),
        DATE_TIME("dateTime", false,false),
        READ_STATUS("readStatus", false,false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;
        public Object getValue(edu.wpi.punchy_pegasi.schema.Alert ref){
    return ref.getFromField(this);
}
public String getValueAsString(edu.wpi.punchy_pegasi.schema.Alert ref){
    return ref.getFromFieldAsString(this);
}
    public void setValueFromString(edu.wpi.punchy_pegasi.schema.Alert ref, String value){
            ref.setFieldFromString(this, value);
        }
        public int oridinal(){
            return ordinal();
        }
    }
    public Object getFromField(Field field) {
        return switch (field) {
            case UUID -> getUuid();
            case EMPLOYEE_ID -> getEmployeeID();
            case ALERT_TITLE -> getAlertTitle();
            case DESCRIPTION -> getDescription();
            case DATE_TIME -> getDateTime();
            case READ_STATUS -> getReadStatus();
        };
    }
    public void setFieldFromString(Field field, String value) {
        switch (field) {
            case UUID -> setUuid(UUID.fromString(value));
            case EMPLOYEE_ID -> setEmployeeID(Long.parseLong(value));
            case ALERT_TITLE -> setAlertTitle(value);
            case DESCRIPTION -> setDescription(value);
            case DATE_TIME -> setDateTime(Instant.parse(value));
            case READ_STATUS -> setReadStatus(ReadStatus.valueOf(value));
        };
    }
    public String getFromFieldAsString(Field field) {
        return switch (field) {
            case UUID -> getUuid().toString();
            case EMPLOYEE_ID -> Long.toString(getEmployeeID());
            case ALERT_TITLE -> getAlertTitle();
            case DESCRIPTION -> getDescription();
            case DATE_TIME -> getDateTime().toString();
            case READ_STATUS -> getReadStatus().name();
        };
    }

}