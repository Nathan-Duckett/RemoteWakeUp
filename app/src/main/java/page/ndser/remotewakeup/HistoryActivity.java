package page.ndser.remotewakeup;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.snackbar.Snackbar;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import page.ndser.remotewakeup.model.HistoryDBContract;
import page.ndser.remotewakeup.model.WakeupHistory;
import page.ndser.remotewakeup.util.HistoryDBSQLiteHelper;
import page.ndser.remotewakeup.util.SendWakeup;

/**
 * Represents HistoryActivity to display previous sent wake up packets.
 *
 * @author Nathan Duckett
 */
public class HistoryActivity extends AppCompatActivity {
    private HistoryDBSQLiteHelper db;

    private ListView historyList;

    private ArrayList<WakeupHistory> historyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Set dark mode by default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        historyList = findViewById(R.id.history_list);

        db = new HistoryDBSQLiteHelper(this);
        historyItems = new ArrayList<>();
        displayData();

        historyList.setOnItemClickListener((adapterView, view, i, l) -> {
            String text = historyList.getItemAtPosition(i).toString();
            WakeupHistory entry = (WakeupHistory) historyList.getItemAtPosition(i);

            new Thread(() -> {
                try {
                    SendWakeup.to(entry);
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                }
            }).start();

            Snackbar notification = Snackbar.make(view, R.string.sent_message, Snackbar.LENGTH_SHORT);
            notification.show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home_button) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Display the data into the Activity view.
     */
    private void displayData() {
        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0) {
            // No history then redirect to send wakeup
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        } else {
            while (cursor.moveToNext()) {
                String friendlyName = getValue(cursor, HistoryDBContract.History.COLUMN_FRIENDLY_NAME);
                String hostname = getValue(cursor, HistoryDBContract.History.COLUMN_HOSTNAME);
                String mac = getValue(cursor, HistoryDBContract.History.COLUMN_MAC);
                String port = getValue(cursor, HistoryDBContract.History.COLUMN_PORT);
                WakeupHistory entry = new WakeupHistory(friendlyName, mac, hostname, port);
                // Only add if it is not already in the list.
                if (!historyItems.contains(entry)) {
                    historyItems.add(entry);
                }
            }

            ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.history_list_item, historyItems);
            historyList.setAdapter(adapter);
        }
    }

    /**
     * Get a string value from the DB.
     *
     * @param cursor Cursor to access the content of the DB.
     * @param columnName Column name to retrieve the data from.
     * @return String contained within the DB entry.
     */
    private String getValue(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }
}
