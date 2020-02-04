package de.zigldrum.ihnn.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.zigldrum.ihnn.activities.content.SettingsAdapter;
import de.zigldrum.ihnn.utils.AppState;
import de.zigldrum.ihnn.utils.Constants;
import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.utils.Constants.SettingsResults;

import static de.zigldrum.ihnn.utils.Constants.SettingsResults.DEFAULT;
import static de.zigldrum.ihnn.utils.Constants.SettingsResults.UPDATE_NOW;

public class Settings extends AppCompatActivity implements SettingsAdapter.SettingsSwitchCallback {

    private static final String LOG_TAG = "Settings";

    private boolean stateUpdated = false;
    private boolean updateTriggered = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.i(LOG_TAG, "Running Settings::onCreate()");

        RecyclerView rView = findViewById(R.id.settings_list_view);

        // Performance improvement
        rView.setHasFixedSize(true);

        // Using linear layout
        rView.setLayoutManager(new LinearLayoutManager(this));

        // Setting adapter class
        rView.setAdapter(new SettingsAdapter(this));

        // Add divider between elements
        rView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public void updateNow(View v) {
        updateTriggered = true;
        finish();
    }

    public void goBack(View v) {
        Log.i(LOG_TAG, "K, thx bai!");
        finish();
    }

    @Override
    public void finish() {
        Log.i(LOG_TAG, "Running Settings::finish()");

        if (stateUpdated) {
            if (AppState.getInstance(null).saveState()) {
                Log.i(LOG_TAG, "Saving AppState after Updates!");
            } else {
                Log.w(LOG_TAG, "Could not save AppState!");
            }
        }

        setResult(updateTriggered ? SettingsResults.UPDATE_NOW : SettingsResults.DEFAULT);
        super.finish();
    }

    @Override
    public void setStateUpdated() {
        stateUpdated = true;
    }
}
