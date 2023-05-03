package edu.wpi.punchy_pegasi.schema;

import edu.wpi.punchy_pegasi.backend.SchemaID;
import edu.wpi.punchy_pegasi.backend.Unique;
import lombok.*;

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder = true)
public class Account {
    @SchemaID
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("uuid")
    private Long uuid;
    @Unique
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("username")
    private String username;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("password")
    private String password;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("employeeid")
    private Long employeeID;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("accounttype")
    private AccountType accountType;
    @lombok.With
    @com.jsoniter.annotation.JsonProperty("theme")
    private Theme theme;

    public Object getFromField(Field field) {
        return switch (field) {
            case UUID -> getUuid();
            case USERNAME -> getUsername();
            case PASSWORD -> getPassword();
            case EMPLOYEE_ID -> getEmployeeID();
            case ACCOUNT_TYPE -> getAccountType();
            case THEME -> getTheme();
        };
    }

    @RequiredArgsConstructor
    public enum AccountType {
        NONE(0),
        ADMIN(2),
        STAFF(1);
        @Getter
        private final int shieldLevel;
    }

    @lombok.RequiredArgsConstructor
    public enum Field implements IField<edu.wpi.punchy_pegasi.schema.Account, edu.wpi.punchy_pegasi.schema.Account.AccountBuilder> {
        UUID("uuid", true, false),
        USERNAME("username", false, true),
        PASSWORD("password", false, false),
        EMPLOYEE_ID("employeeID", false, false),
        ACCOUNT_TYPE("accountType", false, false),
        THEME("theme", false, false);
        @lombok.Getter
        private final String colName;
        @lombok.Getter
        private final boolean primaryKey;
        @lombok.Getter
        private final boolean unique;

        public Object getValue(edu.wpi.punchy_pegasi.schema.Account ref) {
            return ref.getFromField(this);
        }

        public String getValueAsString(edu.wpi.punchy_pegasi.schema.Account ref) {
            return ref.getFromFieldAsString(this);
        }

        public void setValueFromString(edu.wpi.punchy_pegasi.schema.Account.AccountBuilder builder, String value) {
            switch (this) {
                case UUID -> builder.uuid(Long.parseLong(value));
                case USERNAME -> builder.username(value);
                case PASSWORD -> builder.password(value);
                case EMPLOYEE_ID -> builder.employeeID(Long.parseLong(value));
                case ACCOUNT_TYPE -> builder.accountType(AccountType.valueOf(value));
                case THEME -> builder.theme(Theme.valueOf(value));
            }
        }

        public int oridinal() {
            return ordinal();
        }
    }

    public String getFromFieldAsString(Field field) {
        return switch (field) {
            case UUID -> Long.toString(getUuid());
            case USERNAME -> getUsername();
            case PASSWORD -> getPassword();
            case EMPLOYEE_ID -> Long.toString(getEmployeeID());
            case ACCOUNT_TYPE -> getAccountType().name();
            case THEME -> getTheme().name();
        };
    }

    public enum Theme {
        LIGHT, DARK
    }

}