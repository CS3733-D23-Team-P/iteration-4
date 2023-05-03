package edu.wpi.punchy_pegasi.generator.schema;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor
public class FlowerDeliveryRequestEntry extends RequestEntry {
    private List<String> selectedFlowers;
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
}
