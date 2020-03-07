package de.zigldrum.ihnn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import de.zigldrum.ihnn.networking.objects.ContentPackResponse;
import de.zigldrum.ihnn.networking.services.ContentService;
import de.zigldrum.ihnn.networking.services.RequesterService;
import de.zigldrum.ihnn.networking.tasks.CheckUpdateResponse;
import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.utils.AppState;
import de.zigldrum.ihnn.utils.Constants.SettingsResults;
import de.zigldrum.ihnn.utils.Utils;
import retrofit2.Call;

import static de.zigldrum.ihnn.utils.Constants.*;
import static de.zigldrum.ihnn.utils.Constants.RequestCodes.GAME_REQUEST_CODE;
import static de.zigldrum.ihnn.utils.Constants.RequestCodes.SETTINGS_REQUEST_CODE;

public class Home extends AppCompatActivity implements CheckUpdateResponse.UpdateMethods {

    private static final String LOG_TAG = "Home";

    private ProgressBar progressBar;
    private Snackbar snackbar;
    private TextView info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "Running Home::onCreate()");

        info = findViewById(R.id.progress_info);
        progressBar = findViewById(R.id.main_progress);

        setNetworkingProgressVisibility(true);
        setNetworkingProgress(true, 0);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Log.i(LOG_TAG, "Running Home::onPostCreate()");

        if (AppState.getInstance().getEnableAutoUpdates()) {
            checkForUpdates();
        } else {
            setNetworkingProgressVisibility(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Running Home::onResume()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SETTINGS_REQUEST_CODE:
                evalSettingsResponse(resultCode);
                break;
            case GAME_REQUEST_CODE:
                evalGameResponse(resultCode);
                break;
            default:
                Log.d(LOG_TAG, "Unknown: request-code=" + requestCode + ", result-code=" + resultCode);
                break;
        }
    }

    private void evalSettingsResponse(int resultCode) {
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

    private void evalGameResponse(int resultCode) {
        switch (resultCode) {
            case GameResults.GAME_DEFAULT:
                Log.d(LOG_TAG, "Got 0 from Game -> Doing nothing.");
                break;
            case GameResults.GAME_QUESTIONS_EMPTY:
                Log.d(LOG_TAG, "Got 1 from Game -> Displaying Error-Snackbar!");
                showLongSnackbar(R.string.info_no_questions_available);
                break;
            default:
                Log.w(LOG_TAG, "Got undefined result-code from Game!");
                break;
        }
    }

    private void checkForUpdates() {
        ContentService backendConn = RequesterService.getInstance();
        Call<ContentPackResponse> request = backendConn.getPacks();
        CheckUpdateResponse responseChecker = new CheckUpdateResponse(this);
        request.enqueue(responseChecker);
    }

    private void dismissSnackbar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    /*
     * Update-Methods
     */

    @Override
    public void updatesFinished(boolean success) {
        if (success) {
            Log.d(LOG_TAG, "Updates have finished! Can continue normally.");
        } else {
            Log.d(LOG_TAG, "There was an error. We will handle this in the future.");
        }
    }

    @Override
    public <T> void setNetworkingInfoText(@NonNull T text) {
        runOnUiThread(() -> {
            if (text instanceof CharSequence) {
                info.setText((CharSequence) text);
            } else if (text instanceof Integer) {
                info.setText((Integer) text);
            }
        });
    }

    @Override
    public <T> void showLongSnackbar(@NonNull T text) {
        runOnUiThread(() -> {
            dismissSnackbar();
            snackbar = Utils.showLongSnackbar(this, text);
        });
    }

    @Override
    public void setNetworkingProgressVisibility(boolean isVisible) {
        if (progressBar == null) {
            Log.w(LOG_TAG, "Cannot get Progressbar!");
        } else {
            progressBar.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void setNetworkingProgress(boolean indeterminate, @IntRange(from = 0, to = 100) int progress) {
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
        dismissSnackbar();

        Intent startGame = new Intent(this, Game.class);
        startActivityForResult(startGame, RequestCodes.GAME_REQUEST_CODE);
    }

    public void openSettings(View v) {
        dismissSnackbar();

        Intent openSettings = new Intent(this, Settings.class);
        startActivityForResult(openSettings, RequestCodes.SETTINGS_REQUEST_CODE);
    }

    public void openProposals(View v) {
        dismissSnackbar();

        Intent openProposals = new Intent(this, ProposeQuestion.class);
        startActivity(openProposals);
    }

    public void openContentManagement(View v) {
        dismissSnackbar();

        Intent openContentManagement = new Intent(this, ContentPacks.class);
        startActivity(openContentManagement);
    }
}
