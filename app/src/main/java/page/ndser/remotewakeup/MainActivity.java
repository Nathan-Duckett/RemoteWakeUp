package page.ndser.remotewakeup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import page.ndser.remotewakeup.model.WakeupHistory;
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
    }

    // Get the text from the text edit view.
    private String getTextContents(int id) {
        return ((EditText) findViewById(id)).getText().toString();
    }
}
