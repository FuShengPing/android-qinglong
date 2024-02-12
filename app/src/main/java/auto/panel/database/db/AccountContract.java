package auto.panel.database.db;

import android.provider.BaseColumns;

/**
 * @author: ASman
 * @date: 2024/2/4
 * @description:
 */
public final class AccountContract {
    private AccountContract() {}

    public static class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_TOKEN = "token";
        public static final String COLUMN_VERSION = "version";
        public static final String COLUMN_TIME = "time";
    }
}
