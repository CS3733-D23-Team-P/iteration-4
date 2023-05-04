package edu.wpi.punchy_pegasi.generated;

import edu.wpi.punchy_pegasi.backend.PdbController;
import edu.wpi.punchy_pegasi.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class FacadeTest {
    static PdbController pdbController;
    static Facade facade;
    static String[] nodeFields;
    static String[] edgeFields;
    static String[] moveFields;
    static String[] locationNameFields;
    static String[] requestFields;
    static String[] foodServiceFields;
    static String[] flowerDeliveryFields;
    static String[] conferenceRoomFields;
    static String[] furnitureRequestFields;
    static String[] officeServiceFields;
    static String[] employeeFields;
    static String[] accountFields;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        nodeFields = new String[]{"nodeID", "xcoord", "ycoord", "floor", "building"};
        edgeFields = new String[]{"uuid", "startNode", "endNode"};
        moveFields = new String[]{"uuid", "nodeID", "locationID", "date"};
        locationNameFields = new String[]{"uuid", "longName", "shortName", "nodeType"};
        requestFields = new String[]{"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "employeeID"};
        foodServiceFields = new String[]{"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "selectedFoods", "patientName", "employeeID"};
        flowerDeliveryFields = new String[]{"serviceID", "patientName", "locationName", "staffAssignment", "additionalNotes", "status", "selectedFlowers", "employeeID"};
        conferenceRoomFields = new String[]{"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "beginningTime", "endTime", "date", "amountOfParticipants", "employeeID"};
        furnitureRequestFields = new String[]{"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "selectFurniture", "employeeID"};
        officeServiceFields = new String[]{"serviceID", "locationName", "staffAssignment", "additionalNotes", "status", "officeSupplies", "employeeID"};
        employeeFields = new String[]{"employeeID", "firstName", "lastName"};
        accountFields = AccountCachedDaoImpl.fields;
        try {
            pdbController = new PdbController(Config.source, "test");
        } catch (PdbController.DatabaseException e) {
            log.error("Could not connect to database");
        }
        for (var tt : TableType.values()) {
            try {
                pdbController.initTableByType(tt);
            } catch (PdbController.DatabaseException e) {
                log.error("Could not init table " + tt.name());
            }
        }
        facade = new Facade(pdbController);
    }

    @AfterEach
    void tearDown() throws SQLException {
        var statement = pdbController.exposeConnection().createStatement();
        statement.execute("drop schema test cascade;");
        statement.close();
    }

    @Test
    void getNode() {
        Node node = new Node(100L, 500, 500, "L1", "testBuilding");
        Object[] values = new Object[]{node.getNodeID(), node.getXcoord(), node.getYcoord(), node.getFloor(), node.getBuilding()};
        try {
            pdbController.insertQuery(TableType.NODES, nodeFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<Node> results = facade.getNode(node.getNodeID());
        Node daoresult = results.get();
        assertEquals(daoresult, node);
        try {
            pdbController.deleteQuery(TableType.NODES, "nodeID", node.getNodeID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetNode() {
        Node node = new Node(100L, 500, 500, "L1", "testBuilding");
        Node node2 = new Node(101L, 500, 500, "L1", "testBuilding");
        Object[] values = new Object[]{node.getNodeID(), node.getXcoord(), node.getYcoord(), node.getFloor(), node.getBuilding()};
        Object[] values2 = new Object[]{node2.getNodeID(), node2.getXcoord(), node2.getYcoord(), node2.getFloor(), node2.getBuilding()};
        try {
            pdbController.insertQuery(TableType.NODES, nodeFields, values);
            pdbController.insertQuery(TableType.NODES, nodeFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        var results = facade.getNode(Node.Field.BUILDING, "testBuilding");
        var map = new HashMap<Long, Node>();
        try (var rs = pdbController.searchQuery(TableType.NODES, "building", "testBuilding")) {
            while (rs.next()) {
                Node req = new Node(
                        (java.lang.Long) rs.getObject("nodeID"),
                        (java.lang.Integer) rs.getObject("xcoord"),
                        (java.lang.Integer) rs.getObject("ycoord"),
                        (java.lang.String) rs.getObject("floor"),
                        (java.lang.String) rs.getObject("building"));
                if (req != null)
                    map.put(req.getNodeID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(node.getNodeID()), results.get(node.getNodeID()));
        assertEquals(map.get(node2.getNodeID()), results.get(node2.getNodeID()));
        try {
            pdbController.deleteQuery(TableType.NODES, "nodeID", node.getNodeID());
            pdbController.deleteQuery(TableType.NODES, "nodeID", node2.getNodeID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetNode1() {
        Node node = new Node(100L, 500, 500, "L1", "testBuilding");
        Node node2 = new Node(101L, 400, 500, "L1", "testBuilding");
        Node node3 = new Node(102L, 500, 500, "L1", "testBuilding1");
        Object[] values = new Object[]{node.getNodeID(), node.getXcoord(), node.getYcoord(), node.getFloor(), node.getBuilding()};
        Object[] values2 = new Object[]{node2.getNodeID(), node2.getXcoord(), node2.getYcoord(), node2.getFloor(), node2.getBuilding()};
        Object[] values3 = new Object[]{node3.getNodeID(), node3.getXcoord(), node3.getYcoord(), node3.getFloor(), node3.getBuilding()};
        try {
            pdbController.insertQuery(TableType.NODES, nodeFields, values);
            pdbController.insertQuery(TableType.NODES, nodeFields, values2);
            pdbController.insertQuery(TableType.NODES, nodeFields, values3);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Node.Field[] fields = {Node.Field.BUILDING, Node.Field.XCOORD};
        Object[] searchValues = new Object[]{"testBuilding", 500};
        String[] searchFields = new String[]{"building", "xcoord"};
        var results = facade.getNode(fields, searchValues);
        var map = new HashMap<Long, Node>();
        try (var rs = pdbController.searchQuery(TableType.NODES, searchFields, searchValues)) {
            while (rs.next()) {
                Node req = new Node(
                        (java.lang.Long) rs.getObject("nodeID"),
                        (java.lang.Integer) rs.getObject("xcoord"),
                        (java.lang.Integer) rs.getObject("ycoord"),
                        (java.lang.String) rs.getObject("floor"),
                        (java.lang.String) rs.getObject("building"));
                if (req != null)
                    map.put(req.getNodeID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(node.getNodeID()), results.get(node.getNodeID()));
        assertEquals(map.get(node2.getNodeID()), results.get(node2.getNodeID()));
        assertEquals(map.get(node3.getNodeID()), results.get(node3.getNodeID()));
        try {
            pdbController.deleteQuery(TableType.NODES, "nodeID", node.getNodeID());
            pdbController.deleteQuery(TableType.NODES, "nodeID", node2.getNodeID());
            pdbController.deleteQuery(TableType.NODES, "nodeID", node3.getNodeID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void getAllNode() {
//        var values0 = new Object[]{100L, 500, 500, "L1", "testBuilding0"};
//        var values1 = new Object[]{101L, 501, 501, "L1", "testBuilding1"};
//        var values2 = new Object[]{102L, 502, 502, "L1", "testBuilding2"};
//        var valueSet = new Object[][]{values0, values1, values2};
//
//        var refMap = new HashMap<Long, Node>();
//        for (var values : valueSet) {
//            try {
//                pdbController.insertQuery(TableType.NODES, nodeFields, values);
//            } catch (PdbController.DatabaseException e) {
//                assert false : "Failed to insert Node";
//            }
//            Node node = new Node((Long) values[0], (Integer) values[1], (Integer) values[2], (String) values[3], (String) values[4]);
//            refMap.put(node.getNodeID(), node);
//        }
//
//        Map<Long, Node> resultMap = facade.getAllNode();
//        assertEquals(refMap, resultMap);
//        for (var key : resultMap.keySet()) {
//            try {
//                pdbController.deleteQuery(TableType.NODES, "nodeID", key);
//            } catch (PdbController.DatabaseException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//    }

    @Test
    void saveNode() {
        Node node = new Node(100L, 500, 500, "L1", "testBuilding");
        facade.saveNode(node);
        Optional<Node> results = facade.getNode(node.getNodeID());
        Node daoresult = results.get();
        assertEquals(node, daoresult);
        try {
            pdbController.deleteQuery(TableType.NODES, "nodeID", node.getNodeID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateNode() {
        Node node = new Node(100L, 500, 500, "L1", "testBuilding");
        facade.saveNode(node);

        Node updatedNode = new Node(100L, 1500, 1500, "L2", "updatedTestBuilding");
        Node.Field[] fields = {Node.Field.XCOORD, Node.Field.YCOORD, Node.Field.FLOOR, Node.Field.BUILDING};
        facade.updateNode(updatedNode, fields);

        Optional<Node> results = facade.getNode(node.getNodeID());
        Node daoresult = results.get();
        assertEquals(updatedNode, daoresult);
        try {
            pdbController.deleteQuery(TableType.NODES, "nodeID", node.getNodeID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteNode() {
        Node node = new Node(100L, 500, 500, "L1", "testBuilding");
        Object[] values = new Object[]{node.getNodeID(), node.getXcoord(), node.getYcoord(), node.getFloor(), node.getBuilding()};
        try {
            pdbController.insertQuery(TableType.NODES, nodeFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert node";
        }

        try {
            pdbController.searchQuery(TableType.NODES, "nodeID", node.getNodeID());
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to find node";
        }

        facade.deleteNode(node);

        try {
            pdbController.searchQuery(TableType.NODES, "nodeID", node.getNodeID());
        } catch (PdbController.DatabaseException e) {
            assert true : "Node was deleted successfully";
        }
    }

    @Test
    void getEdge() {
        Edge edge = new Edge(UUID.randomUUID(), 123123L, 123123L);
        Object[] values = new Object[]{edge.getUuid(), edge.getStartNode(), edge.getEndNode()};
        try {
            pdbController.insertQuery(TableType.EDGES, edgeFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<Edge> results = facade.getEdge(edge.getUuid());
        Edge daoresult = results.get();
        assertEquals(daoresult, edge);
        try {
            pdbController.deleteQuery(TableType.EDGES, "uuid", edge.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetEdge() {
        var edge = new Edge(UUID.randomUUID(), 123123L, 123123L);
        var edge2 = new Edge(UUID.randomUUID(), 123123L, 123123L);
        var values = new Object[]{edge.getUuid(), edge.getStartNode(), edge.getEndNode()};
        var values2 = new Object[]{edge2.getUuid(), edge2.getStartNode(), edge2.getEndNode()};
        try {
            pdbController.insertQuery(TableType.EDGES, edgeFields, values);
            pdbController.insertQuery(TableType.EDGES, edgeFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        var results = facade.getEdge(Edge.Field.START_NODE, 123123L);
        var map = new HashMap<UUID, Edge>();
        try (var rs = pdbController.searchQuery(TableType.EDGES, Edge.Field.START_NODE.getColName(), 123123L)) {
            while (rs.next()) {
                Edge req = new Edge(
                        (java.util.UUID) rs.getObject("uuid"),
                        (java.lang.Long) rs.getObject("startNode"),
                        (java.lang.Long) rs.getObject("endNode"));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(edge.getUuid()), results.get(edge.getUuid()));
        assertEquals(map.get(edge2.getUuid()), results.get(edge2.getUuid()));
        try {
            pdbController.deleteQuery(TableType.EDGES, "uuid", edge.getUuid());
            pdbController.deleteQuery(TableType.EDGES, "uuid", edge2.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetEdge1() {
        var edge = new Edge(UUID.randomUUID(), 1L, 2L);
        var edge2 = new Edge(UUID.randomUUID(), 2L, 2L);
        var edge3 = new Edge(UUID.randomUUID(), 2L, 0L);
        var values = new Object[]{edge.getUuid(), edge.getStartNode(), edge.getEndNode()};
        var values2 = new Object[]{edge2.getUuid(), edge2.getStartNode(), edge2.getEndNode()};
        var values3 = new Object[]{edge3.getUuid(), edge3.getStartNode(), edge3.getEndNode()};
        try {
            pdbController.insertQuery(TableType.EDGES, edgeFields, values);
            pdbController.insertQuery(TableType.EDGES, edgeFields, values2);
            pdbController.insertQuery(TableType.EDGES, edgeFields, values3);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Edge.Field[] fields = {Edge.Field.START_NODE, Edge.Field.END_NODE};
        Object[] searchValues = new Object[]{2L, 2L};
        String[] searchFields = new String[]{"startNode", "endNode"};
        var results = facade.getEdge(fields, searchValues);
        var map = new HashMap<UUID, Edge>();
        try (var rs = pdbController.searchQuery(TableType.EDGES, searchFields, searchValues)) {
            while (rs.next()) {
                Edge req = new Edge(
                        (java.util.UUID) rs.getObject("uuid"),
                        (java.lang.Long) rs.getObject("startNode"),
                        (java.lang.Long) rs.getObject("endNode"));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(edge.getUuid()), results.get(edge.getUuid()));
        assertEquals(map.get(edge2.getUuid()), results.get(edge2.getUuid()));
        assertEquals(map.get(edge3.getUuid()), results.get(edge3.getUuid()));
        try {
            pdbController.deleteQuery(TableType.EDGES, "uuid", edge.getUuid());
            pdbController.deleteQuery(TableType.EDGES, "uuid", edge2.getUuid());
            pdbController.deleteQuery(TableType.EDGES, "uuid", edge3.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllEdge() {
        var value0 = new Object[]{UUID.randomUUID(), 1005L, 1006L};
        var value1 = new Object[]{UUID.randomUUID(), 1005L, 1007L};
        var value2 = new Object[]{UUID.randomUUID(), 1005L, 1008L};
        var valueSet = new Object[][]{value0, value1, value2};
        var refMap = new HashMap<UUID, Edge>();
        for (Object[] values : valueSet) {
            try {
                pdbController.insertQuery(TableType.EDGES, edgeFields, values);
            } catch (PdbController.DatabaseException e) {
                assert false : "Failed to insert edge";
            }
            Edge edge = new Edge((UUID) values[0], (Long) values[1], (Long) values[2]);
            refMap.put(edge.getUuid(), edge);
        }

        Map<UUID, Edge> resultMap = facade.getAllEdge();
        assertEquals(refMap, resultMap);
        for (var uuid : refMap.keySet()) {
            try {
                pdbController.deleteQuery(TableType.EDGES, "uuid", uuid);
            } catch (PdbController.DatabaseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void saveEdge() {
        Edge edge = new Edge(UUID.randomUUID(), 1005L, 1006L);
        facade.saveEdge(edge);
        Optional<Edge> results = facade.getEdge(edge.getUuid());
        Edge daoresult = results.get();
        assertEquals(edge, daoresult);
        try {
            pdbController.deleteQuery(TableType.EDGES, "uuid", edge.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateEdge() {
        Edge edge = new Edge(UUID.randomUUID(), 1005L, 1006L);
        facade.saveEdge(edge);

        Edge updatedEdge = new Edge(edge.getUuid(), 2005L, 2006L);
        Edge.Field[] fields = {Edge.Field.START_NODE, Edge.Field.END_NODE};
        facade.updateEdge(updatedEdge, fields);

        Optional<Edge> results = facade.getEdge(edge.getUuid());
        Edge daoresult = results.get();
        assertEquals(updatedEdge, daoresult);
        try {
            pdbController.deleteQuery(TableType.EDGES, "uuid", edge.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteEdge() {
        UUID uuid = UUID.randomUUID();
        long startNode = 2222;
        long endNode = 3333;
        Edge edge = new Edge(uuid, startNode, endNode);
        var values = new Object[]{
                edge.getUuid(),
                edge.getStartNode(),
                edge.getEndNode()
        };
        try {
            pdbController.insertQuery(TableType.EDGES, edgeFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert edge";
            throw new RuntimeException(e);
        }

        try {
            pdbController.searchQuery(TableType.EDGES, edgeFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to find edge";
            throw new RuntimeException(e);
        }

        facade.deleteEdge(edge);

        try {
            pdbController.searchQuery(TableType.EDGES, edgeFields, values);
        } catch (PdbController.DatabaseException e) {
            assert true : "Successfully deleted edge";
        }
    }

    @Test
    void getMove() {
        Move move = new Move(100L, 1005L, 100L, LocalDate.now());
        Object[] values = new Object[]{move.getUuid(), move.getNodeID(), move.getLocationID(), move.getDate()};
        try {
            pdbController.insertQuery(TableType.MOVES, moveFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<Move> results = facade.getMove(move.getUuid());
        Move daoresult = results.get();
        assertEquals(daoresult, move);
        try {
            pdbController.deleteQuery(TableType.MOVES, "uuid", move.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetMove() {
        Move move = new Move(100L, 1005L, 100L, LocalDate.now());
        Move move2 = new Move(101L, 1005L, 100L, LocalDate.now());
        Object[] values = new Object[]{move.getUuid(), move.getNodeID(), move.getLocationID(), move.getDate()};
        Object[] values2 = new Object[]{move2.getUuid(), move2.getNodeID(), move2.getLocationID(), move2.getDate()};
        try {
            pdbController.insertQuery(TableType.MOVES, moveFields, values);
            pdbController.insertQuery(TableType.MOVES, moveFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        var results = facade.getMove(Move.Field.LOCATION_ID, 100L);
        var map = new HashMap<java.lang.Long, Move>();
        try (var rs = pdbController.searchQuery(TableType.MOVES, "locationID", 100L)) {
            while (rs.next()) {
                java.sql.Date dd = (java.sql.Date) rs.getObject("date");
                LocalDate ld = dd.toLocalDate();
                Move req = new Move(
                        (java.lang.Long) rs.getObject("uuid"),
                        (java.lang.Long) rs.getObject("nodeID"),
                        (java.lang.Long) rs.getObject("locationID"),
                        ld);
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(move.getUuid()), results.get(move.getUuid()));
        assertEquals(map.get(move2.getUuid()), results.get(move2.getUuid()));
        try {
            pdbController.deleteQuery(TableType.MOVES, "uuid", move.getUuid());
            pdbController.deleteQuery(TableType.MOVES, "uuid", move2.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetMove1() {
        Move move = new Move(100L, 1005L, 100L, LocalDate.now());
        Move move2 = new Move(101L, 1000L, 200L, LocalDate.now());
        Move move3 = new Move(102L, 1005L, 300L, LocalDate.now());
        Object[] values = new Object[]{move.getUuid(), move.getNodeID(), move.getLocationID(), move.getDate()};
        Object[] values2 = new Object[]{move2.getUuid(), move2.getNodeID(), move2.getLocationID(), move2.getDate()};
        Object[] values3 = new Object[]{move3.getUuid(), move3.getNodeID(), move3.getLocationID(), move3.getDate()};
        try {
            pdbController.insertQuery(TableType.MOVES, moveFields, values);
            pdbController.insertQuery(TableType.MOVES, moveFields, values2);
            pdbController.insertQuery(TableType.MOVES, moveFields, values3);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Move.Field[] fields = {Move.Field.LOCATION_ID, Move.Field.NODE_ID};
        Object[] searchValues = new Object[]{200L, 1005L};
        String[] searchFields = new String[]{"locationID", "nodeID"};
        var results = facade.getMove(fields, searchValues);
        var map = new HashMap<java.lang.Long, Move>();
        try (var rs = pdbController.searchQuery(TableType.MOVES, searchFields, searchValues)) {
            while (rs.next()) {
                java.sql.Date dd = (java.sql.Date) rs.getObject("date");
                LocalDate ld = dd.toLocalDate();
                Move req = new Move(
                        (java.lang.Long) rs.getObject("uuid"),
                        (java.lang.Long) rs.getObject("nodeID"),
                        (java.lang.Long) rs.getObject("locationID"),
                        ld);
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(move.getUuid()), results.get(move.getUuid()));
        assertEquals(map.get(move2.getUuid()), results.get(move2.getUuid()));
        assertEquals(map.get(move3.getUuid()), results.get(move3.getUuid()));
        try {
            pdbController.deleteQuery(TableType.MOVES, "uuid", move.getUuid());
            pdbController.deleteQuery(TableType.MOVES, "uuid", move2.getUuid());
            pdbController.deleteQuery(TableType.MOVES, "uuid", move3.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllMove() {
        var values0 = new Object[]{100L, 1005L, 100L, LocalDate.now()};
        var values1 = new Object[]{101L, 1006L, 200L, LocalDate.now()};
        var values2 = new Object[]{102L, 1007L, 300L, LocalDate.now()};
        var valueSet = new Object[][]{values0, values1, values2};

        var refMap = new HashMap<Long, Move>();
        for (Object[] values : valueSet) {
            var move = new Move((Long) values[0], (Long) values[1], (Long) values[2], (LocalDate) values[3]);
            refMap.put(move.getUuid(), move);
            try {
                pdbController.insertQuery(TableType.MOVES, moveFields, values);
            } catch (PdbController.DatabaseException e) {
                throw new RuntimeException(e);
            }
        }

        Map<Long, Move> resultMap = facade.getAllMove();
        assertEquals(refMap, resultMap);
        for (var uuid : refMap.keySet()) {
            try {
                pdbController.deleteQuery(TableType.MOVES, "uuid", uuid);
            } catch (PdbController.DatabaseException e) {
                assert false : "Failed to delete from database";
            }
        }

    }

    @Test
    void saveMove() {
        Move move = new Move(100L, 1005L, 100L, LocalDate.now());
        facade.saveMove(move);
        Optional<Move> results = facade.getMove(move.getUuid());
        Move daoresult = results.get();
        assertEquals(move, daoresult);
        try {
            pdbController.deleteQuery(TableType.MOVES, "uuid", move.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateMove() {
        LocalDate date = LocalDate.now();
        Move move = new Move(100L, 1005L, 100L, date);
        facade.saveMove(move);
        LocalDate updatedDate = LocalDate.now();
        Move updatedMove = new Move(100L, 1005L, 200L, updatedDate);
        Move.Field[] fields = {Move.Field.LOCATION_ID, Move.Field.DATE};
        facade.updateMove(updatedMove, fields);
        Optional<Move> results = facade.getMove(move.getUuid());
        Move daoresult = results.get();
        assertEquals(updatedMove, daoresult);
        try {
            pdbController.deleteQuery(TableType.MOVES, "uuid", move.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteMove() {
        Move move = new Move(100L, 1005L, 100L, LocalDate.now());
        Object[] values = new Object[]{move.getUuid(), move.getNodeID(), move.getLocationID(), move.getDate()};
        try {
            pdbController.insertQuery(TableType.MOVES, moveFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert test data";
        }

        try {
            var result = pdbController.searchQuery(TableType.MOVES, "uuid", move.getUuid());
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to delete test data";
        }

        facade.deleteMove(move);

        try {
            var result = pdbController.searchQuery(TableType.MOVES, "uuid", move.getUuid());
        } catch (PdbController.DatabaseException e) {
            assert true : "Test data deleted successfully";
        }
    }

    @Test
    void getLocationName() {
        LocationName location = new LocationName(100L, "testName", "testName", LocationName.NodeType.HALL);
        Object[] values = new Object[]{location.getUuid(), location.getLongName(), location.getShortName(), location.getNodeType()};
        try {
            pdbController.insertQuery(TableType.LOCATIONNAMES, locationNameFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<LocationName> results = facade.getLocationName(location.getUuid());
        LocationName daoresult = results.get();
        assertEquals(daoresult, location);
        try {
            pdbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", location.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetLocationName() {
        LocationName location = new LocationName(100L, "testName", "testName", LocationName.NodeType.HALL);
        LocationName location2 = new LocationName(101L, "testName", "testName", LocationName.NodeType.HALL);
        Object[] values = new Object[]{location.getUuid(), location.getLongName(), location.getShortName(), location.getNodeType()};
        Object[] values2 = new Object[]{location2.getUuid(), location2.getLongName(), location2.getShortName(), location2.getNodeType()};
        try {
            pdbController.insertQuery(TableType.LOCATIONNAMES, locationNameFields, values);
            pdbController.insertQuery(TableType.LOCATIONNAMES, locationNameFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        var results = facade.getLocationName(LocationName.Field.SHORT_NAME, "testName");
        var map = new HashMap<java.lang.Long, LocationName>();
        try (var rs = pdbController.searchQuery(TableType.LOCATIONNAMES, "shortName", "testName")) {
            while (rs.next()) {
                LocationName req = new LocationName(
                        (java.lang.Long) rs.getObject("uuid"),
                        (java.lang.String) rs.getObject("longName"),
                        (java.lang.String) rs.getObject("shortName"),
                        edu.wpi.punchy_pegasi.schema.LocationName.NodeType.valueOf((String) rs.getObject("nodeType")));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(location.getUuid()), results.get(location.getUuid()));
        assertEquals(map.get(location2.getUuid()), results.get(location2.getUuid()));
        try {
            pdbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", location.getUuid());
            pdbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", location2.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetLocationName1() {
        LocationName location = new LocationName(100L, "testName", "testName", LocationName.NodeType.HALL);
        LocationName location2 = new LocationName(101L, "testName", "testName", LocationName.NodeType.HALL);
        LocationName location3 = new LocationName(102L, "testName1", "testName1", LocationName.NodeType.HALL);
        Object[] values = new Object[]{location.getUuid(), location.getLongName(), location.getShortName(), location.getNodeType()};
        Object[] values2 = new Object[]{location2.getUuid(), location2.getLongName(), location2.getShortName(), location2.getNodeType()};
        Object[] values3 = new Object[]{location3.getUuid(), location3.getLongName(), location3.getShortName(), location3.getNodeType()};
        try {
            pdbController.insertQuery(TableType.LOCATIONNAMES, locationNameFields, values);
            pdbController.insertQuery(TableType.LOCATIONNAMES, locationNameFields, values2);
            pdbController.insertQuery(TableType.LOCATIONNAMES, locationNameFields, values3);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        LocationName.Field[] fields = {LocationName.Field.LONG_NAME, LocationName.Field.SHORT_NAME};
        Object[] searchValues = new Object[]{"testName1", "testName1"};
        String[] searchFields = new String[]{"longName", "shortName"};
        var results = facade.getLocationName(fields, searchValues);
        var map = new HashMap<java.lang.Long, LocationName>();
        try (var rs = pdbController.searchQuery(TableType.LOCATIONNAMES, searchFields, searchValues)) {
            while (rs.next()) {
                LocationName req = new LocationName(
                        (java.lang.Long) rs.getObject("uuid"),
                        (java.lang.String) rs.getObject("longName"),
                        (java.lang.String) rs.getObject("shortName"),
                        edu.wpi.punchy_pegasi.schema.LocationName.NodeType.valueOf((String) rs.getObject("nodeType")));
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(location.getUuid()), results.get(location.getUuid()));
        assertEquals(map.get(location2.getUuid()), results.get(location2.getUuid()));
        assertEquals(map.get(location3.getUuid()), results.get(location3.getUuid()));
        try {
            pdbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", location.getUuid());
            pdbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", location2.getUuid());
            pdbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", location3.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllLocationName() {
        var values0 = new Object[]{100L, "LongtestName0", "testName0", LocationName.NodeType.HALL};
        var values1 = new Object[]{101L, "LongtestName1", "testName1", LocationName.NodeType.HALL};
        var values2 = new Object[]{102L, "LongtestName2", "testName2", LocationName.NodeType.HALL};

        var valueSet = new Object[][]{values0, values1, values2};

        var refMap = new HashMap<Long, LocationName>();
        for (Object[] values : valueSet) {
            var location = new LocationName((Long) values[0], (String) values[1], (String) values[2], (LocationName.NodeType) values[3]);
            refMap.put(location.getUuid(), location);
            try {
                pdbController.insertQuery(TableType.LOCATIONNAMES, locationNameFields, values);
            } catch (PdbController.DatabaseException e) {
                throw new RuntimeException(e);
            }
        }

        Map<Long, LocationName> resultMap = facade.getAllLocationName();
        Map<Long, LocationName> copyMap = new HashMap<>(resultMap);
        assertEquals(refMap, resultMap);
        for (var uuid : copyMap.keySet()) {
            try {
                pdbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", uuid);
            } catch (PdbController.DatabaseException e) {
                assert false : "Failed to delete from database";
            }
        }

    }

    @Test
    void saveLocationName() {
        Long uuid = 100L;
        LocationName ln = new LocationName(uuid, "testName", "testName", LocationName.NodeType.HALL);
        facade.saveLocationName(ln);
        Optional<LocationName> results = facade.getLocationName(ln.getUuid());
        LocationName daoresult = results.get();
        assertEquals(ln, daoresult);
        try {
            pdbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", ln.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateLocationName() {
        Long uuid = 100L;
        LocationName ln = new LocationName(uuid, "testName", "testName", LocationName.NodeType.HALL);
        facade.saveLocationName(ln);
        Optional<LocationName> results = facade.getLocationName(ln.getUuid());
        LocationName daoresult = results.get();
        assertEquals(ln, daoresult);
        try {
            pdbController.deleteQuery(TableType.LOCATIONNAMES, "uuid", ln.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteLocationName() {
        LocationName location = new LocationName(100L, "testLName", "testSName", LocationName.NodeType.HALL);
        Object[] values = new Object[]{location.getUuid(), location.getLongName(), location.getShortName(), location.getNodeType()};
        try {
            pdbController.insertQuery(TableType.LOCATIONNAMES, locationNameFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert into database";
        }
        try {
            pdbController.searchQuery(TableType.LOCATIONNAMES, "uuid", location.getUuid());
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to search database";
        }

        facade.deleteLocationName(location);

        try {
            pdbController.searchQuery(TableType.LOCATIONNAMES, "uuid", location.getUuid());
        } catch (PdbController.DatabaseException e) {
            assert true : "Successfully deleted from database";
        }
    }

    @Test
    void getRequestEntry() {
        var locName = ThreadLocalRandom.current().nextLong();
        var staff = ThreadLocalRandom.current().nextLong();
        RequestEntry request = new RequestEntry(UUID.randomUUID(), locName, staff, "testNotes", RequestEntry.Status.PROCESSING, 100L);
        Object[] values = new Object[]{request.getServiceID(), request.getLocationName(), request.getStaffAssignment(), request.getAdditionalNotes(), request.getStatus(), request.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.REQUESTS, requestFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<RequestEntry> results = facade.getRequestEntry(request.getServiceID());
        RequestEntry daoresult = results.get();
        assertEquals(daoresult.getServiceID(), request.getServiceID());
        try {
            pdbController.deleteQuery(TableType.REQUESTS, "serviceID", request.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRequestEntry() {
        var locName0 = ThreadLocalRandom.current().nextLong();
        var staff0 = ThreadLocalRandom.current().nextLong();
        var locName1 = ThreadLocalRandom.current().nextLong();
        var staff1 = ThreadLocalRandom.current().nextLong();
        var request1 = new RequestEntry(UUID.randomUUID(), locName0, staff0, "testNotes", RequestEntry.Status.PROCESSING, 100L);
        var request2 = new RequestEntry(UUID.randomUUID(), locName1, staff0, "testNotes", RequestEntry.Status.PROCESSING, 100L);
        var values = new Object[]{request1.getServiceID(), request1.getLocationName(), request1.getStaffAssignment(), request1.getAdditionalNotes(), request1.getStatus(), request1.getEmployeeID()};
        var values2 = new Object[]{request2.getServiceID(), request2.getLocationName(), request2.getStaffAssignment(), request2.getAdditionalNotes(), request2.getStatus(), request2.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.REQUESTS, requestFields, values);
            pdbController.insertQuery(TableType.REQUESTS, requestFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        var results = facade.getRequestEntry(RequestEntry.Field.STAFF_ASSIGNMENT, staff0);
        var map = new HashMap<java.util.UUID, RequestEntry>();
        try (var rs = pdbController.searchQuery(TableType.REQUESTS, "staffAssignment", staff0)) {
            while (rs.next()) {
                RequestEntry req = new RequestEntry(
                        (java.util.UUID) rs.getObject("serviceID"),
                        (java.lang.Long) rs.getObject("locationName"),
                        (java.lang.Long) rs.getObject("staffAssignment"),
                        rs.getObject("additionalNotes", String.class),
                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
                        (java.lang.Long) rs.getObject("employeeID"));
                map.put(req.getServiceID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            assert false : e.getMessage();
        }
        assertEquals(map.get(request1.getServiceID()).getServiceID(), results.get(request1.getServiceID()).getServiceID());
        assertEquals(map.get(request2.getServiceID()).getServiceID(), results.get(request2.getServiceID()).getServiceID());
        try {
            pdbController.deleteQuery(TableType.REQUESTS, "serviceID", request1.getServiceID());
            pdbController.deleteQuery(TableType.REQUESTS, "serviceID", request2.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRequestEntry1() {
        var locName0 = ThreadLocalRandom.current().nextLong();
        var staff0 = ThreadLocalRandom.current().nextLong();
        var locName1 = ThreadLocalRandom.current().nextLong();
        var staff1 = ThreadLocalRandom.current().nextLong();
        var request1 = new RequestEntry(UUID.randomUUID(), locName0, staff0, "testNotes1", RequestEntry.Status.PROCESSING, 100L);
        var request2 = new RequestEntry(UUID.randomUUID(), locName1, staff1, "testNotes2", RequestEntry.Status.PROCESSING, 100L);
        var values = new Object[]{request1.getServiceID(), request1.getLocationName(), request1.getStaffAssignment(), request1.getAdditionalNotes(), request1.getStatus(), request1.getEmployeeID()};
        var values2 = new Object[]{request2.getServiceID(), request2.getLocationName(), request2.getStaffAssignment(), request2.getAdditionalNotes(), request2.getStatus(), request2.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.REQUESTS, requestFields, values);
            pdbController.insertQuery(TableType.REQUESTS, requestFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        RequestEntry.Field[] fields = {RequestEntry.Field.ADDITIONAL_NOTES, RequestEntry.Field.STAFF_ASSIGNMENT};
        Object[] searchValues = new Object[]{"testNotes1", staff0};
        String[] searchFields = new String[]{"additionalNotes", "staffAssignment"};
        var results = facade.getRequestEntry(fields, searchValues);
        var map = new HashMap<java.util.UUID, RequestEntry>();
        try (var rs = pdbController.searchQuery(TableType.REQUESTS, searchFields, searchValues)) {
            while (rs.next()) {
                RequestEntry req = new RequestEntry(
                        (java.util.UUID) rs.getObject("serviceID"),
                        (java.lang.Long) rs.getObject("locationName"),
                        (java.lang.Long) rs.getObject("staffAssignment"),
                        rs.getObject("additionalNotes", String.class),
                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
                        (java.lang.Long) rs.getObject("employeeID"));
                map.put(req.getServiceID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            assert false : e.getMessage();
        }
        assertEquals(map.get(request1.getServiceID()), results.get(request1.getServiceID()));
        assertEquals(map.get(request2.getServiceID()), results.get(request2.getServiceID()));
        try {
            pdbController.deleteQuery(TableType.REQUESTS, "serviceID", request1.getServiceID());
            pdbController.deleteQuery(TableType.REQUESTS, "serviceID", request2.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void getAllRequestEntry() {
//        var locName0 = ThreadLocalRandom.current().nextLong();
//        var staff0 = ThreadLocalRandom.current().nextLong();
//        var locName1 = ThreadLocalRandom.current().nextLong();
//        var staff1 = ThreadLocalRandom.current().nextLong();
//        var locName2 = ThreadLocalRandom.current().nextLong();
//        var staff2 = ThreadLocalRandom.current().nextLong();
//        var values0 = new Object[]{UUID.randomUUID(), locName0, staff0, "testNotes", RequestEntry.Status.PROCESSING, 100L};
//        var values1 = new Object[]{UUID.randomUUID(), locName1, staff1, "testNotes", RequestEntry.Status.PROCESSING, 100L};
//        var values2 = new Object[]{UUID.randomUUID(), locName2, staff2, "testNotes", RequestEntry.Status.PROCESSING, 100L};
//        var valuesSet = new Object[][]{values0, values1, values2};
//        var refMap = new HashMap<java.util.UUID, RequestEntry>();
//
//        for (var values : valuesSet) {
//            try {
//                pdbController.insertQuery(TableType.REQUESTS, requestFields, values);
//            } catch (PdbController.DatabaseException e) {
//                throw new RuntimeException(e);
//            }
//            RequestEntry req = new RequestEntry(
//                    (java.util.UUID) values[0],
//                    (java.lang.Long) values[1],
//                    (java.lang.Long) values[2],
//                    (java.lang.String) values[3],
//                    (RequestEntry.Status) values[4],
//                    (java.lang.Long) values[5]);
//            refMap.put(req.getServiceID(), req);
//        }
//
//        Map<UUID, RequestEntry> resultMap = facade.getAllRequestEntry();
//
//        assertEquals(refMap, resultMap);
//
//        for (var key : resultMap.keySet()) {
//            try {
//                pdbController.deleteQuery(TableType.REQUESTS, "serviceID", key);
//            } catch (PdbController.DatabaseException e) {
//                assert false : e.getMessage();
//            }
//        }
//
//    }

    @Test
    void saveRequestEntry() {
        UUID uuid = UUID.randomUUID();
        List<String> additionalItems = new ArrayList<>();
        additionalItems.add("testItems");
        var locName = ThreadLocalRandom.current().nextLong();
        var staff = ThreadLocalRandom.current().nextLong();
        RequestEntry fsre = new RequestEntry(uuid, locName, staff, "testNotes", RequestEntry.Status.PROCESSING, 100L);
        facade.saveRequestEntry(fsre);

        Optional<RequestEntry> results = facade.getRequestEntry(uuid);
        RequestEntry daoresult = results.get();
        assertEquals(fsre.getServiceID(), daoresult.getServiceID());
        try {
            pdbController.deleteQuery(TableType.REQUESTS, "serviceID", uuid);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateRequestEntry() {
        UUID uuid = UUID.randomUUID();
        RequestEntry requestEntry = new RequestEntry(
                uuid,
                100L,
                100L,
                "testNode0",
                RequestEntry.Status.PROCESSING,
                100L
        );
        RequestEntry updatedRequestEntry = new RequestEntry(
                uuid,
                100L,
                100L,
                "testNode1",
                RequestEntry.Status.PROCESSING,
                100L
        );
        try {
            pdbController.insertQuery(TableType.REQUESTS, requestFields, new Object[]{
                    requestEntry.getServiceID(),
                    requestEntry.getLocationName(),
                    requestEntry.getStaffAssignment(),
                    requestEntry.getAdditionalNotes(),
                    requestEntry.getStatus(),
                    requestEntry.getEmployeeID()
            });
        } catch (PdbController.DatabaseException e) {
            assert false : e.getMessage();
        }
        RequestEntry.Field[] updateFields = {
                RequestEntry.Field.ADDITIONAL_NOTES
        };
        facade.updateRequestEntry(updatedRequestEntry, updateFields);
        Optional<RequestEntry> fsrq = facade.getRequestEntry(uuid);
        RequestEntry daoresult = fsrq.get();
        assertEquals(daoresult.getAdditionalNotes(), updatedRequestEntry.getAdditionalNotes());
        try {
            pdbController.deleteQuery(TableType.REQUESTS, "serviceID", uuid);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteRequestEntry() {
        RequestEntry requestEntry = new RequestEntry(
                UUID.randomUUID(),
                ThreadLocalRandom.current().nextLong(),
                ThreadLocalRandom.current().nextLong(),
                "testNode",
                RequestEntry.Status.PROCESSING,
                100L
        );


        var values = new Object[]{
                requestEntry.getServiceID(),
                requestEntry.getLocationName(),
                requestEntry.getStaffAssignment(),
                requestEntry.getAdditionalNotes(),
                requestEntry.getStatus(),
                requestEntry.getEmployeeID()
        };
        try {
            pdbController.insertQuery(TableType.REQUESTS, requestFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert test data";
        }

        try {
            pdbController.searchQuery(TableType.REQUESTS, "serviceID", requestEntry.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to find test data";
        }

        facade.deleteRequestEntry(requestEntry);

        try {
            pdbController.searchQuery(TableType.REQUESTS, "serviceID", requestEntry.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert true : "Successfully deleted test data";
        }
    }

    @Test
    void getFoodServiceRequestEntry() {
        var locName = ThreadLocalRandom.current().nextLong();
        var staff = ThreadLocalRandom.current().nextLong();
        List<String> selectedFoods = new ArrayList<>();
        selectedFoods.add("testItems");
        FoodServiceRequestEntry food = new FoodServiceRequestEntry(UUID.randomUUID(), locName, staff, "testNotes", RequestEntry.Status.PROCESSING, selectedFoods, "testPatient", 100L);
        Object[] values = new Object[]{food.getServiceID(), food.getLocationName(), food.getStaffAssignment(), food.getAdditionalNotes(), food.getStatus(), food.getSelectedFoods(), food.getPatientName(), food.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.FOODREQUESTS, foodServiceFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<FoodServiceRequestEntry> results = facade.getFoodServiceRequestEntry(food.getServiceID());
        FoodServiceRequestEntry daoresult = results.get();
        assertEquals(daoresult.getServiceID(), food.getServiceID());
        try {
            pdbController.deleteQuery(TableType.FOODREQUESTS, "serviceID", food.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void testGetFoodServiceRequestEntry() {
//        List<String> additionalItems = new ArrayList<>();
//        additionalItems.add("testItems");
//        var locName0 = ThreadLocalRandom.current().nextLong();
//        var locName1 = ThreadLocalRandom.current().nextLong();
//        var food = new FoodServiceRequestEntry(UUID.randomUUID(), locName0, 100L, "testNotes", RequestEntry.Status.PROCESSING, "testFood", "testTemp", additionalItems, "juice", "testRestrictions", "testPatient", 100L);
//        var food2 = new FoodServiceRequestEntry(UUID.randomUUID(), locName1, 100L, "testNotes", RequestEntry.Status.PROCESSING, "testFood", "testTemp", additionalItems, "juice", "testRestrictions", "testPatient", 100L);
//        var values = new Object[]{food.getServiceID(), food.getLocationName(), food.getStaffAssignment(), food.getAdditionalNotes(), food.getStatus(), food.getFoodSelection(), food.getTempType(), food.getAdditionalItems(), food.getBeverage(), food.getDietaryRestrictions(), food.getPatientName(), food.getEmployeeID()};
//        var values2 = new Object[]{food2.getServiceID(), food2.getLocationName(), food2.getStaffAssignment(), food2.getAdditionalNotes(), food2.getStatus(), food2.getFoodSelection(), food2.getTempType(), food2.getAdditionalItems(), food2.getBeverage(), food2.getDietaryRestrictions(), food2.getPatientName(), food.getEmployeeID()};
//        try {
//            pdbController.insertQuery(TableType.FOODREQUESTS, foodServiceFields, values);
//            pdbController.insertQuery(TableType.FOODREQUESTS, foodServiceFields, values2);
//        } catch (PdbController.DatabaseException e) {
//            throw new RuntimeException(e);
//        }
//        var results = facade.getFoodServiceRequestEntry(FoodServiceRequestEntry.Field.STAFF_ASSIGNMENT, 100L);
//        var map = new HashMap<java.util.UUID, FoodServiceRequestEntry>();
//        try (var rs = pdbController.searchQuery(TableType.FOODREQUESTS, "staffAssignment", 100L)) {
//            while (rs.next()) {
//                String myCol = rs.getString("additionalItems");
//                List<String> myColumnList = Arrays.asList(myCol.split(","));
//                FoodServiceRequestEntry req = new FoodServiceRequestEntry(
//                        (java.util.UUID) rs.getObject("serviceID"),
//                        (java.lang.Long) rs.getObject("locationName"),
//                        (java.lang.Long) rs.getObject("staffAssignment"),
//                        rs.getObject("additionalNotes", String.class),
//                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
//                        (java.lang.String) rs.getObject("foodSelection"),
//                        (java.lang.String) rs.getObject("tempType"),
//                        myColumnList,
//                        (java.lang.String) rs.getObject("beverage"),
//                        (java.lang.String) rs.getObject("dietaryRestrictions"),
//                        (java.lang.String) rs.getObject("patientName"),
//                        (java.lang.Long) rs.getObject("employeeID"));
//                map.put(req.getServiceID(), req);
//            }
//        } catch (PdbController.DatabaseException | SQLException e) {
//            assert false : e.getMessage();
//        }
//        assertEquals(map.get(food.getServiceID()), results.get(food.getServiceID()));
//        assertEquals(map.get(food2.getServiceID()), results.get(food2.getServiceID()));
//        try {
//            pdbController.deleteQuery(TableType.FOODREQUESTS, "serviceID", food.getServiceID());
//            pdbController.deleteQuery(TableType.FOODREQUESTS, "serviceID", food2.getServiceID());
//        } catch (PdbController.DatabaseException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    @Test
//    void testGetFoodServiceRequestEntry1() {
//        List<String> additionalItems = new ArrayList<>();
//        additionalItems.add("testItems");
//        var locName0 = ThreadLocalRandom.current().nextLong();
//        var staff0 = ThreadLocalRandom.current().nextLong();
//        var locName1 = ThreadLocalRandom.current().nextLong();
//        var staff1 = ThreadLocalRandom.current().nextLong();
//        var food = new FoodServiceRequestEntry(UUID.randomUUID(), locName0, staff0, "testNotes1", RequestEntry.Status.PROCESSING, additionalItems, "testPatient", 100L);
//        var food2 = new FoodServiceRequestEntry(UUID.randomUUID(), locName1, staff1, "testNotes2", RequestEntry.Status.PROCESSING, additionalItems, "testPatient", 100L);
//        var values = new Object[]{food.getServiceID(), food.getLocationName(), food.getStaffAssignment(), food.getAdditionalNotes(), food.getStatus(), food.getSelectedFoods(), food.getPatientName(), food.getEmployeeID()};
//        var values2 = new Object[]{food2.getServiceID(), food2.getLocationName(), food2.getStaffAssignment(), food2.getAdditionalNotes(), food2.getStatus(), food2.getSelectedFoods(), food2.getPatientName(), food2.getEmployeeID()};
//        try {
//            pdbController.insertQuery(TableType.FOODREQUESTS, foodServiceFields, values);
//            pdbController.insertQuery(TableType.FOODREQUESTS, foodServiceFields, values2);
//        } catch (PdbController.DatabaseException e) {
//            throw new RuntimeException(e);
//        }
//        FoodServiceRequestEntry.Field[] fields = {FoodServiceRequestEntry.Field.ADDITIONAL_NOTES, FoodServiceRequestEntry.Field.SELECTED_FOODS};
//        Object[] searchValues = new Object[]{"testNotes1", additionalItems};
//        String[] searchFields = new String[]{"additionalNotes", "selectedFoods"};
//        var results = facade.getFoodServiceRequestEntry(fields, searchValues);
//        var map = new HashMap<java.util.UUID, FoodServiceRequestEntry>();
//        try (var rs = pdbController.searchQuery(TableType.FOODREQUESTS, searchFields, searchValues)) {
//            while (rs.next()) {
//                String myCol = rs.getString("additionalItems");
//                List<String> myColumnList = Arrays.asList(myCol.split(","));
//                FoodServiceRequestEntry req = new FoodServiceRequestEntry(
//                        (java.util.UUID) rs.getObject("serviceID"),
//                        (java.lang.Long) rs.getObject("locationName"),
//                        (java.lang.Long) rs.getObject("staffAssignment"),
//                        rs.getObject("additionalNotes", String.class),
//                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
//                        myColumnList,
//                        (java.lang.String) rs.getObject("patientName"),
//                        (java.lang.Long) rs.getObject("employeeID"));
//                map.put(req.getServiceID(), req);
//            }
//        } catch (PdbController.DatabaseException | SQLException e) {
//            assert false : e.getMessage();
//        }
//        assertEquals(map.get(food.getServiceID()), results.get(food.getServiceID()));
//        assertEquals(map.get(food2.getServiceID()), results.get(food2.getServiceID()));
//        try {
//            pdbController.deleteQuery(TableType.FOODREQUESTS, "serviceID", food.getServiceID());
//            pdbController.deleteQuery(TableType.FOODREQUESTS, "serviceID", food2.getServiceID());
//        } catch (PdbController.DatabaseException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    @Test
//    void getAllFoodServiceRequestEntry() {
//        var locName0 = ThreadLocalRandom.current().nextLong();
//        var staff0 = ThreadLocalRandom.current().nextLong();
//        var locName1 = ThreadLocalRandom.current().nextLong();
//        var staff1 = ThreadLocalRandom.current().nextLong();
//        var locName2 = ThreadLocalRandom.current().nextLong();
//        var staff2 = ThreadLocalRandom.current().nextLong();
//        var additionalItem = "testItems";
//        var values0 = new Object[]{UUID.randomUUID(), locName0, staff0, "testNotes", RequestEntry.Status.PROCESSING, "testFood", "testTemp", List.of(additionalItem), "juice", "testRestrictions", "testPatient", 100L};
//        var values1 = new Object[]{UUID.randomUUID(), locName1, staff1, "testNotes", RequestEntry.Status.PROCESSING, "testFood", "testTemp", List.of(additionalItem), "juice", "testRestrictions", "testPatient", 100L};
//        var values2 = new Object[]{UUID.randomUUID(), locName2, staff2, "testNotes", RequestEntry.Status.PROCESSING, "testFood", "testTemp", List.of(additionalItem), "juice", "testRestrictions", "testPatient", 100L};
//        var valuesSet = new Object[][]{values0, values1, values2};
//        var refMap = new HashMap<java.util.UUID, FoodServiceRequestEntry>();
//
//        for (var values : valuesSet) {
//            try {
//                pdbController.insertQuery(TableType.FOODREQUESTS, foodServiceFields, values);
//            } catch (PdbController.DatabaseException e) {
//                throw new RuntimeException(e);
//            }
//            FoodServiceRequestEntry req = new FoodServiceRequestEntry(
//                    (java.util.UUID) values[0],
//                    (java.lang.Long) values[1],
//                    (java.lang.Long) values[2],
//                    (java.lang.String) values[3],
//                    (RequestEntry.Status) values[4],
//                    (java.lang.String) values[5],
//                    (java.lang.String) values[6],
//                    (List<String>) values[7],
//                    (java.lang.String) values[8],
//                    (java.lang.String) values[9],
//                    (java.lang.String) values[10],
//                    (java.lang.Long) values[11]);
//            refMap.put(req.getServiceID(), req);
//        }
//
//        Map<UUID, FoodServiceRequestEntry> resultMap = facade.getAllFoodServiceRequestEntry();
//
//        for (var key : resultMap.keySet()) {
//            try {
//                pdbController.deleteQuery(TableType.FOODREQUESTS, "serviceID", key);
//            } catch (PdbController.DatabaseException e) {
//                assert false : e.getMessage();
//            }
//        }
//
//        assertEquals(refMap, resultMap);
//    }

    @Test
    void saveFoodServiceRequestEntry() {
        UUID uuid = UUID.randomUUID();
        List<String> additionalItems = new ArrayList<>();
        additionalItems.add("testItems");
        var locName = ThreadLocalRandom.current().nextLong();
        var staff = ThreadLocalRandom.current().nextLong();
        FoodServiceRequestEntry fsre = new FoodServiceRequestEntry(uuid, locName, staff, "testNotes", RequestEntry.Status.PROCESSING, additionalItems, "testPatient", 100L);
        facade.saveFoodServiceRequestEntry(fsre);

        Optional<FoodServiceRequestEntry> results = facade.getFoodServiceRequestEntry(uuid);
        FoodServiceRequestEntry daoresult = results.get();
        assertEquals(fsre, daoresult);
        try {
            pdbController.deleteQuery(TableType.FOODREQUESTS, "serviceID", uuid);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void updateFoodServiceRequestEntry() {
        UUID uuid = UUID.randomUUID();
        FoodServiceRequestEntry foodRequest = new FoodServiceRequestEntry(
                uuid,
                100L,
                100L,
                "testNode0",
                RequestEntry.Status.PROCESSING,
                List.of("item1", "item2"),
                "patientName0",
                100L
        );
        FoodServiceRequestEntry updateFoodRequest = new FoodServiceRequestEntry(
                uuid,
                100L,
                100L,
                "testNode0",
                RequestEntry.Status.PROCESSING,
                List.of("item1", "item2"),
                "patientName0",
                200L
        );
        try {
            pdbController.insertQuery(TableType.FOODREQUESTS, foodServiceFields, new Object[]{
                    foodRequest.getServiceID(),
                    foodRequest.getLocationName(),
                    foodRequest.getStaffAssignment(),
                    foodRequest.getAdditionalNotes(),
                    foodRequest.getStatus(),
                    foodRequest.getSelectedFoods(),
                    foodRequest.getPatientName(),
                    foodRequest.getEmployeeID()
            });
        } catch (PdbController.DatabaseException e) {
            assert false : e.getMessage();
        }
        FoodServiceRequestEntry.Field[] updateFields = {
                FoodServiceRequestEntry.Field.EMPLOYEE_ID
        };
        facade.updateFoodServiceRequestEntry(updateFoodRequest, updateFields);
        Optional<FoodServiceRequestEntry> fsrq = facade.getFoodServiceRequestEntry(uuid);
        FoodServiceRequestEntry daoresult = fsrq.get();
        assertEquals(daoresult.getEmployeeID(), updateFoodRequest.getEmployeeID());
        try {
            pdbController.deleteQuery(TableType.FOODREQUESTS, "serviceID", uuid);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteFoodServiceRequestEntry() {
        FoodServiceRequestEntry foodRequest = new FoodServiceRequestEntry(
                UUID.randomUUID(),
                ThreadLocalRandom.current().nextLong(),
                ThreadLocalRandom.current().nextLong(),
                "testNode",
                RequestEntry.Status.PROCESSING,
                List.of("item1", "item2"),
                "patientName",
                100L
        );


        var values = new Object[]{
                foodRequest.getServiceID(),
                foodRequest.getLocationName(),
                foodRequest.getStaffAssignment(),
                foodRequest.getAdditionalNotes(),
                foodRequest.getStatus(),
                foodRequest.getSelectedFoods(),
                foodRequest.getPatientName(),
                foodRequest.getEmployeeID()
        };
        try {
            pdbController.insertQuery(TableType.FOODREQUESTS, foodServiceFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert test data";
        }

        try {
            pdbController.searchQuery(TableType.FOODREQUESTS, "serviceID", foodRequest.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to find test data";
        }

        facade.deleteFoodServiceRequestEntry(foodRequest);

        try {
            pdbController.searchQuery(TableType.FOODREQUESTS, "serviceID", foodRequest.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert true : "Successfully deleted test data";
        }
    }

    @Test
    void getFlowerDeliveryRequestEntry() {
        var locName = ThreadLocalRandom.current().nextLong();
        var staff = ThreadLocalRandom.current().nextLong();
        List<String> selectedFlowers = new ArrayList<>();
        selectedFlowers.add("testFlowers");
        FlowerDeliveryRequestEntry flowers = new FlowerDeliveryRequestEntry(UUID.randomUUID(), "testPatient", locName, staff, "testNotes", RequestEntry.Status.PROCESSING, selectedFlowers, 100L);
        Object[] values = new Object[]{flowers.getServiceID(), flowers.getPatientName(), flowers.getLocationName(), flowers.getStaffAssignment(), flowers.getAdditionalNotes(), flowers.getStatus(), flowers.getSelectedFlowers(), flowers.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.FLOWERREQUESTS, flowerDeliveryFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<FlowerDeliveryRequestEntry> results = facade.getFlowerDeliveryRequestEntry(flowers.getServiceID());
        FlowerDeliveryRequestEntry daoresult = results.get();
        assertEquals(daoresult.getServiceID(), flowers.getServiceID());
        try {
            pdbController.deleteQuery(TableType.FLOWERREQUESTS, "serviceID", flowers.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void testGetFlowerDeliveryRequestEntry() {
//        var locName0 = ThreadLocalRandom.current().nextLong();
//        var staff0 = ThreadLocalRandom.current().nextLong();
//        var locName1 = ThreadLocalRandom.current().nextLong();
//        var staff1 = ThreadLocalRandom.current().nextLong();
//        List<String> selectedFlowers0 = new ArrayList<>();
//        selectedFlowers0.add("testFlowers0");
//        List<String> selectedFlowers1 = new ArrayList<>();
//        selectedFlowers1.add("testFlowers1");
//        var flowers = new FlowerDeliveryRequestEntry(UUID.randomUUID(), "testPatient", locName0, staff0, "testNotes", RequestEntry.Status.PROCESSING, selectedFlowers0, 100L);
//        var flowers2 = new FlowerDeliveryRequestEntry(UUID.randomUUID(), "testPatient", locName1, staff1, "testNotes", RequestEntry.Status.PROCESSING, selectedFlowers1, 100L);
//        var values = new Object[]{flowers.getServiceID(), flowers.getPatientName(), flowers.getLocationName(), flowers.getStaffAssignment(), flowers.getAdditionalNotes(), flowers.getStatus(), flowers.getSelectedFlowers(), flowers.getEmployeeID()};
//        var values2 = new Object[]{flowers2.getServiceID(), flowers2.getPatientName(), flowers2.getLocationName(), flowers2.getStaffAssignment(), flowers2.getAdditionalNotes(), flowers2.getStatus(), flowers2.getSelectedFlowers(), flowers2.getEmployeeID()};
//        try {
//            pdbController.insertQuery(TableType.FLOWERREQUESTS, flowerDeliveryFields, values);
//            pdbController.insertQuery(TableType.FLOWERREQUESTS, flowerDeliveryFields, values2);
//        } catch (PdbController.DatabaseException e) {
//            throw new RuntimeException(e);
//        }
//        var results = facade.getFlowerDeliveryRequestEntry(FlowerDeliveryRequestEntry.Field.PATIENT_NAME, "testPatient");
//        var map = new HashMap<java.util.UUID, FlowerDeliveryRequestEntry>();
//        try (var rs = pdbController.searchQuery(TableType.FLOWERREQUESTS, "patientName", "testPatient")) {
//            while (rs.next()) {
//                FlowerDeliveryRequestEntry req = new FlowerDeliveryRequestEntry(
//                        (java.util.UUID) rs.getObject("serviceID"),
//                        (java.lang.String) rs.getObject("patientName"),
//                        (java.lang.Long) rs.getObject("locationName"),
//                        (java.lang.Long) rs.getObject("staffAssignment"),
//                        (java.lang.String) rs.getObject("additionalNotes"),
//                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
//                        (java.util.List) rs.getObject("selectedFlowers"),
//                        (java.lang.Long) rs.getObject("employeeID"));
//                if (req != null)
//                    map.put(req.getServiceID(), req);
//            }
//        } catch (PdbController.DatabaseException | SQLException e) {
//            log.error("", e);
//        }
//        assertEquals(map.get(flowers.getServiceID()).getServiceID(), results.get(flowers.getServiceID()).getServiceID());
//        assertEquals(map.get(flowers2.getServiceID()).getServiceID(), results.get(flowers2.getServiceID()).getServiceID());
//        try {
//            pdbController.deleteQuery(TableType.FLOWERREQUESTS, "serviceID", flowers.getServiceID());
//            pdbController.deleteQuery(TableType.FLOWERREQUESTS, "serviceID", flowers2.getServiceID());
//        } catch (PdbController.DatabaseException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Test
    void testGetFlowerDeliveryRequestEntry1() {
        var locName0 = ThreadLocalRandom.current().nextLong();
        var staff0 = ThreadLocalRandom.current().nextLong();
        var locName1 = ThreadLocalRandom.current().nextLong();
        var staff1 = ThreadLocalRandom.current().nextLong();
        List<String> selectedFlowers0 = new ArrayList<>();
        selectedFlowers0.add("testFlowers0");
        List<String> selectedFlowers1 = new ArrayList<>();
        selectedFlowers1.add("testFlowers1");
        var flowers = new FlowerDeliveryRequestEntry(UUID.randomUUID(), "testPatient1", locName0, staff0, "testNotes1", RequestEntry.Status.PROCESSING, selectedFlowers0, 100L);
        var flowers2 = new FlowerDeliveryRequestEntry(UUID.randomUUID(), "testPatient2", locName1, staff1, "testNotes2", RequestEntry.Status.PROCESSING, selectedFlowers1, 100L);
        var values = new Object[]{flowers.getServiceID(), flowers.getPatientName(), flowers.getLocationName(), flowers.getStaffAssignment(), flowers.getAdditionalNotes(), flowers.getStatus(), flowers.getSelectedFlowers(), flowers.getEmployeeID()};
        var values2 = new Object[]{flowers2.getServiceID(), flowers2.getPatientName(), flowers2.getLocationName(), flowers2.getStaffAssignment(), flowers2.getAdditionalNotes(), flowers2.getStatus(), flowers2.getSelectedFlowers(), flowers2.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.FLOWERREQUESTS, flowerDeliveryFields, values);
            pdbController.insertQuery(TableType.FLOWERREQUESTS, flowerDeliveryFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        FlowerDeliveryRequestEntry.Field[] fields = {FlowerDeliveryRequestEntry.Field.PATIENT_NAME, FlowerDeliveryRequestEntry.Field.ADDITIONAL_NOTES};
        Object[] searchValues = new Object[]{"testPatient2", "testNotes1"};
        String[] searchFields = new String[]{"patientName", "additionalNotes"};
        var results = facade.getFlowerDeliveryRequestEntry(fields, searchValues);
        var map = new HashMap<java.util.UUID, FlowerDeliveryRequestEntry>();
        try (var rs = pdbController.searchQuery(TableType.FLOWERREQUESTS, searchFields, searchValues)) {
            while (rs.next()) {
                FlowerDeliveryRequestEntry req = new FlowerDeliveryRequestEntry(
                        (java.util.UUID) rs.getObject("serviceID"),
                        (java.lang.String) rs.getObject("patientName"),
                        (java.lang.Long) rs.getObject("locationName"),
                        (java.lang.Long) rs.getObject("staffAssignment"),
                        (java.lang.String) rs.getObject("additionalNotes"),
                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
                        (java.util.List) rs.getObject("selectedFlowers"),
                        (java.lang.Long) rs.getObject("employeeID"));
                if (req != null)
                    map.put(req.getServiceID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(flowers.getServiceID()), results.get(flowers.getServiceID()));
        assertEquals(map.get(flowers2.getServiceID()), results.get(flowers2.getServiceID()));
        try {
            pdbController.deleteQuery(TableType.FLOWERREQUESTS, "serviceID", flowers.getServiceID());
            pdbController.deleteQuery(TableType.FLOWERREQUESTS, "serviceID", flowers2.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllFlowerDeliveryRequestEntry() {
        var locName0 = ThreadLocalRandom.current().nextLong();
        var staff0 = ThreadLocalRandom.current().nextLong();
        var locName1 = ThreadLocalRandom.current().nextLong();
        var staff1 = ThreadLocalRandom.current().nextLong();
        var locName2 = ThreadLocalRandom.current().nextLong();
        var staff2 = ThreadLocalRandom.current().nextLong();
        List<String> selectedFlowers0 = new ArrayList<>();
        selectedFlowers0.add("testFlowers0");
        List<String> selectedFlowers1 = new ArrayList<>();
        selectedFlowers1.add("testFlowers1");
        List<String> selectedFlowers2 = new ArrayList<>();
        selectedFlowers2.add("testFlowers2");
        var values0 = new Object[]{
                UUID.randomUUID(),
                "testPatient0",
                locName0,
                staff0,
                "testNotes0",
                RequestEntry.Status.PROCESSING,
                selectedFlowers0,
                100L
        };
        var values1 = new Object[]{
                UUID.randomUUID(),
                "testPatient1",
                locName1,
                staff1,
                "testNotes1",
                RequestEntry.Status.PROCESSING,
                selectedFlowers1,
                100L
        };
        var values2 = new Object[]{
                UUID.randomUUID(),
                "testPatient2",
                locName2,
                staff2,
                "testNotes2",
                RequestEntry.Status.PROCESSING,
                selectedFlowers2,
                100L
        };

        var valueSet = new Object[][]{values0, values1, values2};
        var refMap = new HashMap<UUID, FlowerDeliveryRequestEntry>();

        for (var values : valueSet) {
            try {
                pdbController.insertQuery(TableType.FLOWERREQUESTS, flowerDeliveryFields, values);
            } catch (PdbController.DatabaseException e) {
                throw new RuntimeException(e);
            }
            var uuid = (UUID) values[0];
            var patientName = (String) values[1];
            var locationName = (Long) values[2];
            var staffAssignment = (Long) values[3];
            var additionalNotes = (String) values[4];
            var status = (RequestEntry.Status) values[5];
            var selectedFlowers = (List<String>) values[6];
            var employeeID = (Long) values[7];
            var entry = new FlowerDeliveryRequestEntry(uuid, patientName, locationName, staffAssignment, additionalNotes, status, selectedFlowers, employeeID);
            refMap.put(uuid, entry);
        }
        Map<UUID, FlowerDeliveryRequestEntry> resultMap = facade.getAllFlowerDeliveryRequestEntry();
        assertEquals(refMap, resultMap);
        for (var uuid : refMap.keySet()) {
            try {
                pdbController.deleteQuery(TableType.FLOWERREQUESTS, "serviceID", uuid);
            } catch (PdbController.DatabaseException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Test
    void saveFlowerDeliveryRequestEntry() {
        var locName = ThreadLocalRandom.current().nextLong();
        var staff = ThreadLocalRandom.current().nextLong();
        List<String> selectedFlowers = new ArrayList<>();
        selectedFlowers.add("testFlowers");
        UUID uuid = UUID.randomUUID();
        FlowerDeliveryRequestEntry fdre = new FlowerDeliveryRequestEntry(uuid, "testPatient", locName, staff, "testNotes", RequestEntry.Status.PROCESSING, selectedFlowers, 100L);
        facade.saveFlowerDeliveryRequestEntry(fdre);
        Optional<FlowerDeliveryRequestEntry> results = facade.getFlowerDeliveryRequestEntry(uuid);
        FlowerDeliveryRequestEntry daoresult = results.get();
        assertEquals(fdre.getServiceID(), daoresult.getServiceID());
        try {
            pdbController.deleteQuery(TableType.FLOWERREQUESTS, "serviceID", uuid);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateFlowerDeliveryRequestEntry() {
        UUID uuid = UUID.randomUUID();
        List<String> selectedFlowers0 = new ArrayList<>();
        selectedFlowers0.add("testFlowers0");
        List<String> selectedFlowers1 = new ArrayList<>();
        selectedFlowers1.add("testFlowers1");
        FlowerDeliveryRequestEntry fdre = new FlowerDeliveryRequestEntry(uuid, "testPatient", 100L, 200L, "testNotes", RequestEntry.Status.PROCESSING, selectedFlowers0, 100L);
        facade.saveFlowerDeliveryRequestEntry(fdre);

        FlowerDeliveryRequestEntry updatedFdre = new FlowerDeliveryRequestEntry(uuid, "testPatient", 100L, 200L, "testNotes", RequestEntry.Status.PROCESSING, selectedFlowers1, 100L);
        FlowerDeliveryRequestEntry.Field[] fields = {FlowerDeliveryRequestEntry.Field.SELECTED_FLOWERS};
        facade.updateFlowerDeliveryRequestEntry(updatedFdre, fields);

        Optional<FlowerDeliveryRequestEntry> results = facade.getFlowerDeliveryRequestEntry(uuid);
        FlowerDeliveryRequestEntry daoresult = results.get();
        assertEquals(updatedFdre, daoresult);
        try {
            pdbController.deleteQuery(TableType.FLOWERREQUESTS, "serviceID", uuid);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteFlowerDeliveryRequestEntry() {
        var locName = ThreadLocalRandom.current().nextLong();
        var staff = ThreadLocalRandom.current().nextLong();
        List<String> selectedFlowers0 = new ArrayList<>();
        selectedFlowers0.add("testFlowers0");
        List<String> selectedFlowers1 = new ArrayList<>();
        selectedFlowers1.add("testFlowers1");
        FlowerDeliveryRequestEntry flowerEntry =
                new FlowerDeliveryRequestEntry(
                        UUID.randomUUID(),
                        "testPatient",
                        locName,
                        staff,
                        "testNotes",
                        RequestEntry.Status.PROCESSING,
                        selectedFlowers0,
                        100L
                );
        var values = new Object[]{
                flowerEntry.getServiceID(),
                flowerEntry.getPatientName(),
                flowerEntry.getLocationName(),
                flowerEntry.getStaffAssignment(),
                flowerEntry.getAdditionalNotes(),
                flowerEntry.getStatus(),
                flowerEntry.getSelectedFlowers(),
                flowerEntry.getEmployeeID()
        };
        try {
            pdbController.insertQuery(TableType.FLOWERREQUESTS, flowerDeliveryFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Unable to insert flower request";
            log.error("Unable to insert flower request: ", e);
        }

        try {
            ResultSet result = pdbController.searchQuery(TableType.FLOWERREQUESTS, "serviceID", flowerEntry.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert false : "Unable to find flower request";
            throw new RuntimeException(e);
        }

        facade.deleteFlowerDeliveryRequestEntry(flowerEntry);

        try {
            ResultSet result = pdbController.searchQuery(TableType.FLOWERREQUESTS, "serviceID", flowerEntry.getServiceID());
        } catch (PdbController.DatabaseException thrown) {
            assert true : "Flower request was deleted";
            assertTrue(thrown.getMessage().contentEquals("SQL error"));
        }
    }

    @Test
    void getConferenceRoomEntry() {
        var locName = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var staff = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        ConferenceRoomEntry room = new ConferenceRoomEntry(UUID.randomUUID(), locName, staff, "testNotes", RequestEntry.Status.PROCESSING, "testBeginning", "testEnd", LocalDate.now(), "5", 100L);
        Object[] values = new Object[]{room.getServiceID(), room.getLocationName(), room.getStaffAssignment(), room.getAdditionalNotes(), room.getStatus(), room.getBeginningTime(), room.getEndTime(), room.getDate(), room.getAmountOfParticipants(), room.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.CONFERENCEREQUESTS, conferenceRoomFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<ConferenceRoomEntry> results = facade.getConferenceRoomEntry(room.getServiceID());
        ConferenceRoomEntry daoresult = results.get();
        assertEquals(daoresult.getServiceID(), room.getServiceID());
        try {
            pdbController.deleteQuery(TableType.CONFERENCEREQUESTS, "serviceID", room.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetConferenceRoomEntry() {
        var locName0 = ThreadLocalRandom.current().nextLong();
        var staff0 = ThreadLocalRandom.current().nextLong();
        var locName1 = ThreadLocalRandom.current().nextLong();
        var staff1 = ThreadLocalRandom.current().nextLong();
        var room = new ConferenceRoomEntry(UUID.randomUUID(), locName0, staff0, "testNotes", RequestEntry.Status.PROCESSING, "testBeginning", "testEnd", LocalDate.now(), "5", 100L);
        var room2 = new ConferenceRoomEntry(UUID.randomUUID(), locName1, staff1, "testNotes", RequestEntry.Status.PROCESSING, "testBeginning", "testEnd", LocalDate.now(), "5", 100L);
        var values = new Object[]{room.getServiceID(), room.getLocationName(), room.getStaffAssignment(), room.getAdditionalNotes(), room.getStatus(), room.getBeginningTime(), room.getEndTime(), room.getDate(), room.getAmountOfParticipants(), room.getEmployeeID()};
        var values2 = new Object[]{room2.getServiceID(), room2.getLocationName(), room2.getStaffAssignment(), room2.getAdditionalNotes(), room2.getStatus(), room2.getBeginningTime(), room2.getEndTime(), room2.getDate(), room2.getAmountOfParticipants(), room2.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.CONFERENCEREQUESTS, conferenceRoomFields, values);
            pdbController.insertQuery(TableType.CONFERENCEREQUESTS, conferenceRoomFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        var results = facade.getConferenceRoomEntry(ConferenceRoomEntry.Field.LOCATION_NAME, "testRoom");
        var map = new HashMap<java.util.UUID, ConferenceRoomEntry>();
        try (var rs = pdbController.searchQuery(TableType.CONFERENCEREQUESTS, ConferenceRoomEntry.Field.LOCATION_NAME.getColName(), "testRoom")) {
            while (rs.next()) {
                ConferenceRoomEntry req = new ConferenceRoomEntry(
                        (java.util.UUID) rs.getObject("serviceID"),
                        (java.lang.Long) rs.getObject("roomNumber"),
                        (java.lang.Long) rs.getObject("staffAssignment"),
                        (java.lang.String) rs.getObject("additionalNotes"),
                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
                        (java.lang.String) rs.getObject("beginningTime"),
                        (java.lang.String) rs.getObject("endTime"),
                        LocalDate.now(),
                        (java.lang.String) rs.getObject("amountOfParticipants"),
                        (java.lang.Long) rs.getObject("employeeID"));
                if (req != null)
                    map.put(req.getServiceID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(room.getServiceID()), results.get(room.getServiceID()));
        assertEquals(map.get(room2.getServiceID()), results.get(room2.getServiceID()));
        try {
            pdbController.deleteQuery(TableType.CONFERENCEREQUESTS, "serviceID", room.getServiceID());
            pdbController.deleteQuery(TableType.CONFERENCEREQUESTS, "serviceID", room2.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetConferenceRoomEntry1() {
        var locName0 = ThreadLocalRandom.current().nextLong();
        var staff0 = ThreadLocalRandom.current().nextLong();
        var locName1 = ThreadLocalRandom.current().nextLong();
        var staff1 = ThreadLocalRandom.current().nextLong();
        var room = new ConferenceRoomEntry(UUID.randomUUID(), locName0, staff0, "testNotes1", RequestEntry.Status.PROCESSING, "testBeginning1", "testEnd1", LocalDate.now(), "5", 100L);
        var room2 = new ConferenceRoomEntry(UUID.randomUUID(), locName1, staff1, "testNotes2", RequestEntry.Status.DONE, "testBeginning2", "testEnd2", LocalDate.now(), "5", 100L);
        var values = new Object[]{room.getServiceID(), room.getLocationName(), room.getStaffAssignment(), room.getAdditionalNotes(), room.getStatus(), room.getBeginningTime(), room.getEndTime(), room.getDate(), room.getAmountOfParticipants(), room.getEmployeeID()};
        var values2 = new Object[]{room2.getServiceID(), room2.getLocationName(), room2.getStaffAssignment(), room2.getAdditionalNotes(), room2.getStatus(), room2.getBeginningTime(), room2.getEndTime(), room2.getDate(), room2.getAmountOfParticipants(), room2.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.CONFERENCEREQUESTS, conferenceRoomFields, values);
            pdbController.insertQuery(TableType.CONFERENCEREQUESTS, conferenceRoomFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        ConferenceRoomEntry.Field[] fields = {ConferenceRoomEntry.Field.STAFF_ASSIGNMENT, ConferenceRoomEntry.Field.STATUS};
        Object[] searchValues = new Object[]{staff0, ConferenceRoomEntry.Field.STATUS};
        String[] searchFields = new String[]{"staffAssignment", "status"};
        var results = facade.getConferenceRoomEntry(fields, searchValues);
        var map = new HashMap<java.util.UUID, ConferenceRoomEntry>();
        try (var rs = pdbController.searchQuery(TableType.CONFERENCEREQUESTS, searchFields, searchValues)) {
            while (rs.next()) {
                ConferenceRoomEntry req = new ConferenceRoomEntry(
                        (java.util.UUID) rs.getObject("serviceID"),
                        (java.lang.Long) rs.getObject("roomNumber"),
                        (java.lang.Long) rs.getObject("staffAssignment"),
                        (java.lang.String) rs.getObject("additionalNotes"),
                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
                        (java.lang.String) rs.getObject("beginningTime"),
                        (java.lang.String) rs.getObject("endTime"),
                        LocalDate.now(),
                        (java.lang.String) rs.getObject("amountOfParticipants"),
                        (java.lang.Long) rs.getObject("employeeID"));
                if (req != null)
                    map.put(req.getServiceID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(room.getServiceID()), results.get(room.getServiceID()));
        assertEquals(map.get(room2.getServiceID()), results.get(room2.getServiceID()));
        try {
            pdbController.deleteQuery(TableType.CONFERENCEREQUESTS, "serviceID", room.getServiceID());
            pdbController.deleteQuery(TableType.CONFERENCEREQUESTS, "serviceID", room2.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllConferenceRoomEntry() {
        var locName0 = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var staff0 = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var locName1 = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var staff1 = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var locName2 = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var staff2 = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var values0 = new Object[]{
                UUID.randomUUID(),
                locName0,
                staff0,
                "testNotes0",
                RequestEntry.Status.PROCESSING,
                "beginTime0",
                "endTime0",
                LocalDate.now(),
                "5",
                100L
        };
        var values1 = new Object[]{
                UUID.randomUUID(),
                locName1,
                staff1,
                "testNotes1",
                RequestEntry.Status.PROCESSING,
                "beginTime1",
                "endTime1",
                LocalDate.now(),
                "5",
                100L
        };
        var values2 = new Object[]{
                UUID.randomUUID(),
                locName2,
                staff2,
                "testNotes2",
                RequestEntry.Status.PROCESSING,
                "beginTime2",
                "endTime2",
                LocalDate.now(),
                "5",
                100L
        };

        var valuesSet = new Object[][]{values0, values1, values2};
        var refMap = new HashMap<UUID, ConferenceRoomEntry>();

        for (var values : valuesSet) {
            try {
                pdbController.insertQuery(TableType.CONFERENCEREQUESTS, conferenceRoomFields, values);
            } catch (PdbController.DatabaseException e) {
                assert false : e.getMessage();
            }
            var uuid = (UUID) values[0];
            var locName = (java.lang.Long) values[1];
            var staff = (java.lang.Long) values[2];
            var notes = (String) values[3];
            var status = (RequestEntry.Status) values[4];
            var beginTime = (String) values[5];
            var endTime = (String) values[6];
            var date = (LocalDate) values[7];
            var amountOfParticipants = (String) values[8];
            var employeeID = (Long) values[9];
            refMap.put(uuid, new ConferenceRoomEntry(uuid, locName, staff, notes, status, beginTime, endTime, date, amountOfParticipants, employeeID));
        }

        Map<UUID, ConferenceRoomEntry> resultMap = facade.getAllConferenceRoomEntry();
        Map<UUID, ConferenceRoomEntry> copyMap = new HashMap<>(resultMap);
        assertEquals(refMap, resultMap);
        for (var entry : copyMap.entrySet()) {
            try {
                pdbController.deleteQuery(TableType.CONFERENCEREQUESTS, "serviceID", entry.getKey());
            } catch (PdbController.DatabaseException e) {
                assert false : "Failed to delete from database";
            }
        }

    }

    @Test
    void saveConferenceRoomEntry() {
        UUID uuid = UUID.randomUUID();
        var locName = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var staff = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var date = LocalDate.now();
        ConferenceRoomEntry conference = new ConferenceRoomEntry(uuid, locName, staff, "testNotes", RequestEntry.Status.PROCESSING, "testBeginning", "testEnd", date, "5", 100L);
        facade.saveConferenceRoomEntry(conference);
        Optional<ConferenceRoomEntry> results = facade.getConferenceRoomEntry(uuid);
        ConferenceRoomEntry daoresult = results.get();
        assertEquals(conference.getServiceID(), daoresult.getServiceID());
        try {
            pdbController.deleteQuery(TableType.CONFERENCEREQUESTS, "serviceID", uuid);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateConferenceRoomEntry() {
        UUID uuid = UUID.randomUUID();
        var locName = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var staff = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var date = LocalDate.now();
        ConferenceRoomEntry conference = new ConferenceRoomEntry(uuid, locName, staff, "testNotes", RequestEntry.Status.PROCESSING, "testBeginning", "testEnd", date, "5", 100L);
        facade.saveConferenceRoomEntry(conference);

        var updatedLocName = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var updatedStaff = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        ConferenceRoomEntry updatedConference = new ConferenceRoomEntry(uuid, updatedLocName, updatedStaff, "testNotes", RequestEntry.Status.PROCESSING, "testBeginning", "testEnd", date, "5", 100L);
        ConferenceRoomEntry.Field[] fields = {ConferenceRoomEntry.Field.LOCATION_NAME, ConferenceRoomEntry.Field.STAFF_ASSIGNMENT};
        facade.updateConferenceRoomEntry(updatedConference, fields);

        Optional<ConferenceRoomEntry> results = facade.getConferenceRoomEntry(uuid);
        ConferenceRoomEntry daoresult = results.get();
        assertEquals(updatedConference.getLocationName(), daoresult.getLocationName());
        assertEquals(updatedConference.getStaffAssignment(), daoresult.getStaffAssignment());
        try {
            pdbController.deleteQuery(TableType.CONFERENCEREQUESTS, "serviceID", uuid);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteConferenceRoomEntry() {
        var locName = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var staff = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        var date = LocalDate.now();
        ConferenceRoomEntry conferenceRoom =
                new ConferenceRoomEntry(
                        UUID.randomUUID(),
                        locName,
                        staff,
                        "testNotes",
                        RequestEntry.Status.PROCESSING,
                        "beginTime",
                        "endTime",
                        date,
                        "5",
                        100L);

        var values = new Object[]{
                conferenceRoom.getServiceID(),
                conferenceRoom.getLocationName(),
                conferenceRoom.getStaffAssignment(),
                conferenceRoom.getAdditionalNotes(),
                conferenceRoom.getStatus(),
                conferenceRoom.getBeginningTime(),
                conferenceRoom.getEndTime(),
                conferenceRoom.getDate(),
                conferenceRoom.getAmountOfParticipants(),
                conferenceRoom.getEmployeeID()
        };

        try {
            pdbController.insertQuery(TableType.CONFERENCEREQUESTS, conferenceRoomFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert into database";
            log.error("Failed to insert into database", e);
        }

        try {
            ResultSet rs = pdbController.searchQuery(TableType.CONFERENCEREQUESTS, "serviceID", conferenceRoom.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to find the entry in the database";
            log.error("Failed to find the entry in the database", e);
        }

        facade.deleteConferenceRoomEntry(conferenceRoom);

        try {
            ResultSet rs = pdbController.searchQuery(TableType.CONFERENCEREQUESTS, "serviceID", conferenceRoom.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert true : "Successfully deleted the entry from the database";
            assertTrue(e.getMessage().contains("SQL error"));
        }
    }

    @Test
    void getFurnitureRequestEntry() {
        List<String> requestItems = new ArrayList<>();
        requestItems.add("testItems");
        var locName = ThreadLocalRandom.current().nextLong();
        var staff = ThreadLocalRandom.current().nextLong();
        FurnitureRequestEntry furniture = new FurnitureRequestEntry(UUID.randomUUID(), locName, staff, "testNotes", RequestEntry.Status.PROCESSING, requestItems, 100L);
        Object[] values = new Object[]{furniture.getServiceID(), furniture.getLocationName(), furniture.getStaffAssignment(), furniture.getAdditionalNotes(), furniture.getStatus(), furniture.getSelectFurniture(), furniture.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.FURNITUREREQUESTS, furnitureRequestFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<FurnitureRequestEntry> results = facade.getFurnitureRequestEntry(furniture.getServiceID());
        FurnitureRequestEntry daoresult = results.get();
        assertEquals(daoresult.getServiceID(), furniture.getServiceID());
        try {
            pdbController.deleteQuery(TableType.FURNITUREREQUESTS, "serviceID", furniture.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetFurnitureRequestEntry() {
        List<String> requestItems = new ArrayList<>();
        requestItems.add("testItems");
        var locName0 = ThreadLocalRandom.current().nextLong();
        var staff0 = ThreadLocalRandom.current().nextLong();
        var locName1 = ThreadLocalRandom.current().nextLong();
        var staff1 = ThreadLocalRandom.current().nextLong();
        FurnitureRequestEntry furniture = new FurnitureRequestEntry(UUID.randomUUID(), locName0, staff0, "testNotes", RequestEntry.Status.PROCESSING, requestItems, 100L);
        FurnitureRequestEntry furniture2 = new FurnitureRequestEntry(UUID.randomUUID(), locName1, staff1, "testNotes", RequestEntry.Status.PROCESSING, requestItems, 100L);
        Object[] values = new Object[]{furniture.getServiceID(), furniture.getLocationName(), furniture.getStaffAssignment(), furniture.getAdditionalNotes(), furniture.getStatus(), furniture.getSelectFurniture(), furniture.getEmployeeID()};
        Object[] values2 = new Object[]{furniture2.getServiceID(), furniture2.getLocationName(), furniture2.getStaffAssignment(), furniture2.getAdditionalNotes(), furniture2.getStatus(), furniture2.getSelectFurniture(), furniture2.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.FURNITUREREQUESTS, furnitureRequestFields, values);
            pdbController.insertQuery(TableType.FURNITUREREQUESTS, furnitureRequestFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        var results = facade.getFurnitureRequestEntry(FurnitureRequestEntry.Field.ADDITIONAL_NOTES, "testNotes");
        var map = new HashMap<java.util.UUID, FurnitureRequestEntry>();
        try (var rs = pdbController.searchQuery(TableType.FURNITUREREQUESTS, "additionalNotes", "testNotes")) {
            while (rs.next()) {
                FurnitureRequestEntry req = new FurnitureRequestEntry(
                        (java.util.UUID) rs.getObject("serviceID"),
                        (java.lang.Long) rs.getObject("locationName"),
                        (java.lang.Long) rs.getObject("staffAssignment"),
                        (java.lang.String) rs.getObject("additionalNotes"),
                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
                        Arrays.asList((String[]) rs.getArray("selectFurniture").getArray()),
                        (java.lang.Long) rs.getObject("employeeID"));
                if (req != null)
                    map.put(req.getServiceID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(furniture.getServiceID()).getServiceID(), results.get(furniture.getServiceID()).getServiceID());
        assertEquals(map.get(furniture2.getServiceID()).getServiceID(), results.get(furniture2.getServiceID()).getServiceID());
        try {
            pdbController.deleteQuery(TableType.FURNITUREREQUESTS, "serviceID", furniture.getServiceID());
            pdbController.deleteQuery(TableType.FURNITUREREQUESTS, "serviceID", furniture2.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetFurnitureRequestEntry1() {
        List<String> requestItems = new ArrayList<>();
        requestItems.add("testItems");
        var locName0 = ThreadLocalRandom.current().nextLong();
        var staff0 = ThreadLocalRandom.current().nextLong();
        var locName1 = ThreadLocalRandom.current().nextLong();
        var staff1 = ThreadLocalRandom.current().nextLong();
        FurnitureRequestEntry furniture = new FurnitureRequestEntry(UUID.randomUUID(), locName0, staff0, "testNotes1", RequestEntry.Status.PROCESSING, requestItems, 100L);
        FurnitureRequestEntry furniture2 = new FurnitureRequestEntry(UUID.randomUUID(), locName1, staff1, "testNotes2", RequestEntry.Status.PROCESSING, requestItems, 100L);
        Object[] values = new Object[]{furniture.getServiceID(), furniture.getLocationName(), furniture.getStaffAssignment(), furniture.getAdditionalNotes(), furniture.getStatus(), furniture.getSelectFurniture(), furniture.getEmployeeID()};
        Object[] values2 = new Object[]{furniture2.getServiceID(), furniture2.getLocationName(), furniture2.getStaffAssignment(), furniture2.getAdditionalNotes(), furniture2.getStatus(), furniture2.getSelectFurniture(), furniture2.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.FURNITUREREQUESTS, furnitureRequestFields, values);
            pdbController.insertQuery(TableType.FURNITUREREQUESTS, furnitureRequestFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        FurnitureRequestEntry.Field[] fields = {FurnitureRequestEntry.Field.STAFF_ASSIGNMENT, FurnitureRequestEntry.Field.LOCATION_NAME};
        Object[] searchValues = new Object[]{staff0, locName1};
        String[] searchFields = new String[]{"staffAssignment", "locationName"};
        var results = facade.getFurnitureRequestEntry(fields, searchValues);
        var map = new HashMap<java.util.UUID, FurnitureRequestEntry>();
        try (var rs = pdbController.searchQuery(TableType.FURNITUREREQUESTS, searchFields, searchValues)) {
            while (rs.next()) {
                FurnitureRequestEntry req = new FurnitureRequestEntry(
                        (java.util.UUID) rs.getObject("serviceID"),
                        (java.lang.Long) rs.getObject("locationName"),
                        (java.lang.Long) rs.getObject("staffAssignment"),
                        (java.lang.String) rs.getObject("additionalNotes"),
                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
                        Arrays.asList((String[]) rs.getArray("selectFurniture").getArray()),
                        (java.lang.Long) rs.getObject("employeeID"));
                if (req != null)
                    map.put(req.getServiceID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(furniture.getServiceID()), results.get(furniture.getServiceID()));
        assertEquals(map.get(furniture2.getServiceID()), results.get(furniture2.getServiceID()));
        try {
            pdbController.deleteQuery(TableType.FURNITUREREQUESTS, "serviceID", furniture.getServiceID());
            pdbController.deleteQuery(TableType.FURNITUREREQUESTS, "serviceID", furniture2.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllFurnitureRequestEntry() {
        var values0 = new Object[]{
                UUID.randomUUID(),
                ThreadLocalRandom.current().nextLong(),
                ThreadLocalRandom.current().nextLong(),
                "additionalNotes0",
                RequestEntry.Status.PROCESSING,
                List.of("item1", "item2"),
                100L
        };
        var values1 = new Object[]{
                UUID.randomUUID(),
                ThreadLocalRandom.current().nextLong(),
                ThreadLocalRandom.current().nextLong(),
                "additionalNotes1",
                RequestEntry.Status.PROCESSING,
                List.of("item1", "item2"),
                100L
        };
        var values2 = new Object[]{
                UUID.randomUUID(),
                ThreadLocalRandom.current().nextLong(),
                ThreadLocalRandom.current().nextLong(),
                "additionalNotes2",
                RequestEntry.Status.PROCESSING,
                List.of("item1", "item2"),
                100L
        };

        var valuesSet = new Object[][]{
                values0,
                values1,
                values2
        };
        var refMap = new HashMap<UUID, FurnitureRequestEntry>();

        for (Object[] objects : valuesSet) {
            try {
                pdbController.insertQuery(TableType.FURNITUREREQUESTS, furnitureRequestFields, objects);
            } catch (PdbController.DatabaseException e) {
                assert false : "Failed to insert into database";
            }
            var furnRequests = new FurnitureRequestEntry(
                    (UUID) objects[0],
                    (Long) objects[1],
                    (Long) objects[2],
                    (String) objects[3],
                    (RequestEntry.Status) objects[4],
                    (List<String>) objects[5],
                    (Long) objects[6]
            );
            refMap.put(furnRequests.getServiceID(), furnRequests);
        }

        Map<UUID, FurnitureRequestEntry> resultMap = facade.getAllFurnitureRequestEntry();
        assertEquals(refMap, resultMap);
        for (var uuid : refMap.keySet()) {
            try {
                pdbController.deleteQuery(TableType.FURNITUREREQUESTS, "serviceID", uuid);
            } catch (PdbController.DatabaseException e) {
                assert false : "Failed to delete from database";
            }
        }

    }

    @Test
    void saveFurnitureRequestEntry() {
        UUID uuid = UUID.randomUUID();
        List<String> requestItems = new ArrayList<>();
        requestItems.add("testItems");
        var locName = ThreadLocalRandom.current().nextLong();
        var staff = ThreadLocalRandom.current().nextLong();
        FurnitureRequestEntry fdre = new FurnitureRequestEntry(uuid, locName, staff, "testNotes", RequestEntry.Status.PROCESSING, requestItems, 100L);
        facade.saveFurnitureRequestEntry(fdre);
        Optional<FurnitureRequestEntry> results = facade.getFurnitureRequestEntry(uuid);
        FurnitureRequestEntry daoresult = results.get();
        assertEquals(fdre.getServiceID(), daoresult.getServiceID());
        try {
            pdbController.deleteQuery(TableType.FURNITUREREQUESTS, "serviceID", uuid);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateFurnitureRequestEntry() {
        UUID uuid = UUID.randomUUID();
        List<String> requestItems = new ArrayList<>();
        requestItems.add("testItems");
        var locName = ThreadLocalRandom.current().nextLong();
        var staff = ThreadLocalRandom.current().nextLong();
        FurnitureRequestEntry fdre = new FurnitureRequestEntry(uuid, locName, staff, "testNotes", RequestEntry.Status.PROCESSING, requestItems, 100L);
        facade.saveFurnitureRequestEntry(fdre);
        FurnitureRequestEntry updatedFdre = new FurnitureRequestEntry(uuid, locName, staff, "updatedTestNotes", RequestEntry.Status.NONE, requestItems, 100L);
        FurnitureRequestEntry.Field[] fields = {FurnitureRequestEntry.Field.ADDITIONAL_NOTES, FurnitureRequestEntry.Field.STATUS};
        facade.updateFurnitureRequestEntry(updatedFdre, fields);

        Optional<FurnitureRequestEntry> results = facade.getFurnitureRequestEntry(uuid);
        FurnitureRequestEntry daoresult = results.get();
        assertEquals(updatedFdre.getServiceID(), daoresult.getServiceID());
        try {
            pdbController.deleteQuery(TableType.FURNITUREREQUESTS, "serviceID", uuid);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteFurnitureRequestEntry() {
        FurnitureRequestEntry furnitureRequest = new FurnitureRequestEntry(
                UUID.randomUUID(),
                ThreadLocalRandom.current().nextLong(),
                ThreadLocalRandom.current().nextLong(),
                "additionalNotes",
                RequestEntry.Status.PROCESSING,
                List.of("item1", "item2"),
                100L
        );

        var values = new Object[]{
                furnitureRequest.getServiceID(),
                furnitureRequest.getLocationName(),
                furnitureRequest.getStaffAssignment(),
                furnitureRequest.getAdditionalNotes(),
                furnitureRequest.getStatus(),
                furnitureRequest.getSelectFurniture(),
                furnitureRequest.getEmployeeID()
        };

        try {
            pdbController.insertQuery(TableType.FURNITUREREQUESTS, furnitureRequestFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert into database";
        }

        try {
            pdbController.searchQuery(TableType.FURNITUREREQUESTS, "serviceID", furnitureRequest.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to search database";
        }

        facade.deleteFurnitureRequestEntry(furnitureRequest);

        try {
            pdbController.searchQuery(TableType.FURNITUREREQUESTS, "serviceID", furnitureRequest.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert true : "Successfully deleted from database";
        }
    }

    @Test
    void getOfficeServiceRequestEntry() {
        List<String> officeSupplies = new ArrayList<>();
        officeSupplies.add("officeSuppliesTest");
        OfficeServiceRequestEntry office = new OfficeServiceRequestEntry(UUID.randomUUID(), ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong(), "testNotes", RequestEntry.Status.PROCESSING, officeSupplies, 100L);
        Object[] values = new Object[]{office.getServiceID(), office.getLocationName(), office.getStaffAssignment(), office.getAdditionalNotes(), office.getStatus(), office.getOfficeSupplies(), office.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.OFFICEREQUESTS, officeServiceFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<OfficeServiceRequestEntry> results = facade.getOfficeServiceRequestEntry(office.getServiceID());
        OfficeServiceRequestEntry daoresult = results.get();
        assertEquals(daoresult.getServiceID(), office.getServiceID());
        try {
            pdbController.deleteQuery(TableType.OFFICEREQUESTS, "serviceID", office.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void testGetOfficeServiceRequestEntry() {
//        var locName0 = ThreadLocalRandom.current().nextLong();
//        var locName1 = ThreadLocalRandom.current().nextLong();
//        List<String> officeSupplies0 = new ArrayList<>();
//        officeSupplies0.add("officeSuppliesTest0");
//        List<String> officeSupplies1 = new ArrayList<>();
//        officeSupplies1.add("officeSuppliesTest1");
//        var office0 = new OfficeServiceRequestEntry(UUID.randomUUID(), locName0, ThreadLocalRandom.current().nextLong(), "testNotes", RequestEntry.Status.PROCESSING, officeSupplies0, 100L);
//        var office1 = new OfficeServiceRequestEntry(UUID.randomUUID(), locName1, ThreadLocalRandom.current().nextLong(), "testNotes", RequestEntry.Status.PROCESSING, officeSupplies1, 100L);
//        Object[] values0 = new Object[]{office0.getServiceID(), office0.getLocationName(), office0.getStaffAssignment(), office0.getAdditionalNotes(), office0.getStatus(), office0.getOfficeSupplies(), office0.getEmployeeID()};
//        Object[] values1 = new Object[]{office1.getServiceID(), office1.getLocationName(), office1.getStaffAssignment(), office1.getAdditionalNotes(), office1.getStatus(), office1.getOfficeSupplies(), office1. getEmployeeID()};
//        try {
//            pdbController.insertQuery(TableType.OFFICEREQUESTS, officeServiceFields, values0);
//            pdbController.insertQuery(TableType.OFFICEREQUESTS, officeServiceFields, values1);
//        } catch (PdbController.DatabaseException e) {
//            throw new RuntimeException(e);
//        }
//        var results = facade.getOfficeServiceRequestEntry(OfficeServiceRequestEntry.Field.LOCATION_NAME, locName0);
//        var map = new HashMap<UUID, OfficeServiceRequestEntry>();
//        try (var rs = pdbController.searchQuery(TableType.OFFICEREQUESTS, OfficeServiceRequestEntry.Field.LOCATION_NAME.getColName(), locName0)) {
//            while (rs.next()) {
//                var req = new OfficeServiceRequestEntry(
//                        (UUID) rs.getObject("serviceID"),
//                        (Long) rs.getObject("locationName"),
//                        (Long) rs.getObject("staffAssignment"),
//                        (String) rs.getObject("additionalNotes"),
//                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
//                        (List<String>) rs.getObject("officeSupplies"),
//                        (Long) rs.getObject("employeeID"));
//                if (req != null) {
//                    map.put(req.getServiceID(), req);
//                }
//            }
//        } catch (PdbController.DatabaseException | SQLException e) {
//            assert false : e.getMessage();
//        }
//
//        assertEquals(map.get(office0.getServiceID()), results.get(office0.getServiceID()));
//        assertEquals(map.get(office1.getServiceID()), results.get(office1.getServiceID()));
//        try {
//            pdbController.deleteQuery(TableType.OFFICEREQUESTS, "serviceID", office0.getServiceID());
//            pdbController.deleteQuery(TableType.OFFICEREQUESTS, "serviceID", office1.getServiceID());
//        } catch (PdbController.DatabaseException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    @Test
//    void testGetOfficeServiceRequestEntry1() {
//        var locName0 = ThreadLocalRandom.current().nextLong();
//        var locName1 = ThreadLocalRandom.current().nextLong();
//        List<String> officeSupplies0 = new ArrayList<>();
//        officeSupplies0.add("officeSuppliesTest0");
//        List<String> officeSupplies1 = new ArrayList<>();
//        officeSupplies1.add("officeSuppliesTest1");
//        var office0 = new OfficeServiceRequestEntry(UUID.randomUUID(), locName0, ThreadLocalRandom.current().nextLong(), "testNotes", RequestEntry.Status.PROCESSING, officeSupplies0, 100L);
//        var office1 = new OfficeServiceRequestEntry(UUID.randomUUID(), locName1, ThreadLocalRandom.current().nextLong(), "testNotes", RequestEntry.Status.PROCESSING, officeSupplies1, 100L);
//        Object[] values0 = new Object[]{office0.getServiceID(), office0.getLocationName(), office0.getStaffAssignment(), office0.getAdditionalNotes(), office0.getStatus(), office0.getOfficeSupplies(), office0.getEmployeeID()};
//        Object[] values1 = new Object[]{office1.getServiceID(), office1.getLocationName(), office1.getStaffAssignment(), office1.getAdditionalNotes(), office1.getStatus(), office1.getOfficeSupplies(), office1.getEmployeeID()};
//        try {
//            pdbController.insertQuery(TableType.OFFICEREQUESTS, officeServiceFields, values0);
//            pdbController.insertQuery(TableType.OFFICEREQUESTS, officeServiceFields, values1);
//        } catch (PdbController.DatabaseException e) {
//            throw new RuntimeException(e);
//        }
//        OfficeServiceRequestEntry.Field[] fields = {OfficeServiceRequestEntry.Field.OFFICE_SUPPLIES, OfficeServiceRequestEntry.Field.EMPLOYEE_ID};
//        Object[] searchValues = new Object[]{officeSupplies1, 200L};
//        String[] searchFields = new String[]{"officeSupplies", "employeeID"};
//        var results = facade.getOfficeServiceRequestEntry(fields, searchValues);
//        var map = new HashMap<UUID, OfficeServiceRequestEntry>();
//        try (var rs = pdbController.searchQuery(TableType.OFFICEREQUESTS, searchFields, searchValues)) {
//            while (rs.next()) {
//                var req = new OfficeServiceRequestEntry(
//                        (UUID) rs.getObject("serviceID"),
//                        (Long) rs.getObject("locationName"),
//                        (Long) rs.getObject("staffAssignment"),
//                        (String) rs.getObject("additionalNotes"),
//                        edu.wpi.punchy_pegasi.schema.RequestEntry.Status.valueOf((String) rs.getObject("status")),
//                        (List<String>) rs.getObject("officeSupplies"),
//                        (Long) rs.getObject("employeeID"));
//                if (req != null) {
//                    map.put(req.getServiceID(), req);
//                }
//            }
//        } catch (PdbController.DatabaseException | SQLException e) {
//            assert false : e.getMessage();
//        }
//
//        assertEquals(map.get(office0.getServiceID()), results.get(office0.getServiceID()));
//        assertEquals(map.get(office1.getServiceID()), results.get(office1.getServiceID()));
//        try {
//            pdbController.deleteQuery(TableType.OFFICEREQUESTS, "serviceID", office0.getServiceID());
//            pdbController.deleteQuery(TableType.OFFICEREQUESTS, "serviceID", office1.getServiceID());
//        } catch (PdbController.DatabaseException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Test
    void getAllOfficeServiceRequestEntry() {
        List<String> officeSupplies0 = new ArrayList<>();
        officeSupplies0.add("officeSuppliesTest0");
        var value0 = new Object[]{UUID.randomUUID(), ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong(), "testNotes", RequestEntry.Status.PROCESSING, officeSupplies0, 100L};
        var value1 = new Object[]{UUID.randomUUID(), ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong(), "testNotes", RequestEntry.Status.PROCESSING, officeSupplies0, 100L};
        var value2 = new Object[]{UUID.randomUUID(), ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong(), "testNotes", RequestEntry.Status.PROCESSING, officeSupplies0, 100L};
        var valueSet = new Object[][]{value0, value1, value2};

        var refMap = new HashMap<UUID, OfficeServiceRequestEntry>();
        for (var value : valueSet) {
            var office = new OfficeServiceRequestEntry((UUID) value[0], (Long) value[1], (Long) value[2], (String) value[3], (RequestEntry.Status) value[4], (List<String>) value[5], (Long) value[6]);
            refMap.put(office.getServiceID(), office);
            try {
                pdbController.insertQuery(TableType.OFFICEREQUESTS, officeServiceFields, value);
            } catch (PdbController.DatabaseException e) {
                assert false : e.getMessage();
            }
        }
        Map<UUID, OfficeServiceRequestEntry> resultMap = facade.getAllOfficeServiceRequestEntry();
        assertEquals(refMap, resultMap);
        for (var key : refMap.keySet()) {
            try {
                pdbController.deleteQuery(TableType.OFFICEREQUESTS, "serviceID", key);
            } catch (PdbController.DatabaseException e) {
                assert false : e.getMessage();
            }
        }


    }

    @Test
    void saveOfficeServiceRequestEntry() {
        UUID uuid = UUID.randomUUID();
        List<String> officeSupplies0 = new ArrayList<>();
        officeSupplies0.add("officeSuppliesTest0");
        OfficeServiceRequestEntry office = new OfficeServiceRequestEntry(uuid, ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong(), "testNotes", RequestEntry.Status.PROCESSING, officeSupplies0, 100L);
        facade.saveOfficeServiceRequestEntry(office);
        Optional<OfficeServiceRequestEntry> results = facade.getOfficeServiceRequestEntry(office.getServiceID());
        OfficeServiceRequestEntry daoresult = results.get();
        assertEquals(office.getServiceID(), daoresult.getServiceID());
        try {
            pdbController.deleteQuery(TableType.OFFICEREQUESTS, "serviceID", office.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateOfficeServiceRequestEntry() {
        UUID uuid = UUID.randomUUID();
        List<String> officeSupplies0 = new ArrayList<>();
        officeSupplies0.add("officeSuppliesTest0");
        List<String> officeSupplies1 = new ArrayList<>();
        officeSupplies1.add("officeSuppliesTest1");
        OfficeServiceRequestEntry office = new OfficeServiceRequestEntry(uuid, 100L, 100L, "testNotes", RequestEntry.Status.PROCESSING, officeSupplies0, 100L);
        facade.saveOfficeServiceRequestEntry(office);

        OfficeServiceRequestEntry updatedOffice = new OfficeServiceRequestEntry(uuid, 100L, 100L, "testNotes", RequestEntry.Status.DONE, officeSupplies1, 200L);
        OfficeServiceRequestEntry.Field[] fields = {OfficeServiceRequestEntry.Field.LOCATION_NAME, OfficeServiceRequestEntry.Field.STATUS, OfficeServiceRequestEntry.Field.EMPLOYEE_ID};
        facade.updateOfficeServiceRequestEntry(updatedOffice, fields);

        Optional<OfficeServiceRequestEntry> results = facade.getOfficeServiceRequestEntry(office.getServiceID());
        OfficeServiceRequestEntry daoresult = results.get();
        assertEquals(updatedOffice.getStatus(), daoresult.getStatus());
        try {
            pdbController.deleteQuery(TableType.OFFICEREQUESTS, "serviceID", office.getServiceID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteOfficeServiceRequestEntry() {
        List<String> officeSupplies0 = new ArrayList<>();
        officeSupplies0.add("officeSuppliesTest0");
        OfficeServiceRequestEntry office = new OfficeServiceRequestEntry(UUID.randomUUID(), ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong(), "testNotes", RequestEntry.Status.PROCESSING, officeSupplies0, 100L);
        Object[] values = new Object[]{office.getServiceID(), office.getLocationName(), office.getStaffAssignment(), office.getAdditionalNotes(), office.getStatus(), office.getOfficeSupplies(), office.getEmployeeID()};
        try {
            pdbController.insertQuery(TableType.OFFICEREQUESTS, officeServiceFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert into database";
        }

        try {
            pdbController.searchQuery(TableType.OFFICEREQUESTS, "serviceID", office.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to search database";
        }

        facade.deleteOfficeServiceRequestEntry(office);

        try {
            pdbController.searchQuery(TableType.OFFICEREQUESTS, "serviceID", office.getServiceID());
        } catch (PdbController.DatabaseException e) {
            assert true : "Successfully deleted from database";
        }
    }

    @Test
    void getEmployee() {
        Employee employee = new Employee(100L, "testName", "testName");
        Object[] values = new Object[]{employee.getEmployeeID(), employee.getFirstName(), employee.getLastName()};
        try {
            pdbController.insertQuery(TableType.EMPLOYEES, employeeFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<Employee> results = facade.getEmployee(employee.getEmployeeID());
        Employee daoresult = results.get();
        assertEquals(daoresult, employee);
        try {
            pdbController.deleteQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetEmployee() {
        Employee employee = new Employee(100L, "testFirstName", "testLastName");
        Employee employee2 = new Employee(101L, "testFirstName", "testLastName");
        Object[] values = new Object[]{employee.getEmployeeID(), employee.getFirstName(), employee.getLastName()};
        Object[] values2 = new Object[]{employee2.getEmployeeID(), employee2.getFirstName(), employee2.getLastName()};
        try {
            pdbController.insertQuery(TableType.EMPLOYEES, employeeFields, values);
            pdbController.insertQuery(TableType.EMPLOYEES, employeeFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        var results = facade.getEmployee(Employee.Field.FIRST_NAME, "testName");
        var map = new HashMap<Long, Employee>();
        try (var rs = pdbController.searchQuery(TableType.EMPLOYEES, "shortName", "testName")) {
            while (rs.next()) {
                Employee req = new Employee(
                        (java.lang.Long) rs.getObject("employeeID"),
                        (java.lang.String) rs.getObject("firstName"),
                        (java.lang.String) rs.getObject("lastName"));
                if (req != null)
                    map.put(req.getEmployeeID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(employee.getEmployeeID()), results.get(employee.getEmployeeID()));
        assertEquals(map.get(employee2.getEmployeeID()), results.get(employee2.getEmployeeID()));
        try {
            pdbController.deleteQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID());
            pdbController.deleteQuery(TableType.EMPLOYEES, "employeeID", employee2.getEmployeeID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetEmployee1() {
        Employee employee = new Employee(100L, "testFirstName1", "testLastName1");
        Employee employee2 = new Employee(101L, "testFirstName2", "testLastName2");
        Object[] values = new Object[]{employee.getEmployeeID(), employee.getFirstName(), employee.getLastName()};
        Object[] values2 = new Object[]{employee2.getEmployeeID(), employee2.getFirstName(), employee2.getLastName()};
        try {
            pdbController.insertQuery(TableType.EMPLOYEES, employeeFields, values);
            pdbController.insertQuery(TableType.EMPLOYEES, employeeFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Employee.Field[] fields = {Employee.Field.FIRST_NAME, Employee.Field.LAST_NAME};
        Object[] searchValues = new Object[]{"testFirstName1", "testFirstName2"};
        String[] searchFields = new String[]{"staffAssignment", "employeeName"};
        var results = facade.getEmployee(fields, searchValues);
        var map = new HashMap<Long, Employee>();
        try (var rs = pdbController.searchQuery(TableType.EMPLOYEES, searchFields, searchValues)) {
            while (rs.next()) {
                Employee req = new Employee(
                        (java.lang.Long) rs.getObject("employeeID"),
                        (java.lang.String) rs.getObject("firstName"),
                        (java.lang.String) rs.getObject("lastName"));
                if (req != null)
                    map.put(req.getEmployeeID(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(employee.getEmployeeID()), results.get(employee.getEmployeeID()));
        assertEquals(map.get(employee2.getEmployeeID()), results.get(employee2.getEmployeeID()));
        try {
            pdbController.deleteQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID());
            pdbController.deleteQuery(TableType.EMPLOYEES, "employeeID", employee2.getEmployeeID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void getAllEmployee() {
//        var values0 = new Object[]{100L, "LongtestName0", "testName0"};
//        var values1 = new Object[]{101L, "LongtestName1", "testName1"};
//        var values2 = new Object[]{102L, "LongtestName2", "testName2"};
//
//        var valueSet = new Object[][]{values0, values1, values2};
//
//        var refMap = new HashMap<Long, Employee>();
//        for (Object[] values : valueSet) {
//            var employee = new Employee((Long) values[0], (String) values[1], (String) values[2]);
//            refMap.put(employee.getEmployeeID(), employee);
//            try {
//                pdbController.insertQuery(TableType.EMPLOYEES, employeeFields, values);
//            } catch (PdbController.DatabaseException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        Map<Long, Employee> resultMap = facade.getAllEmployee();
//        for (var employeeID : resultMap.keySet()) {
//            try {
//                pdbController.deleteQuery(TableType.EMPLOYEES, "employeeID", employeeID);
//            } catch (PdbController.DatabaseException e) {
//                assert false : "Failed to delete from database";
//            }
//        }
//        assertEquals(refMap, resultMap);
//    }

    @Test
    void saveEmployee() {
        Employee employee = new Employee(100L, "testFirstName", "testLastName");
        facade.saveEmployee(employee);
        Optional<Employee> results = facade.getEmployee(employee.getEmployeeID());
        Employee daoresult = results.get();
        assertEquals(employee, daoresult);
        try {
            pdbController.deleteQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateEmployee() {
        Employee employee = new Employee(100L, "testFirstName", "testLastName");
        facade.saveEmployee(employee);

        Employee updatedEmployee = new Employee(100L, "testFirstName", "updatedTestLastName");
        Employee.Field[] fields = {Employee.Field.LAST_NAME};
        facade.updateEmployee(updatedEmployee, fields);

        Optional<Employee> results = facade.getEmployee(employee.getEmployeeID());
        Employee daoresult = results.get();
        assertEquals(updatedEmployee, daoresult);
        try {
            pdbController.deleteQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteEmployee() {
        Employee employee = new Employee(100L, "testLName", "testSName");
        Object[] values = new Object[]{employee.getEmployeeID(), employee.getFirstName(), employee.getLastName()};
        try {
            pdbController.insertQuery(TableType.EMPLOYEES, employeeFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert into database";
        }
        try {
            pdbController.searchQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID());
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to search database";
        }

        facade.deleteEmployee(employee);

        try {
            pdbController.searchQuery(TableType.EMPLOYEES, "employeeID", employee.getEmployeeID());
        } catch (PdbController.DatabaseException e) {
            assert true : "Successfully deleted from database";
        }
    }

    @Test
    void getAccount() {
        Account account = new Account(100L, "testUsername", "testPassword", 100L, Account.AccountType.ADMIN, Account.Theme.DARK, Account.Accent.DEFAULT);
        Object[] values = new Object[]{account.getUuid(), account.getUsername(), "testPassword", account.getEmployeeID(), account.getAccountType(), account.getTheme(), Account.Accent.DEFAULT};
        try {
            pdbController.insertQuery(TableType.ACCOUNTS, accountFields, values);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Optional<Account> results = facade.getAccount(account.getUuid());
        Account daoresult = results.get();
        assertEquals(daoresult, account);
        try {
            pdbController.deleteQuery(TableType.ACCOUNTS, "uuid", account.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAccount() {
        Account account = new Account(100L, "testUsername", "testPassword", 100L, Account.AccountType.ADMIN, Account.Theme.DARK, Account.Accent.DEFAULT);
        Account account2 = new Account(101L, "testUsername1", "testPassword", 100L, Account.AccountType.ADMIN, Account.Theme.DARK, Account.Accent.DEFAULT);
        Object[] values = new Object[]{account.getUuid(), account.getUsername(), account.getPassword(), account.getEmployeeID(), account.getAccountType(), account.getTheme(), account.getAccent()};
        Object[] values2 = new Object[]{account2.getUuid(), account2.getUsername(), account2.getPassword(), account2.getEmployeeID(), account2.getAccountType(), account2.getTheme(), account2.getAccent()};
        try {
            pdbController.insertQuery(TableType.ACCOUNTS, accountFields, values);
            pdbController.insertQuery(TableType.ACCOUNTS, accountFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        var results = facade.getAccount(Account.Field.EMPLOYEE_ID, 100L);
        var map = new HashMap<Long, Account>();
        try (var rs = pdbController.searchQuery(TableType.ACCOUNTS, "employeeID", 100L)) {
            while (rs.next()) {
                Account req = new Account(
                        (java.lang.Long) rs.getObject("uuid"),
                        (java.lang.String) rs.getObject("username"),
                        (java.lang.String) rs.getObject("password"),
                        (java.lang.Long) rs.getObject("employeeID"),
                        edu.wpi.punchy_pegasi.schema.Account.AccountType.valueOf((String) rs.getObject("accountType")),
                        edu.wpi.punchy_pegasi.schema.Account.Theme.valueOf((String) rs.getObject("theme")),
                        edu.wpi.punchy_pegasi.schema.Account.Accent.valueOf((String) rs.getObject("accent"))
                );
                if (req != null)
                    map.put(req.getUuid(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(account.getUuid()), results.get(account.getUuid()));
        assertEquals(map.get(account2.getUuid()), results.get(account2.getUuid()));
        try {
            pdbController.deleteQuery(TableType.ACCOUNTS, "uuid", account.getUuid());
            pdbController.deleteQuery(TableType.ACCOUNTS, "uuid", account2.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAccount1() {
        Account account = new Account(100L, "testUsername", "testPassword", 100L, Account.AccountType.ADMIN, Account.Theme.DARK, Account.Accent.DEFAULT);
        Account account2 = new Account(101L, "testUsername1", "testPassword", 200L, Account.AccountType.STAFF, Account.Theme.DARK, Account.Accent.DEFAULT);
        Object[] values = new Object[]{account.getUuid(), account.getUsername(), account.getPassword(), account.getEmployeeID(), account.getAccountType(), account.getTheme(), account.getAccent()};
        Object[] values2 = new Object[]{account2.getUuid(), account2.getUsername(), account2.getPassword(), account2.getEmployeeID(), account2.getAccountType(), account2.getTheme(), account2.getAccent()};
        try {
            pdbController.insertQuery(TableType.ACCOUNTS, accountFields, values);
            pdbController.insertQuery(TableType.ACCOUNTS, accountFields, values2);
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
        Account.Field[] fields = {Account.Field.EMPLOYEE_ID, Account.Field.ACCOUNT_TYPE};
        Object[] searchValues = new Object[]{100L, "STAFF"};
        String[] searchFields = new String[]{"employeeID", "accountType"};
        var results = facade.getAccount(fields, searchValues);
        var map = new HashMap<String, Account>();
        try (var rs = pdbController.searchQuery(TableType.ACCOUNTS, searchFields, searchValues)) {
            while (rs.next()) {
                Account req = new Account(
                        (java.lang.Long) rs.getObject("uuid"),
                        (java.lang.String) rs.getObject("username"),
                        (java.lang.String) rs.getObject("password"),
                        (java.lang.Long) rs.getObject("employeeID"),
                        edu.wpi.punchy_pegasi.schema.Account.AccountType.valueOf((String) rs.getObject("accountType")),
                        edu.wpi.punchy_pegasi.schema.Account.Theme.valueOf((String) rs.getObject("theme")),
                        edu.wpi.punchy_pegasi.schema.Account.Accent.valueOf((String) rs.getObject("accent"))
                );
                if (req != null)
                    map.put(req.getUsername(), req);
            }
        } catch (PdbController.DatabaseException | SQLException e) {
            log.error("", e);
        }
        assertEquals(map.get(account.getUsername()), results.get(account.getUsername()));
        assertEquals(map.get(account2.getUsername()), results.get(account2.getUsername()));
        try {
            pdbController.deleteQuery(TableType.ACCOUNTS, "uuid", account.getUuid());
            pdbController.deleteQuery(TableType.ACCOUNTS, "uuid", account2.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void getAllAccount() {
//        var values0 = new Object[]{100L, "username0", "password0", 100L, Account.AccountType.ADMIN};
//        var values1 = new Object[]{101L, "username1", "password1", 101L, Account.AccountType.ADMIN};
//        var values2 = new Object[]{102L, "username2", "password2", 102L, Account.AccountType.ADMIN};
//
//        var valueSet = new Object[][]{values0, values1, values2};
//
//        var refMap = new HashMap<Long, Account>();
//        for (Object[] values : valueSet) {
//            var account = new Account((Long) values[0], (String) values[1], (String) values[2], (Long) values[3], (Account.AccountType) values[4]);
//            refMap.put(account.getUuid(), account);
//            try {
//                pdbController.insertQuery(TableType.ACCOUNTS, accountFields, values);
//            } catch (PdbController.DatabaseException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        Map<Long, Account> resultMap = facade.getAllAccount();
//        for (var username : resultMap.keySet()) {
//            try {
//                pdbController.deleteQuery(TableType.ACCOUNTS, "uuid", username);
//            } catch (PdbController.DatabaseException e) {
//                assert false : "Failed to delete from database";
//            }
//        }
//        assertEquals(refMap, resultMap);
//    }

    @Test
    void saveAccount() {
        Long uuid = 100L;
        Account account = new Account(uuid, "testUsername", "testPassword", 100L, Account.AccountType.ADMIN, Account.Theme.DARK, Account.Accent.DEFAULT);
        facade.saveAccount(account);
        Optional<Account> results = facade.getAccount(account.getUuid());
        Account daoresult = results.get();
        assertEquals(account, daoresult);
        try {
            pdbController.deleteQuery(TableType.ACCOUNTS, "uuid", account.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateAccount() {
        Account account = new Account(100L, "testUsername", "testPassword", 100L, Account.AccountType.ADMIN, Account.Theme.DARK, Account.Accent.DEFAULT);
        facade.saveAccount(account);

        Account updatedAccount = new Account(100L, "testUsername", "testPassword", 100L, Account.AccountType.STAFF, Account.Theme.DARK, Account.Accent.DEFAULT);
        Account.Field[] fields = {Account.Field.ACCOUNT_TYPE};
        facade.updateAccount(updatedAccount, fields);

        Optional<Account> results = facade.getAccount(account.getUuid());
        Account daoresult = results.get();
        assertEquals(updatedAccount, daoresult);
        try {
            pdbController.deleteQuery(TableType.ACCOUNTS, "uuid", account.getUuid());
        } catch (PdbController.DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteAccount() {
        Account account = new Account(100L, "testUsername", "testPassword", 100L, Account.AccountType.ADMIN, Account.Theme.DARK, Account.Accent.DEFAULT);
        Object[] values = new Object[]{account.getUuid(), account.getUsername(), account.getPassword(), account.getEmployeeID(), account.getAccountType(), account.getTheme(), account.getAccent()};
        try {
            pdbController.insertQuery(TableType.ACCOUNTS, accountFields, values);
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to insert into database";
        }
        try {
            pdbController.searchQuery(TableType.ACCOUNTS, "uuid", account.getUuid());
        } catch (PdbController.DatabaseException e) {
            assert false : "Failed to search database";
        }

        facade.deleteAccount(account);

        try {
            pdbController.searchQuery(TableType.ACCOUNTS, "username", account.getUsername());
        } catch (PdbController.DatabaseException e) {
            assert true : "Successfully deleted from database";
        }
    }
}