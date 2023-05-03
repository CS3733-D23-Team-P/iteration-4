package edu.wpi.punchy_pegasi.schema;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceRoomEntry extends RequestEntry {
    @com.jsoniter.annotation.JsonProperty("beginningtime")
    private String beginningTime;
    @com.jsoniter.annotation.JsonProperty("endtime")
    private String endTime;
    @com.jsoniter.annotation.JsonProperty("date")
    private LocalDate date;
    @com.jsoniter.annotation.JsonProperty("amountofparticipants")
    private String amountOfParticipants;

    @lombok.Builder(toBuilder = true)
    public ConferenceRoomEntry(UUID serviceID, Long locationName, Long staffAssignment, String additionalNotes, Status status, String beginningTime, String endTime, LocalDate date, String amountOfParticipants, Long employeeID) {
        super(serviceID, locationName, staffAssignment, additionalNotes, status, employeeID);
        this.beginningTime = beginningTime;
        this.endTime = endTime;
        this.date = date;
        this.amountOfParticipants = amountOfParticipants;
    }

    public ConferenceRoomEntry(Long locationName, Long staffAssignment, String additionalNotes, String beginningTime, String endTime, LocalDate date, String amountOfParticipants, Long employeeID) {
        super(UUID.randomUUID(), locationName, staffAssignment, additionalNotes, Status.PROCESSING, employeeID);
        this.beginningTime = beginningTime;
        this.endTime = endTime;
        this.date = date;
        this.amountOfParticipants = amountOfParticipants;
    }
@lombok.RequiredArgsConstructor
public enum Field implements IField<edu.wpi.punchy_pegasi.schema.ConferenceRoomEntry, edu.wpi.punchy_pegasi.schema.ConferenceRoomEntry.ConferenceRoomEntryBuilder>{
        SERVICE_ID("serviceID", true,false),
        LOCATION_NAME("locationName", false,false),
        STAFF_ASSIGNMENT("staffAssignment", false,false),
        ADDITIONAL_NOTES("additionalNotes", false,false),
        STATUS("status", false,false),
        EMPLOYEE_ID("employeeID", false,false),
        BEGINNING_TIME("beginningTime", false,false),
        END_TIME("endTime", false,false),
        DATE("date", false,false),
        AMOUNT_OF_PARTICIPANTS("amountOfParticipants", false,false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;
        public Object getValue(edu.wpi.punchy_pegasi.schema.ConferenceRoomEntry ref){
    return ref.getFromField(this);
}
public String getValueAsString(edu.wpi.punchy_pegasi.schema.ConferenceRoomEntry ref){
    return ref.getFromFieldAsString(this);
}
    public void setValueFromString(edu.wpi.punchy_pegasi.schema.ConferenceRoomEntry.ConferenceRoomEntryBuilder builder, String value){
            switch (this) {
            case SERVICE_ID -> builder.serviceID(java.util.UUID.fromString(value));
            case LOCATION_NAME -> builder.locationName(Long.parseLong(value));
            case STAFF_ASSIGNMENT -> builder.staffAssignment(Long.parseLong(value));
            case ADDITIONAL_NOTES -> builder.additionalNotes(value);
            case STATUS -> builder.status(Status.valueOf(value));
            case EMPLOYEE_ID -> builder.employeeID(Long.parseLong(value));
            case BEGINNING_TIME -> builder.beginningTime(value);
            case END_TIME -> builder.endTime(value);
            case DATE -> builder.date(LocalDate.parse(value));
            case AMOUNT_OF_PARTICIPANTS -> builder.amountOfParticipants(value);
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
            case BEGINNING_TIME -> getBeginningTime();
            case END_TIME -> getEndTime();
            case DATE -> getDate();
            case AMOUNT_OF_PARTICIPANTS -> getAmountOfParticipants();
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
            case BEGINNING_TIME -> getBeginningTime();
            case END_TIME -> getEndTime();
            case DATE -> getDate().toString();
            case AMOUNT_OF_PARTICIPANTS -> getAmountOfParticipants();
        };
    }

}