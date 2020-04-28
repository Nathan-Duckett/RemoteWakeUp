package page.ndser.remotewakeup;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
            // Display empty count
        } else {
            while (cursor.moveToNext()) {
                String hostname = getValue(cursor, HistoryDBContract.History.COLUMN_HOSTNAME);
                String mac = getValue(cursor, HistoryDBContract.History.COLUMN_MAC);
                String port = getValue(cursor, HistoryDBContract.History.COLUMN_PORT);
                historyItems.add(new WakeupHistory(mac, hostname, port));
            }

            ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyItems);
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
