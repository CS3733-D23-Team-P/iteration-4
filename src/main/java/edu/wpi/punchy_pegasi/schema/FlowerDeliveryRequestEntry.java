package edu.wpi.punchy_pegasi.schema;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor
public class FlowerDeliveryRequestEntry extends RequestEntry {
    @com.jsoniter.annotation.JsonProperty("selectedflowers")
    private List<String> selectedFlowers;
    @com.jsoniter.annotation.JsonProperty("patientname")
    private String patientName;

    @lombok.Builder(toBuilder = true)
    public FlowerDeliveryRequestEntry(UUID serviceID, String patientName, Long locationName, Long staffAssignment, String additionalNotes, Status status, List<String> selectedFlowers, Long employeeID) {
        super(serviceID, locationName, staffAssignment, additionalNotes, status, employeeID);
        this.selectedFlowers = selectedFlowers;
        this.patientName = patientName;
    }

    public FlowerDeliveryRequestEntry(String patientName, Long locationName, Long staffAssignment, String additionalNotes, List<String> selectedFlowers, Long employeeID) {
        this(UUID.randomUUID(), patientName, locationName, staffAssignment, additionalNotes, Status.PROCESSING, selectedFlowers, employeeID);
    }

    @lombok.RequiredArgsConstructor
    public enum Field implements IField<edu.wpi.punchy_pegasi.schema.FlowerDeliveryRequestEntry, edu.wpi.punchy_pegasi.schema.FlowerDeliveryRequestEntry.FlowerDeliveryRequestEntryBuilder> {
        SERVICE_ID("serviceID", true, false),
        LOCATION_NAME("locationName", false, false),
        STAFF_ASSIGNMENT("staffAssignment", false, false),
        ADDITIONAL_NOTES("additionalNotes", false, false),
        STATUS("status", false, false),
        EMPLOYEE_ID("employeeID", false, false),
        SELECTED_FLOWERS("selectedFlowers", false, false),
        PATIENT_NAME("patientName", false, false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;

        public Object getValue(edu.wpi.punchy_pegasi.schema.FlowerDeliveryRequestEntry ref) {
            return ref.getFromField(this);
        }

        public String getValueAsString(edu.wpi.punchy_pegasi.schema.FlowerDeliveryRequestEntry ref) {
            return ref.getFromFieldAsString(this);
        }

        public void setValueFromString(edu.wpi.punchy_pegasi.schema.FlowerDeliveryRequestEntry.FlowerDeliveryRequestEntryBuilder builder, String value) {
            switch (this) {
                case SERVICE_ID -> builder.serviceID(java.util.UUID.fromString(value));
                case LOCATION_NAME -> builder.locationName(Long.parseLong(value));
                case STAFF_ASSIGNMENT -> builder.staffAssignment(Long.parseLong(value));
                case ADDITIONAL_NOTES -> builder.additionalNotes(value);
                case STATUS -> builder.status(Status.valueOf(value));
                case EMPLOYEE_ID -> builder.employeeID(Long.parseLong(value));
                case SELECTED_FLOWERS ->
                        builder.selectedFlowers(new java.util.ArrayList<>(java.util.Arrays.asList(value.split("\\s*,\\s*"))));
                case PATIENT_NAME -> builder.patientName(value);
            }
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
            case SELECTED_FLOWERS -> getSelectedFlowers();
            case PATIENT_NAME -> getPatientName();
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
            case SELECTED_FLOWERS -> String.join(", ", getSelectedFlowers());
            case PATIENT_NAME -> getPatientName();
        };
    }

}