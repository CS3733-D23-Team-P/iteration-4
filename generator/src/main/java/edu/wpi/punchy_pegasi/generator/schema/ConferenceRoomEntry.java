package edu.wpi.punchy_pegasi.generator.schema;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceRoomEntry extends RequestEntry {
    private String beginningTime;
    private String endTime;
    private LocalDate date;
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
}
