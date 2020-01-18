package de.zigldrum.ihnn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.networking.objects.Question;
import de.zigldrum.ihnn.networking.tasks.CheckForUpdates;
import de.zigldrum.ihnn.utils.AppState;
import de.zigldrum.ihnn.utils.Constants.ContentPacksResults;
import de.zigldrum.ihnn.utils.Constants.SettingsResults;
import de.zigldrum.ihnn.utils.Utils;

import static de.zigldrum.ihnn.utils.Constants.RequestCodes.CONTENTPACKS_REQUEST_CODE;
import static de.zigldrum.ihnn.utils.Constants.RequestCodes.SETTINGS_REQUEST_CODE;

public class Home extends AppCompatActivity implements CheckForUpdates.UpdateMethods {

    private static final String LOG_TAG = "Home";

    public AppState state;

    private TextView info;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Running Home onCreate...");

        Utils.setMainProgressVisible(this, true);
        setMainProgressProgress(true, 0);

        if (AppState.isFirstStart(getFilesDir())) {
            Log.i(LOG_TAG, "No AppState! First startup...");
            state = firstStart();
            if (state.isInitialized()) {
                Log.i(LOG_TAG, "First Start was successful!");
            } else {
                Log.w(LOG_TAG, "First Start Failed!");
            }
        } else {
            state = AppState.loadState(getFilesDir());
        }

        if (state == null || !state.isInitialized()) {
            Log.i(LOG_TAG, "AppState is null or not initialized.");
            Utils.showLongToast(getApplicationContext(), "Error at startup. Please wait while we try to fix things...");
            state = firstStart();
            if (state == null) {
                Utils.showLongToast(getApplicationContext(), "Could not fix the problem. Please contact the dev!");
                return;
            } else {
                Utils.showShortToast(getApplicationContext(), "Commencing normal startup now!");
            }
        }

        Log.i(LOG_TAG, "Commencing Normal Startup...");

        if (state.getEnableAutoUpdates()) {
            checkForUpdates();
        } else {
            Utils.setMainProgressVisible(this, false);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        info = findViewById(R.id.progress_info);
        progressBar = findViewById(R.id.main_progress);
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
            case SettingsResults.STATE_CHANGED:
                Log.d(LOG_TAG, "Got 1 from Settings -> Reloading State");
                state = AppState.loadState(getFilesDir());
                break;
            case SettingsResults.UPDATENOW:
                Log.d(LOG_TAG, "Got 2 from Settings -> Triggering Content Update!");
                checkForUpdates();
                break;
            case SettingsResults.STATE_AND_UPDATE:
                Log.d(LOG_TAG, "Got 3 from Settings -> Reloading state & updating content!");
                state = AppState.loadState(getFilesDir());
                checkForUpdates();
            default:
                Log.w(LOG_TAG, "Got undefined result-code from Settings!");
                break;
        }
    }

    private void evalContentpacksRequest(int resultCode) {
        switch (resultCode) {
            case ContentPacksResults.DEFAULT:
                Log.d(LOG_TAG, "Received 0 from ContentPacks Activity. Doing nothing.");
                break;
            case ContentPacksResults.UPDATED:
                Log.d(LOG_TAG, "Received 1 from ContentPacks Activity. Updating State");
                state = AppState.loadState(getFilesDir());
                break;
            default:
                Log.w(LOG_TAG, "Got undefined result code from ContentPacks!");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SETTINGS_REQUEST_CODE:
                evalSettingsRequest(resultCode);
                break;
            case CONTENTPACKS_REQUEST_CODE:
                evalContentpacksRequest(resultCode);
                break;
            default:
                Log.d(LOG_TAG, "Unknown request code: " + requestCode);
                break;
        }
    }

    private AppState firstStart() {
        state = new AppState();

        try (CSVReaderHeaderAware packIn = new CSVReaderHeaderAware(new InputStreamReader(getResources().openRawResource(R.raw.contentpacks)))) {
            ArrayList<ContentPack> packList = new ArrayList<>();
            Map<String, String> packMap;
            while ((packMap = packIn.readMap()) != null) {
                String packId = packMap.get("content_pack_id");
                String packVersion = packMap.get("content_pack_version");
                String packMinAge = packMap.get("content_pack_min_age");

                if (packId == null || packVersion == null || packMinAge == null) {
                    Log.w(LOG_TAG, "Malformed pack, skipping this one");
                    continue;
                }

                int id = Integer.parseInt(packId);
                int version = Integer.parseInt(packVersion);
                int minAge = Integer.parseInt(packMinAge);

                String keywords = packMap.get("content_pack_keywords");
                String description = packMap.get("content_pack_description");
                String name = packMap.get("content_pack_name");

                ContentPack pack = new ContentPack(id, name, description, keywords, minAge, version);
                packList.add(pack);
            }
            Log.i(LOG_TAG, "Found " + packList.size() + " Packs!");
            state.setPacks(packList);
        } catch (IOException | CsvValidationException ioe) {
            ioe.printStackTrace();
            return state;
        }

        try (CSVReaderHeaderAware questionIn = new CSVReaderHeaderAware(new InputStreamReader(getResources().openRawResource(R.raw.questions)))) {
            ArrayList<Question> questionList = new ArrayList<>();
            Map<String, String> questionMap;

            while ((questionMap = questionIn.readMap()) != null) {
                String qId = questionMap.get("question_id");
                String packIdFk = questionMap.get("content_pack_id_fk");

                if (qId == null | packIdFk == null) {
                    Log.w(LOG_TAG, "Malformed question, skipping this one");
                    continue;
                }

                int id = Integer.parseInt(qId);
                int packID = Integer.parseInt(packIdFk);
                String string = questionMap.get("question_string");

                Question question = new Question(id, string, packID);
                questionList.add(question);
            }

            Log.i(LOG_TAG, "Found " + questionList.size() + " Questions!");
            state.setQuestions(questionList);
        } catch (IOException | CsvValidationException ioe) {
            ioe.printStackTrace();
            return state;
        }

        state.setInitialized(true);

        if (state.saveState(getFilesDir())) {
            Log.i(LOG_TAG, "Saved State to Disk!");
        } else {
            Log.w(LOG_TAG, "Error when saving state!");
        }

        return state;
    }

    private void checkForUpdates() {
        CheckForUpdates checkForUpdatesTask = new CheckForUpdates(this, this);
        checkForUpdatesTask.execute();
    }

    /*
     * Update-Methods
     */

    @Override
    public void updatesFinished(Boolean success) {
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
    public AppState getState() {
        return state;
    }

    @Override
    public void showLongToast(String text) {
        Utils.showLongToast(this, text);
    }

    @Override
    public void setMainProgressVisible(boolean isVisible) {
        Utils.setMainProgressVisible(this, isVisible);
    }

    @Override
    public void setMainProgressProgress(boolean indeterminate, int progress) {
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
        startActivityForResult(Utils.openContentManagement(this), CONTENTPACKS_REQUEST_CODE);
    }
}
