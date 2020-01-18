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
import static de.zigldrum.ihnn.utils.Constants.SettingsResults.STATE_AND_UPDATE;
import static de.zigldrum.ihnn.utils.Constants.SettingsResults.STATE_CHANGED;
import static de.zigldrum.ihnn.utils.Constants.SettingsResults.UPDATENOW;

public class Settings extends AppCompatActivity {

    private static final String LOG_TAG = "Settings";

    private boolean updateTriggered = false;
    private boolean stateUpdated = false;

    private AppState state;

    private Switch nsfwSwitch;
    private Switch nsfwOnlySwitch;
    private Switch autoUpdateSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.i(LOG_TAG, "Settings was created!");
        Log.d(LOG_TAG, "Message: " + getIntent().getStringExtra("msg"));

        state = AppState.loadState(getFilesDir());

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
        this.finish();
    }

    @Override
    public void finish() {
        if (stateUpdated) state.saveState(getFilesDir());
        Intent data = new Intent();
        int stateChanged = stateUpdated ? STATE_CHANGED : DEFAULT;
        int updated = updateTriggered ? UPDATENOW : stateChanged;
        int resultCode = updateTriggered && stateUpdated ? STATE_AND_UPDATE : updated;
        setResult(resultCode, data);
        super.finish();
    }
}
