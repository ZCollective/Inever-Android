package de.zigldrum.ihnn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import de.zigldrum.ihnn.networking.objects.ContentPackResponse;
import de.zigldrum.ihnn.networking.services.ContentService;
import de.zigldrum.ihnn.networking.services.RequesterService;
import de.zigldrum.ihnn.networking.tasks.CheckUpdateResponse;
import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.utils.AppState;
import de.zigldrum.ihnn.utils.Constants.SettingsResults;
import de.zigldrum.ihnn.utils.Utils;
import io.paperdb.Paper;
import retrofit2.Call;

import static de.zigldrum.ihnn.utils.Constants.RequestCodes.SETTINGS_REQUEST_CODE;

public class Home extends AppCompatActivity implements CheckUpdateResponse.UpdateMethods {

    private static final String LOG_TAG = "Home";

    public AppState state;

    private TextView info;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Running Home onCreate...");

        info = findViewById(R.id.progress_info);
        progressBar = findViewById(R.id.main_progress);

        Paper.init(this);
        state = AppState.getInstance();

        Utils.setMainProgressVisible(this, true);
        setMainProgressProgress(true, 0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Log.d(LOG_TAG, "Running Home onPostCreate...");

        if (!state.isInitialized()) {
            Utils.showLongToast(this, "Could not fix the problem. Please contact the dev!");
            return;
        }

        Log.i(LOG_TAG, "Commencing Normal Startup...");

        if (state.getEnableAutoUpdates()) {
            checkForUpdates();
        } else {
            Utils.setMainProgressVisible(this, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Resuming Home activity!");
    }

    private void evalSettingsRequest(int resultCode) {
        switch (resultCode) {
            case SettingsResults.DEFAULT:
                Log.d(LOG_TAG, "Got 0 from Settings -> Doing nothing.");
                break;
            case SettingsResults.UPDATE_NOW:
                Log.d(LOG_TAG, "Got 1 from Settings -> Triggering Content Update!");
                checkForUpdates();
                break;
            default:
                Log.w(LOG_TAG, "Got undefined result-code from Settings!");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQUEST_CODE) {
            evalSettingsRequest(resultCode);
        } else {
            Log.d(LOG_TAG, "Unknown request code: " + requestCode);
        }
    }

    private void checkForUpdates() {
        ContentService backendConn = RequesterService.getInstance();
        Call<ContentPackResponse> request = backendConn.getPacks();
        CheckUpdateResponse responseChecker = new CheckUpdateResponse(this, this);
        request.enqueue(responseChecker);
    }

    /*
     * Update-Methods
     */

    @Override
    public void updatesFinished(boolean success) {
        if (success) {
            Log.d(LOG_TAG, "Updates have finished! Can continue with program as normal.");
        } else {
            Log.d(LOG_TAG, "There was an error. Maybe handle this in the future?");
        }
    }

    @Override
    public void setInfoText(String message) {
        runOnUiThread(() -> info.setText(message));
    }

    @Override
    public void showLongToast(@NonNull String text) {
        Utils.showLongToast(this, text);
    }

    @Override
    public void setMainProgressVisible(boolean isVisible) {
        Utils.setMainProgressVisible(this, isVisible);
    }

    @Override
    public void setMainProgressProgress(boolean indeterminate, @IntRange(from = 0, to = 100) int progress) {
        runOnUiThread(() -> {
            if (progressBar == null) {
                Log.w(LOG_TAG, "Cannot get Progressbar!");
            } else if (indeterminate) {
                progressBar.setIndeterminate(true);
            } else {
                progressBar.setIndeterminate(false);
                progressBar.setProgress(progress);
            }
        });
    }

    /*
     * GUI-Callbacks
     */

    public void startGame(View v) {
        startActivity(Utils.startGame(this));
    }

    public void openSettings(View v) {
        startActivityForResult(Utils.openSettings(this), SETTINGS_REQUEST_CODE);
    }

    public void openProposals(View v) {
        startActivity(Utils.openProposals(this));
    }

    public void openContentManagement(View v) {
        startActivity(Utils.openContentManagement(this));
    }
}
