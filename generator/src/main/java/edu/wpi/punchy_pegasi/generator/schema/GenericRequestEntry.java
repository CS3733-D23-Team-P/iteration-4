package edu.wpi.punchy_pegasi.generator.schema;

import lombok.*;

import java.util.UUID;

@Getter
@Setter(AccessLevel.NONE)
@AllArgsConstructor
public class GenericRequestEntry extends RequestEntry {
    @lombok.Builder(toBuilder = true)
    public GenericRequestEntry(UUID serviceID, Long locationName, Long staffAssignment, String additionalNotes, Status status, Long employeeID) {
        super(serviceID, locationName, staffAssignment, additionalNotes, status, employeeID);
    }

    public GenericRequestEntry(Long locationName, Long staffAssignment, String additionalNotes, Long employeeID) {
        super(UUID.randomUUID(), locationName, staffAssignment, additionalNotes, Status.PROCESSING, employeeID);
    }
}
