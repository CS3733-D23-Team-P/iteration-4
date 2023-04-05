package edu.wpi.punchy_pegasi.schema;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {
    @SchemaID
    private Long uuid;
    private String startNode;
    private String endNode;
}
