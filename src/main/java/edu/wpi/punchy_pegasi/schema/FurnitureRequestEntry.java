package edu.wpi.punchy_pegasi.schema;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class FurnitureRequestEntry extends RequestEntry {

    @com.jsoniter.annotation.JsonProperty("selectfurniture")
    private List<String> selectFurniture;

    @lombok.Builder(toBuilder = true)
    public FurnitureRequestEntry(UUID serviceID, Long locationName, Long staffAssignment, String additionalNotes, Status status, List<String> selectFurniture, Long employeeID) {
        super(serviceID, locationName, staffAssignment, additionalNotes, status, employeeID);
        this.selectFurniture = selectFurniture;
    }

    public FurnitureRequestEntry(Long locationName, Long staffAssignment, String additionalNotes, List<String> selectFurniture, Long employeeID) {
        super(UUID.randomUUID(), locationName, staffAssignment, additionalNotes, Status.PROCESSING, employeeID);
        this.selectFurniture = selectFurniture;
    }

@lombok.RequiredArgsConstructor
public enum Field implements IField<edu.wpi.punchy_pegasi.schema.FurnitureRequestEntry, edu.wpi.punchy_pegasi.schema.FurnitureRequestEntry.FurnitureRequestEntryBuilder>{
        SERVICE_ID("serviceID", true,false),
        LOCATION_NAME("locationName", false,false),
        STAFF_ASSIGNMENT("staffAssignment", false,false),
        ADDITIONAL_NOTES("additionalNotes", false,false),
        STATUS("status", false,false),
        EMPLOYEE_ID("employeeID", false,false),
        SELECT_FURNITURE("selectFurniture", false,false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;
        public Object getValue(edu.wpi.punchy_pegasi.schema.FurnitureRequestEntry ref){
    return ref.getFromField(this);
}
public String getValueAsString(edu.wpi.punchy_pegasi.schema.FurnitureRequestEntry ref){
    return ref.getFromFieldAsString(this);
}
    public void setValueFromString(edu.wpi.punchy_pegasi.schema.FurnitureRequestEntry.FurnitureRequestEntryBuilder builder, String value){
        if(value == null)
            return;
        switch (this) {
            case SERVICE_ID -> builder.serviceID(java.util.UUID.fromString(value));
            case LOCATION_NAME -> builder.locationName(Long.parseLong(value));
            case STAFF_ASSIGNMENT -> builder.staffAssignment(Long.parseLong(value));
            case ADDITIONAL_NOTES -> builder.additionalNotes(value);
            case STATUS -> builder.status(Status.valueOf(value));
            case EMPLOYEE_ID -> builder.employeeID(Long.parseLong(value));
            case SELECT_FURNITURE -> builder.selectFurniture(new java.util.ArrayList<>(java.util.Arrays.asList(value.split("\\s*,\\s*"))));
            }
        }
        public int oridinal(){
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
            case SELECT_FURNITURE -> getSelectFurniture();
        };
    }
    public String getFromFieldAsString(Field field) {
        if(getFromField(field) == null)
            return null;
        return switch (field) {
            case SERVICE_ID -> getServiceID().toString();
            case LOCATION_NAME -> Long.toString(getLocationName());
            case STAFF_ASSIGNMENT -> Long.toString(getStaffAssignment());
            case ADDITIONAL_NOTES -> getAdditionalNotes();
            case STATUS -> getStatus().name();
            case EMPLOYEE_ID -> Long.toString(getEmployeeID());
            case SELECT_FURNITURE -> String.join(", ", getSelectFurniture());
        };
    }

}