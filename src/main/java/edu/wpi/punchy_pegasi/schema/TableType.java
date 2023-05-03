package edu.wpi.punchy_pegasi.schema;

import lombok.Getter;
import org.intellij.lang.annotations.Language;

public enum TableType {
    NODES(edu.wpi.punchy_pegasi.schema.Node.class, """
DO $$
BEGIN
  IF to_regclass('nodes') IS NULL THEN
    CREATE SEQUENCE nodes_id_seq;
    CREATE TABLE nodes
    (
      nodeID bigint DEFAULT nextval('nodes_id_seq') PRIMARY KEY,
      xcoord int,
      ycoord int,
      floor varchar,
      building varchar
    );
    ALTER SEQUENCE nodes_id_seq OWNED BY nodes.nodeID;
  END IF;
END $$;
CREATE OR REPLACE FUNCTION notify_nodes_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'NODES', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('nodes_update',output::text);
    
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_nodes_update
  AFTER INSERT OR UPDATE OR DELETE
  ON nodes
  FOR EACH ROW
  EXECUTE PROCEDURE notify_nodes_update();

""", edu.wpi.punchy_pegasi.schema.Node.Field.class),
    EDGES(edu.wpi.punchy_pegasi.schema.Edge.class, """
CREATE TABLE IF NOT EXISTS edges
(
  uuid uuid DEFAULT gen_random_uuid() PRIMARY KEY,
  startNode bigint,
  endNode bigint
);
CREATE OR REPLACE FUNCTION notify_edges_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'EDGES', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('edges_update',output::text);
    
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_edges_update
  AFTER INSERT OR UPDATE OR DELETE
  ON edges
  FOR EACH ROW
  EXECUTE PROCEDURE notify_edges_update();

""", edu.wpi.punchy_pegasi.schema.Edge.Field.class)
,
    MOVES(edu.wpi.punchy_pegasi.schema.Move.class, """
DO $$
BEGIN
  IF to_regclass('moves') IS NULL THEN
    CREATE SEQUENCE moves_id_seq;
    CREATE TABLE moves
    (
      uuid bigint DEFAULT nextval('moves_id_seq') PRIMARY KEY,
      nodeID bigint,
      locationID bigint,
      date date NOT NULL
    );
    ALTER SEQUENCE moves_id_seq OWNED BY moves.uuid;
  END IF;
END $$;
CREATE OR REPLACE FUNCTION notify_moves_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'MOVES', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('moves_update',output::text);
    
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_moves_update
  AFTER INSERT OR UPDATE OR DELETE
  ON moves
  FOR EACH ROW
  EXECUTE PROCEDURE notify_moves_update();

""", edu.wpi.punchy_pegasi.schema.Move.Field.class),
    LOCATIONNAMES(edu.wpi.punchy_pegasi.schema.LocationName.class, """
DO $$
BEGIN
  IF to_regclass('locationnames') IS NULL THEN
    CREATE SEQUENCE locationnames_id_seq;
    CREATE TABLE locationnames
    (
      uuid bigint DEFAULT nextval('locationnames_id_seq') PRIMARY KEY,
      longName varchar,
      shortName varchar,
      nodeType varchar NOT NULL
    );
    ALTER SEQUENCE locationnames_id_seq OWNED BY locationnames.uuid;
  END IF;
END $$;
CREATE OR REPLACE FUNCTION notify_locationnames_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'LOCATIONNAMES', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('locationnames_update',output::text);
    
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_locationnames_update
  AFTER INSERT OR UPDATE OR DELETE
  ON locationnames
  FOR EACH ROW
  EXECUTE PROCEDURE notify_locationnames_update();

""", edu.wpi.punchy_pegasi.schema.LocationName.Field.class),
    REQUESTS(edu.wpi.punchy_pegasi.schema.RequestEntry.class, """
CREATE TABLE IF NOT EXISTS requests
(
  serviceID uuid DEFAULT gen_random_uuid() PRIMARY KEY,
  locationName bigint,
  staffAssignment bigint,
  additionalNotes varchar,
  status varchar NOT NULL,
  employeeID bigint
);
CREATE OR REPLACE FUNCTION notify_requests_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'REQUESTS', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('requests_update',output::text);
    
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_requests_update
  AFTER INSERT OR UPDATE OR DELETE
  ON requests
  FOR EACH ROW
  EXECUTE PROCEDURE notify_requests_update();

""", edu.wpi.punchy_pegasi.schema.RequestEntry.Field.class)
,
    GENERIC(edu.wpi.punchy_pegasi.schema.GenericRequestEntry.class, """
CREATE TABLE IF NOT EXISTS generic
(
  
) INHERITS (requests);
CREATE OR REPLACE FUNCTION notify_generic_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'GENERIC', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('generic_update',output::text);
    PERFORM pg_notify('requests_update',output::text);
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_generic_update
  AFTER INSERT OR UPDATE OR DELETE
  ON generic
  FOR EACH ROW
  EXECUTE PROCEDURE notify_generic_update();

""", edu.wpi.punchy_pegasi.schema.GenericRequestEntry.Field.class)
,
    FOODREQUESTS(edu.wpi.punchy_pegasi.schema.FoodServiceRequestEntry.class, """
CREATE TABLE IF NOT EXISTS foodrequests
(
  selectedFoods varchar ARRAY,
  patientName varchar
) INHERITS (requests);
CREATE OR REPLACE FUNCTION notify_foodrequests_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'FOODREQUESTS', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('foodrequests_update',output::text);
    PERFORM pg_notify('requests_update',output::text);
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_foodrequests_update
  AFTER INSERT OR UPDATE OR DELETE
  ON foodrequests
  FOR EACH ROW
  EXECUTE PROCEDURE notify_foodrequests_update();

""", edu.wpi.punchy_pegasi.schema.FoodServiceRequestEntry.Field.class)
,
    FLOWERREQUESTS(edu.wpi.punchy_pegasi.schema.FlowerDeliveryRequestEntry.class, """
CREATE TABLE IF NOT EXISTS flowerrequests
(
  selectedFlowers varchar ARRAY,
  patientName varchar
) INHERITS (requests);
CREATE OR REPLACE FUNCTION notify_flowerrequests_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'FLOWERREQUESTS', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('flowerrequests_update',output::text);
    PERFORM pg_notify('requests_update',output::text);
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_flowerrequests_update
  AFTER INSERT OR UPDATE OR DELETE
  ON flowerrequests
  FOR EACH ROW
  EXECUTE PROCEDURE notify_flowerrequests_update();

""", edu.wpi.punchy_pegasi.schema.FlowerDeliveryRequestEntry.Field.class)
,
    CONFERENCEREQUESTS(edu.wpi.punchy_pegasi.schema.ConferenceRoomEntry.class, """
CREATE TABLE IF NOT EXISTS conferencerequests
(
  beginningTime varchar,
  endTime varchar,
  date date NOT NULL,
  amountOfParticipants varchar
) INHERITS (requests);
CREATE OR REPLACE FUNCTION notify_conferencerequests_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'CONFERENCEREQUESTS', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('conferencerequests_update',output::text);
    PERFORM pg_notify('requests_update',output::text);
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_conferencerequests_update
  AFTER INSERT OR UPDATE OR DELETE
  ON conferencerequests
  FOR EACH ROW
  EXECUTE PROCEDURE notify_conferencerequests_update();

""", edu.wpi.punchy_pegasi.schema.ConferenceRoomEntry.Field.class)
,
    FURNITUREREQUESTS(edu.wpi.punchy_pegasi.schema.FurnitureRequestEntry.class, """
CREATE TABLE IF NOT EXISTS furniturerequests
(
  selectFurniture varchar ARRAY
) INHERITS (requests);
CREATE OR REPLACE FUNCTION notify_furniturerequests_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'FURNITUREREQUESTS', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('furniturerequests_update',output::text);
    PERFORM pg_notify('requests_update',output::text);
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_furniturerequests_update
  AFTER INSERT OR UPDATE OR DELETE
  ON furniturerequests
  FOR EACH ROW
  EXECUTE PROCEDURE notify_furniturerequests_update();

""", edu.wpi.punchy_pegasi.schema.FurnitureRequestEntry.Field.class)
,
    OFFICEREQUESTS(edu.wpi.punchy_pegasi.schema.OfficeServiceRequestEntry.class, """
CREATE TABLE IF NOT EXISTS officerequests
(
  officeSupplies varchar ARRAY
) INHERITS (requests);
CREATE OR REPLACE FUNCTION notify_officerequests_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'OFFICEREQUESTS', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('officerequests_update',output::text);
    PERFORM pg_notify('requests_update',output::text);
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_officerequests_update
  AFTER INSERT OR UPDATE OR DELETE
  ON officerequests
  FOR EACH ROW
  EXECUTE PROCEDURE notify_officerequests_update();

""", edu.wpi.punchy_pegasi.schema.OfficeServiceRequestEntry.Field.class)
,
    EMPLOYEES(edu.wpi.punchy_pegasi.schema.Employee.class, """
DO $$
BEGIN
  IF to_regclass('employees') IS NULL THEN
    CREATE SEQUENCE employees_id_seq;
    CREATE TABLE employees
    (
      employeeID bigint DEFAULT nextval('employees_id_seq') PRIMARY KEY,
      firstName varchar,
      lastName varchar
    );
    ALTER SEQUENCE employees_id_seq OWNED BY employees.employeeID;
  END IF;
END $$;
CREATE OR REPLACE FUNCTION notify_employees_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'EMPLOYEES', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('employees_update',output::text);
    
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_employees_update
  AFTER INSERT OR UPDATE OR DELETE
  ON employees
  FOR EACH ROW
  EXECUTE PROCEDURE notify_employees_update();

""", edu.wpi.punchy_pegasi.schema.Employee.Field.class),
    ACCOUNTS(edu.wpi.punchy_pegasi.schema.Account.class, """
DO $$
BEGIN
  IF to_regclass('accounts') IS NULL THEN
    CREATE SEQUENCE accounts_id_seq;
    CREATE TABLE accounts
    (
      uuid bigint DEFAULT nextval('accounts_id_seq') PRIMARY KEY,
      username varchar UNIQUE,
      password varchar,
      employeeID bigint,
      accountType varchar NOT NULL,
      theme varchar NOT NULL,
      accent varchar NOT NULL,
      DEFAULT null
    );
    ALTER SEQUENCE accounts_id_seq OWNED BY accounts.uuid;
  END IF;
END $$;
CREATE OR REPLACE FUNCTION notify_accounts_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'ACCOUNTS', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('accounts_update',output::text);
    
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_accounts_update
  AFTER INSERT OR UPDATE OR DELETE
  ON accounts
  FOR EACH ROW
  EXECUTE PROCEDURE notify_accounts_update();

""", edu.wpi.punchy_pegasi.schema.Account.Field.class),
    SIGNAGE(edu.wpi.punchy_pegasi.schema.Signage.class, """
DO $$
BEGIN
  IF to_regclass('signage') IS NULL THEN
    CREATE SEQUENCE signage_id_seq;
    CREATE TABLE signage
    (
      uuid bigint DEFAULT nextval('signage_id_seq') PRIMARY KEY,
      signName varchar,
      longName varchar,
      directionType varchar NOT NULL
    );
    ALTER SEQUENCE signage_id_seq OWNED BY signage.uuid;
  END IF;
END $$;
CREATE OR REPLACE FUNCTION notify_signage_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'SIGNAGE', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('signage_update',output::text);
    
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_signage_update
  AFTER INSERT OR UPDATE OR DELETE
  ON signage
  FOR EACH ROW
  EXECUTE PROCEDURE notify_signage_update();

""", edu.wpi.punchy_pegasi.schema.Signage.Field.class),
    ALERT(edu.wpi.punchy_pegasi.schema.Alert.class, """
CREATE TABLE IF NOT EXISTS alert
(
  uuid uuid DEFAULT gen_random_uuid() PRIMARY KEY,
  alertType varchar NOT NULL,
  alertTitle varchar,
  description varchar,
  startDate timestamptz NOT NULL,
  endDate timestamptz NOT NULL,
  readStatus varchar NOT NULL,
  employeeID bigint,
  nodeID bigint
);
CREATE OR REPLACE FUNCTION notify_alert_update() RETURNS TRIGGER AS $$
    DECLARE
        row RECORD;
    output JSONB;
    BEGIN
    IF (TG_OP = 'DELETE') THEN
      row = OLD;
    ELSE
      row = NEW;
    END IF;
    -- encode data as json inside a string
    output = jsonb_build_object('tableType', 'ALERT', 'action', TG_OP, 'data', to_json(row_to_json(row)::text));
    PERFORM pg_notify('alert_update',output::text);
    
    RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE TRIGGER trigger_alert_update
  AFTER INSERT OR UPDATE OR DELETE
  ON alert
  FOR EACH ROW
  EXECUTE PROCEDURE notify_alert_update();

""", edu.wpi.punchy_pegasi.schema.Alert.Field.class)
;
    @Getter
    private final Class<?> clazz;
    @Getter
    private final String tableSQL;
    @Getter
    private final Class<?> fieldEnum;

    TableType(Class<?> clazz, @Language(value = "SQL") String tableSQL, Class<?>fieldEnum) {
        this.clazz = clazz;
        this.tableSQL = tableSQL;
        this.fieldEnum = fieldEnum;
    }
}
