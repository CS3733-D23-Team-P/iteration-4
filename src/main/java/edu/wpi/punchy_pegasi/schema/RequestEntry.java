package edu.wpi.punchy_pegasi.schema;

import edu.wpi.punchy_pegasi.backend.SchemaID;
import lombok.*;

import java.util.UUID;

@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class RequestEntry {
    @SchemaID
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("serviceid")
    protected UUID serviceID;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("locationname")
    protected Long locationName;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("staffassignment")
    protected Long staffAssignment;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("additionalnotes")
    protected String additionalNotes;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("status")
    protected Status status;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("employeeid")
    protected Long employeeID;

    public enum Status {
        NONE,
        PROCESSING,
        DONE
    }

    @lombok.RequiredArgsConstructor
    public enum Field implements IField<edu.wpi.punchy_pegasi.schema.RequestEntry, Class<?>> {
        SERVICE_ID("serviceID", true, false),
        LOCATION_NAME("locationName", false, false),
        STAFF_ASSIGNMENT("staffAssignment", false, false),
        ADDITIONAL_NOTES("additionalNotes", false, false),
        STATUS("status", false, false),
        EMPLOYEE_ID("employeeID", false, false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;

        public Object getValue(edu.wpi.punchy_pegasi.schema.RequestEntry ref) {
            return ref.getFromField(this);
        }

        public String getValueAsString(edu.wpi.punchy_pegasi.schema.RequestEntry ref) {
            return ref.getFromFieldAsString(this);
        }

        public void setValueFromString(Class<?> builder, String value) {
        }

        public int oridinal() {
            return ordinal();
        }
    }
    public Object getFromField(Field field) {
        return switch (field) {
            case SERVICE_ID -> getServiceID();
            case LOCATION_NAME -> getLocationName();
            case STAFF_ASSIGNMENT -> getStaffAssignment();
            case ADDITIONAL_NOTES -> getAdditionalNotes();
            case STATUS -> getStatus();
            case EMPLOYEE_ID -> getEmployeeID();
        };
    }
    public String getFromFieldAsString(Field field) {
        return switch (field) {
            case SERVICE_ID -> getServiceID().toString();
            case LOCATION_NAME -> Long.toString(getLocationName());
            case STAFF_ASSIGNMENT -> Long.toString(getStaffAssignment());
            case ADDITIONAL_NOTES -> getAdditionalNotes();
            case STATUS -> getStatus().name();
            case EMPLOYEE_ID -> Long.toString(getEmployeeID());
        };
    }

}