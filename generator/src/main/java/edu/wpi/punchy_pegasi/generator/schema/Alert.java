package edu.wpi.punchy_pegasi.generator.schema;

import edu.wpi.punchy_pegasi.generator.SchemaID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder=true)
public class Alert {
    @SchemaID
    private UUID uuid;
    private Long employeeID;
    private String alertTitle;
    private String description;
    private Instant dateTime;
    @Setter
    private ReadStatus readStatus;

    public enum ReadStatus {
        READ,
        UNREAD
    }
}