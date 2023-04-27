package edu.wpi.punchy_pegasi.generator.schema;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OfficeServiceRequestEntry extends RequestEntry {
    private List<String> officeSupplies;

    public OfficeServiceRequestEntry(UUID serviceID, Long locationName, Long staffAssignment, String additionalNotes, Status status, List<String> officeSupplies, Long employeeID) {
        super(serviceID, locationName, staffAssignment, additionalNotes, status, employeeID);
        this.officeSupplies = officeSupplies;
    }

    public OfficeServiceRequestEntry(Long locationName, Long staffAssignment, String additionalNotes, List<String> officeSupplies, Long employeeID) {
        super(UUID.randomUUID(), locationName, staffAssignment, additionalNotes, Status.PROCESSING, employeeID);
        this.officeSupplies = officeSupplies;
    }
}
