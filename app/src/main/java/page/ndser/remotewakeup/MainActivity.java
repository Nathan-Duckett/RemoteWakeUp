package page.ndser.remotewakeup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import page.ndser.remotewakeup.model.HistoryDBContract;
import page.ndser.remotewakeup.model.WakeupHistory;
import page.ndser.remotewakeup.util.HistoryDBSQLiteHelper;
import page.ndser.remotewakeup.util.SendWakeup;

/**
 * Represents the MainActivity for the application. Can process waking up the system.
 *
 * @author Nathan Duckett
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set dark mode by default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        findViewById(R.id.button).setOnClickListener(this::wakeup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.history_button) {

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Wakeup the system. Take the content from the text edit fields and send the request to wake up.
     *
     * @param view Android view from the application.
     */
    public void wakeup(View view) {
        // Get values from app screen
        String hostname = getTextContents(R.id.hostnameInput);
        String mac = getTextContents(R.id.macInput);
        String port = getTextContents(R.id.portInput);


        // Verify contents first.
        if (!verifyHostname(hostname)) {
            alert(view, R.string.host_fail_message);
            return;
        }
        if (!verifyMac(mac)) {
            alert(view, R.string.mac_fail_message);
            return;
        }
        if (!verifyPort(port)) {
            alert(view, R.string.port_fail_message);
            return;
        }


        // Create object with values
        WakeupHistory details = new WakeupHistory(mac, hostname, port);

        // Chuck it onto a new thread -> No response so who cares
        new Thread(() -> {
            // Send wakeup request
            try {
                SendWakeup.to(details);
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();

        Snackbar notification = Snackbar.make(view, R.string.sent_message, Snackbar.LENGTH_SHORT);
        notification.show();

        saveDetails(hostname, mac, port);
    }

    /**
     * Get the text from the text edit view.
     *
     * @param id Id of the EditText box to retrieve the contents from.
     * @return String which was contained inside the text box.
      */
    private String getTextContents(int id) {
        return ((EditText) findViewById(id)).getText().toString();
    }

    /**
     * Display a snackbar alert for the user.
     *
     * @param thisView The current view from the application.
     * @param string_id ID code of the string to be displayed in the snack bar.
     */
    private void alert(View thisView, int string_id) {
        Snackbar.make(thisView, string_id, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Verify that the hostname exists and is valid.
     *
     * @param hostname Hostname expected to send the packet to.
     * @return Boolean indicating if this hostname exists.
     */
    private boolean verifyHostname(String hostname) {
        return !hostname.equals("");
    }

    /**
     * Verify that the MAC address is valid.
     *
     * @param mac String MAC address extracted from the application.
     * @return Boolean indicating if this MAC address is valid.
     */
    private boolean verifyMac(String mac) {
        String[] splitMac = mac.split(":");

        return splitMac.length == 6;
    }

    /**
     * Verify that the port number provided is both a number and within the speicified port range
     * 0 -> 65535 for valid ports.
     *
     * @param port String port number extracted from the Android view.
     * @return Boolean indicating if the port is valid.
     */
    private boolean verifyPort(String port) {
        try {
            int portNumber = Integer.parseInt(port);
            return portNumber >= 0 && portNumber < 65535;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    /**
     * Save the details into the DB.
     *
     * @param hostname Hostname of the machine which was woken up.
     * @param mac MAC address of the machine which was woken up.
     * @param port Port of the machine which was woken up.
     */
    private void saveDetails(String hostname, String mac, String port) {
        SQLiteDatabase db = new HistoryDBSQLiteHelper(this).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HistoryDBContract.History.COLUMN_HOSTNAME, hostname);
        values.put(HistoryDBContract.History.COLUMN_MAC, mac);
        values.put(HistoryDBContract.History.COLUMN_PORT, Integer.parseInt(port));

        db.insert(HistoryDBContract.History.TABLE_NAME, null, values);
    }
}
