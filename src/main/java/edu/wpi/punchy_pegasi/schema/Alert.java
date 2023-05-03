package edu.wpi.punchy_pegasi.schema;

import edu.wpi.punchy_pegasi.backend.SchemaID;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder=true)
public class Alert {
    @SchemaID
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("uuid")
    private UUID uuid;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("alerttype")
    private AlertType alertType;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("alerttitle")
    private String alertTitle;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("description")
    private String description;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("startdate")
    private Instant startDate;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("enddate")
    private Instant endDate; // For map alerts
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("readstatus")
    private ReadStatus readStatus;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("employeeid")
    private Long employeeID; // For employee(who will see the alert) only applies for service/employee alert type
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("nodeid")
    private Long nodeID; // For map alert type

    public enum ReadStatus {
        READ,
        UNREAD
    }

    public enum AlertType {
        NONE,
        MAP,
        MAP_DISABLED,
        EMPLOYEE,
        ADMIN,
        SERVICE_REQUEST
    }
@lombok.RequiredArgsConstructor
public enum Field implements IField<edu.wpi.punchy_pegasi.schema.Alert, edu.wpi.punchy_pegasi.schema.Alert.AlertBuilder>{
        UUID("uuid", true,false),
        ALERT_TYPE("alertType", false,false),
        ALERT_TITLE("alertTitle", false,false),
        DESCRIPTION("description", false,false),
        START_DATE("startDate", false,false),
        END_DATE("endDate", false,false),
        READ_STATUS("readStatus", false,false),
        EMPLOYEE_ID("employeeID", false,false),
        NODE_ID("nodeID", false,false);
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
    public void setValueFromString(edu.wpi.punchy_pegasi.schema.Alert.AlertBuilder builder, String value){
            switch (this) {
            case UUID -> builder.uuid(java.util.UUID.fromString(value));
            case ALERT_TYPE -> builder.alertType(AlertType.valueOf(value));
            case ALERT_TITLE -> builder.alertTitle(value);
            case DESCRIPTION -> builder.description(value);
            case START_DATE -> builder.startDate(Instant.parse(value));
            case END_DATE -> builder.endDate(Instant.parse(value));
            case READ_STATUS -> builder.readStatus(ReadStatus.valueOf(value));
            case EMPLOYEE_ID -> builder.employeeID(Long.parseLong(value));
            case NODE_ID -> builder.nodeID(Long.parseLong(value));
            }
        }
        public int oridinal(){
            return ordinal();
        }
    }
    public Object getFromField(Field field) {
        return switch (field) {
            case UUID -> getUuid();
            case ALERT_TYPE -> getAlertType();
            case ALERT_TITLE -> getAlertTitle();
            case DESCRIPTION -> getDescription();
            case START_DATE -> getStartDate();
            case END_DATE -> getEndDate();
            case READ_STATUS -> getReadStatus();
            case EMPLOYEE_ID -> getEmployeeID();
            case NODE_ID -> getNodeID();
        };
    }
    public String getFromFieldAsString(Field field) {
        return switch (field) {
            case UUID -> getUuid().toString();
            case ALERT_TYPE -> getAlertType().name();
            case ALERT_TITLE -> getAlertTitle();
            case DESCRIPTION -> getDescription();
            case START_DATE -> getStartDate().toString();
            case END_DATE -> getEndDate().toString();
            case READ_STATUS -> getReadStatus().name();
            case EMPLOYEE_ID -> Long.toString(getEmployeeID());
            case NODE_ID -> Long.toString(getNodeID());
        };
    }

}