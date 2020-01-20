package de.zigldrum.ihnn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.utils.AppState;

import static de.zigldrum.ihnn.utils.Constants.SettingsResults.DEFAULT;
import static de.zigldrum.ihnn.utils.Constants.SettingsResults.UPDATE_NOW;

public class Settings extends AppCompatActivity {

    private static final String LOG_TAG = "Settings";

    private final AppState state = AppState.getInstance(null);  // null allowed -> should already be instantiated

    private boolean stateUpdated = false;
    private boolean updateTriggered = false;

    private Switch nsfwSwitch;
    private Switch nsfwOnlySwitch;
    private Switch autoUpdateSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.i(LOG_TAG, "Settings was created!");
        Log.d(LOG_TAG, "Message: " + getIntent().getStringExtra("msg"));

        nsfwSwitch = findViewById(R.id.settings_nsfw);
        nsfwOnlySwitch = findViewById(R.id.settings_nsfw_only);
        autoUpdateSwitch = findViewById(R.id.settings_auto_update);

        nsfwSwitch.setChecked(state.getEnableNSFW());
        nsfwOnlySwitch.setChecked(state.isOnlyNSFW());
        autoUpdateSwitch.setChecked(state.getEnableAutoUpdates());
    }

    public void toggleNSFW(View v) {
        boolean newValue = nsfwSwitch.isChecked();
        if (newValue != state.getEnableNSFW()) {
            state.setEnableNSFW(newValue);
            stateUpdated = true;
        }
        Log.i(LOG_TAG, "NSFW is " + (nsfwSwitch.isChecked() ? "enabled" : "disabled"));
    }

    public void toggleAutoUpdate(View v) {
        boolean newValue = autoUpdateSwitch.isChecked();
        if (newValue != state.getEnableAutoUpdates()) {
            state.setEnableAutoUpdates(newValue);
            stateUpdated = true;
        }
        Log.i(LOG_TAG, "Auto Updates are " + (autoUpdateSwitch.isChecked() ? "enabled" : "disabled"));
    }

    public void toggleNSFWOnlyMode(View v) {
        boolean newValue = nsfwOnlySwitch.isChecked();
        if (newValue != state.isOnlyNSFW()) {
            state.setOnlyNSFW(newValue);
            stateUpdated = true;
        }
        Log.i(LOG_TAG, "NSFW Only mode is " + (nsfwOnlySwitch.isChecked() ? "enabled" : "disabled"));
    }

    public void updateNow(View v) {
        updateTriggered = true;
        this.finish();
    }

    public void goBack(View v) {
        Log.i(LOG_TAG, "K, thx bai!");
        finish();
    }

    @Override
    public void finish() {
        if (stateUpdated) {
            if (state.saveState()) {
                Log.i(LOG_TAG, "Saving AppState after Updates!");
            } else {
                Log.w(LOG_TAG, "Could not save AppState!");
            }
        }

        Intent data = new Intent();
        int resultCode = updateTriggered ? UPDATE_NOW : DEFAULT;
        setResult(resultCode, data);
        super.finish();
    }
}
