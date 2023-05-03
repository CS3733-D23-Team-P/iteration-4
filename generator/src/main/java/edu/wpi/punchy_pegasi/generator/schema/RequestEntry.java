package edu.wpi.punchy_pegasi.generator.schema;

import edu.wpi.punchy_pegasi.generator.SchemaID;
import lombok.*;

import java.util.UUID;

@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class RequestEntry {
    @SchemaID
    protected UUID serviceID;
    protected Long locationName;
    protected Long staffAssignment;
    protected String additionalNotes;
    protected Status status;
    protected Long employeeID;

    public enum Status {
        NONE,
        PROCESSING,
        DONE
    }
}
