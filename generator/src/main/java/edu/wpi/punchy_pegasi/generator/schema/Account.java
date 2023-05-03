package edu.wpi.punchy_pegasi.generator.schema;

import edu.wpi.punchy_pegasi.generator.SchemaID;
import edu.wpi.punchy_pegasi.generator.Unique;
import lombok.*;

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder(toBuilder=true)
public class Account {
    @SchemaID
    private Long uuid;
    @Unique
    private String username;
    private String password;
    private Long employeeID;
    private AccountType accountType;
    private Theme theme;
    private Accent accent;
    public static final Account DEFAULT = new Account(0L, "", "", 0L, Account.AccountType.NONE, Account.Theme.LIGHT, Account.Accent.DEFAULT);

    public enum Theme {
        LIGHT, DARK;
    }
    public enum Accent {
        DEFAULT, PRIDE, WATERMELON, PINK, ORANGE, SUNSET, ZEBRA;
    }

        @RequiredArgsConstructor
        public enum AccountType {
            NONE(0),
            ADMIN(2),
            STAFF(1);
            @Getter
            private final int shieldLevel;
        }

}
