package edu.wpi.punchy_pegasi.generator.schema;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class FoodServiceRequestEntry extends RequestEntry {
    private List<String> selectedFoods;
    private String patientName;

    @lombok.Builder(toBuilder = true)
    public FoodServiceRequestEntry(UUID serviceID, Long locationName, Long staffAssignment, String additionalNotes, Status status, List<String> selectedFoods, String patientName, Long employeeID) {
        super(serviceID, locationName, staffAssignment, additionalNotes, status, employeeID);
        this.selectedFoods = selectedFoods;
        this.patientName = patientName;
    }

    public FoodServiceRequestEntry(Long locationName, Long staffAssignment, String additionalNotes, List<String> selectedFoods, String patientName, Long employeeID) {
        super(UUID.randomUUID(), locationName, staffAssignment, additionalNotes, Status.PROCESSING, employeeID);
        this.selectedFoods = selectedFoods;
        this.patientName = patientName;
    }
}
