package page.ndser.remotewakeup.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import page.ndser.remotewakeup.model.HistoryDBContract;

public class HistoryDBSQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "wakeup_history_db";

    public HistoryDBSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HistoryDBContract.History.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + HistoryDBContract.History.TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert a new History object into the database.
     *
     * @param hostname Hostname of the machine which was woken up.
     * @param mac MAC address of the machine which was woken up.
     * @param port Port of the machine which was woken up.
     */
    public boolean insertHistory(String hostname, String mac, String port) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HistoryDBContract.History.COLUMN_HOSTNAME, hostname);
        values.put(HistoryDBContract.History.COLUMN_MAC, mac);
        values.put(HistoryDBContract.History.COLUMN_PORT, port);

        long result = db.insert(HistoryDBContract.History.TABLE_NAME, null, values);

        return result != -1; // Check it inserted
    }

    /**
     * View the data from the database to display history.
     *
     * @return Cursor to point and access the database.
     */
    public Cursor viewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + HistoryDBContract.History.TABLE_NAME;
        return db.rawQuery(query, null);
    }
}
