package edu.wpi.punchy_pegasi.schema;

import com.jsoniter.annotation.JsonProperty;
import edu.wpi.punchy_pegasi.backend.SchemaID;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.jsoniter.annotation.JsonCreator;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder = true)
public class Employee {
    @SchemaID
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("employeeid")
    private Long employeeID;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("firstname")
    private String firstName;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("lastname")
    private String lastName;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @lombok.RequiredArgsConstructor
    public enum Field implements IField<edu.wpi.punchy_pegasi.schema.Employee> {
        EMPLOYEE_ID("employeeID", true, false),
        FIRST_NAME("firstName", false, false),
        LAST_NAME("lastName", false, false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;

        public Object getValue(edu.wpi.punchy_pegasi.schema.Employee ref) {
            return ref.getFromField(this);
        }

        public String getValueAsString(edu.wpi.punchy_pegasi.schema.Employee ref) {
            return ref.getFromFieldAsString(this);
        }

        public void setValueFromString(edu.wpi.punchy_pegasi.schema.Employee ref, String value) {
            ref.setFieldFromString(this, value);
        }

        public int oridinal() {
            return ordinal();
        }
    }

    public Object getFromField(Field field) {
        return switch (field) {
            case EMPLOYEE_ID -> getEmployeeID();
            case FIRST_NAME -> getFirstName();
            case LAST_NAME -> getLastName();
        };
    }

    public void setFieldFromString(Field field, String value) {
        switch (field) {
            case EMPLOYEE_ID -> setEmployeeID(Long.parseLong(value));
            case FIRST_NAME -> setFirstName(value);
            case LAST_NAME -> setLastName(value);
        }
    }

    public String getFromFieldAsString(Field field) {
        return switch (field) {
            case EMPLOYEE_ID -> Long.toString(getEmployeeID());
            case FIRST_NAME -> getFirstName();
            case LAST_NAME -> getLastName();
        };
    }

}