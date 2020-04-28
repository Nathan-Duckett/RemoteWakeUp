package page.ndser.remotewakeup.model;

import android.provider.BaseColumns;

public final class HistoryDBContract {
    private HistoryDBContract() {}

    public static class History implements BaseColumns {
        public static final String TABLE_NAME = "wake_history";
        public static final String COLUMN_HOSTNAME = "hostname";
        public static final String COLUMN_MAC = "mac";
        public static final String COLUMN_PORT = "port";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HOSTNAME + " TEXT, " +
                COLUMN_MAC + " TEXT, " +
                COLUMN_PORT + " TEXT)";
    }
}
