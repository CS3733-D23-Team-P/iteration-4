package edu.wpi.punchy_pegasi.generator.schema;

import edu.wpi.punchy_pegasi.generator.SchemaID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder=true)
public class Node implements INode {
    @SchemaID
    private Long nodeID;
    private Integer xcoord;
    private Integer ycoord;
    private String floor;
    private String building;

    @Override
    public String getId() {
        return nodeID.toString();
    }

    public int getFloorNum() {
        return switch (floor) {
            case "L1" -> 1;
            case "L2" -> 2;
            case "1" -> 3;
            case "2" -> 4;
            case "3" -> 5;
            default -> -1;
        };
    }
}
