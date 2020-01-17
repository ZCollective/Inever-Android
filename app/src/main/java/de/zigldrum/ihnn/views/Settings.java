package de.zigldrum.ihnn.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.objects.AppState;

import static de.zigldrum.ihnn.finals.SettingsResults.DEFAULT;
import static de.zigldrum.ihnn.finals.SettingsResults.STATE_AND_UPDATE;
import static de.zigldrum.ihnn.finals.SettingsResults.STATE_CHANGED;
import static de.zigldrum.ihnn.finals.SettingsResults.UPDATENOW;

public class Settings extends AppCompatActivity {

    private static final String LOG_TAG = "Settings";

    private boolean updateTriggered = false;
    private boolean stateUpdated = false;

    private AppState state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.i(LOG_TAG, "Settings was created!");
        Log.d(LOG_TAG, "Message: " + getIntent().getStringExtra("msg"));
        state = AppState.loadState(getFilesDir());
        Switch nsfwSwtich = findViewById(R.id.settings_nsfw);
        nsfwSwtich.setChecked(state.getEnableNSFW());
        Switch autoUpdateSwitch = findViewById(R.id.settings_auto_update);
        autoUpdateSwitch.setChecked(state.getEnableAutoUpdates());
        Switch nsfwOnlySwitch = findViewById(R.id.settings_nsfw_only);
        nsfwOnlySwitch.setChecked(state.isOnlyNSFW());
    }

    public void toggleNSFW(View v) {
        Switch nsfwSwitch = findViewById(R.id.settings_nsfw);
        boolean newValue = nsfwSwitch.isChecked();
        if (newValue != state.getEnableNSFW()){
            state.setEnableNSFW(newValue);
            stateUpdated = true;
        }
        Log.i(LOG_TAG, "NSFW is " + (nsfwSwitch.isChecked() ? "enabled" : "disabled"));
    }

    public void toggleAutoUpdate(View v) {
        Switch autoUpdateSwitch = findViewById(R.id.settings_auto_update);
        boolean newValue = autoUpdateSwitch.isChecked();
        if (newValue != state.getEnableAutoUpdates()){
            state.setEnableAutoUpdates(newValue);
            stateUpdated = true;
        }
        Log.i(LOG_TAG, "Autoupdates are " + (autoUpdateSwitch.isChecked() ? "enabled" : "disabled"));
    }

    public void toggleNSFWOnlyMode(View v){
        Switch nsfwOnlySwitch = findViewById(R.id.settings_nsfw_only);
        boolean newValue = nsfwOnlySwitch.isChecked();
        if (newValue != state.isOnlyNSFW()){
            state.setOnlyNSFW(newValue);
            stateUpdated = true;
        }
        Log.i(LOG_TAG, "NSFW Only mode is " + (nsfwOnlySwitch.isChecked() ? "enabled" : "disabled"));
    }

    public void updateNow (View v) {
        updateTriggered = true;
        this.finish();
    }

    public void goBack (View v) {
        Log.i(LOG_TAG, "K, thx bai!");
        this.finish();
    }

    @Override
    public void finish() {
        if (stateUpdated) state.saveState(getFilesDir());
        Intent data = new Intent();
        int resultCode = updateTriggered && stateUpdated ? STATE_AND_UPDATE : (updateTriggered ? UPDATENOW : (stateUpdated ? STATE_CHANGED : DEFAULT));
        setResult(resultCode, data);
        super.finish();
    }
}
