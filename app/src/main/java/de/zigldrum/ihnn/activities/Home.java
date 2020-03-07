package de.zigldrum.ihnn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.zigldrum.ihnn.networking.objects.ContentPackResponse;
import de.zigldrum.ihnn.networking.services.ContentService;
import de.zigldrum.ihnn.networking.services.RequesterService;
import de.zigldrum.ihnn.networking.tasks.CheckUpdateResponse;
import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.utils.AppState;
import de.zigldrum.ihnn.utils.Utils;
import retrofit2.Call;

import static de.zigldrum.ihnn.utils.Constants.*;
import static de.zigldrum.ihnn.utils.Constants.GameResults.*;
import static de.zigldrum.ihnn.utils.Constants.SettingsResults.*;

public class Home extends AppCompatActivity implements CheckUpdateResponse.UpdateMethods {

    private static final String LOG_TAG = "Home";

    private final List<Button> uiButtons = new ArrayList<>(4);

    private ProgressBar progressBar;
    private Snackbar snackbar;
    private TextView info;
    private Group progressGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "Running Home::onCreate()");

        info = findViewById(R.id.progress_info);
        progressBar = findViewById(R.id.main_progress);
        progressGroup = findViewById(R.id.progressGroup);

        uiButtons.add(findViewById(R.id.btn_play));
        uiButtons.add(findViewById(R.id.btn_packs));
        uiButtons.add(findViewById(R.id.btn_suggest));
        uiButtons.add(findViewById(R.id.btn_settings));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Log.i(LOG_TAG, "Running Home::onPostCreate()");

        if (AppState.getInstance().autoUpdatesEnabled()) {
            setNetworkingProgressVisibility(true);
            checkForUpdates();
        } else {
            setNetworkingProgressVisibility(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "Running Home::onResume()");

        uiButtons.forEach(btn -> btn.setEnabled(true));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestCodes.RC_SETTINGS:
                evalSettingsResponse(resultCode);
                break;
            case RequestCodes.RC_GAME:
                evalGameResponse(resultCode);
                break;
            default:
                Log.d(LOG_TAG, "Unknown: request-code=" + requestCode + ", result-code=" + resultCode);
                break;
        }
    }

    private void evalSettingsResponse(int resultCode) {
        switch (resultCode) {
            case SETTINGS_DEFAULT:
                Log.d(LOG_TAG, "Got 0 from Settings -> Doing nothing.");
                break;
            case SETTINGS_UPDATE_NOW:
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
            case GAME_DEFAULT:
                Log.d(LOG_TAG, "Got 0 from Game -> Doing nothing.");
                break;
            case GAME_QUESTIONS_EMPTY:
                Log.d(LOG_TAG, "Got 1 from Game -> Displaying Error-Snackbar!");
                showLongSnackbar(R.string.info_no_questions_available);
                break;
            default:
                Log.w(LOG_TAG, "Got undefined result-code from Game!");
                break;
        }
    }

    private void checkForUpdates() {
        runOnUiThread(() -> {
            // Show progressbar and -text to the user
            setNetworkingProgressVisibility(true);
            setNetworkingProgress(false, 5);
        });

        // Make async REST-Call to backend
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
    public void updateFinished(boolean success) {
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
        runOnUiThread(() -> progressGroup.setVisibility(isVisible ? View.VISIBLE : View.GONE));
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
        uiButtons.forEach(btn -> btn.setEnabled(false));

        Intent startGame = new Intent(this, Game.class);
        startActivityForResult(startGame, RequestCodes.RC_GAME);
    }

    public void openSettings(View v) {
        dismissSnackbar();
        uiButtons.forEach(btn -> btn.setEnabled(false));

        Intent openSettings = new Intent(this, Settings.class);
        startActivityForResult(openSettings, RequestCodes.RC_SETTINGS);
    }

    public void openProposals(View v) {
        dismissSnackbar();
        uiButtons.forEach(btn -> btn.setEnabled(false));

        Intent openProposals = new Intent(this, ProposeQuestion.class);
        startActivity(openProposals);
    }

    public void openContentManagement(View v) {
        dismissSnackbar();
        uiButtons.forEach(btn -> btn.setEnabled(false));

        Intent openContentManagement = new Intent(this, ContentPacks.class);
        startActivity(openContentManagement);
    }
}
