package de.zigldrum.ihnn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.networking.objects.Question;
import de.zigldrum.ihnn.objects.AppState;
import de.zigldrum.ihnn.tasks.CheckForUpdates;
import de.zigldrum.ihnn.utils.Constants.ContentPacksResults;
import de.zigldrum.ihnn.utils.Constants.SettingsResults;
import de.zigldrum.ihnn.utils.Utils;
import de.zigldrum.ihnn.views.Game;
import de.zigldrum.ihnn.views.ProposeQuestion;
import de.zigldrum.ihnn.views.Settings;
import de.zigldrum.ihnn.views.contentpacks.view.ContentPacks;

import static de.zigldrum.ihnn.utils.Constants.RequestCodes.CONTENTPACKS_REQUEST_CODE;
import static de.zigldrum.ihnn.utils.Constants.RequestCodes.SETTINGS_REQUEST_CODE;

public class Home extends AppCompatActivity {

    private static final String LOG_TAG = "Home";

    private final Home homeActivity = this;

    public AppState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Running Home onCreate...");

        Utils.setMainProgressVisible(this, true);
        Utils.setMainProgressProgress(this, true, 0);
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
            } else {
                Utils.showShortToast(getApplicationContext(), "Commencing normal startup now!");
                if (state.getEnableAutoUpdates()) checkForUpdates();
                else Utils.setMainProgressVisible(this, false);
            }
        } else {
            Log.i(LOG_TAG, "Commencing Normal Startup...");
            if (state.getEnableAutoUpdates()) checkForUpdates();
            else Utils.setMainProgressVisible(this, false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Resuming Home activity!");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE) {
            switch (resultCode) {
                case SettingsResults.DEFAULT:
                    Log.d(LOG_TAG, "Received 0 from Settings Activity. Doing nothing.");
                    break;
                case SettingsResults.STATE_CHANGED:
                    Log.d(LOG_TAG, "Received 1 from Settings Activity. Reloading State");
                    state = AppState.loadState(getFilesDir());
                    break;
                case SettingsResults.UPDATENOW:
                    Log.d(LOG_TAG, "Received 2 from Settings Activity. Triggering Content Update!");
                    checkForUpdates();
                    break;
                case SettingsResults.STATE_AND_UPDATE:
                    Log.d(LOG_TAG, "Received 3 from Settings Activity. Reloading state and updating content!");
                    state = AppState.loadState(getFilesDir());
                    checkForUpdates();
                default:
                    Log.w(LOG_TAG, "Got undefined result code from Settings!");
                    break;
            }
        } else if (requestCode == CONTENTPACKS_REQUEST_CODE) {
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
    }

    private AppState firstStart() {
        state = new AppState();

        try (CSVReaderHeaderAware packIn = new CSVReaderHeaderAware(new InputStreamReader(getResources().openRawResource(R.raw.contentpacks)))) {
            ArrayList<ContentPack> packList = new ArrayList<>();
            Map<String, String> packMap = null;
            while ((packMap = packIn.readMap()) != null) {
                int id = Integer.parseInt(packMap.get("content_pack_id"));
                int version = Integer.parseInt(packMap.get("content_pack_version"));
                int minAge = Integer.parseInt(packMap.get("content_pack_min_age"));
                String keywords = packMap.get("content_pack_keywords");
                String description = packMap.get("content_pack_description");
                String name = packMap.get("content_pack_name");

                ContentPack pack = new ContentPack(id, name, description, keywords, minAge, version);
                packList.add(pack);
            }
            Log.i(LOG_TAG, "Found " + packList.size() + " Packs!");
            state.setPacks(packList);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return state;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return state;
        }

        try (CSVReaderHeaderAware questionIn = new CSVReaderHeaderAware(new InputStreamReader(getResources().openRawResource(R.raw.questions)))) {
            ArrayList<Question> questionList = new ArrayList<>();
            Map<String, String> questionMap = null;
            while ((questionMap = questionIn.readMap()) != null) {
                int id = Integer.parseInt(questionMap.get("question_id"));
                int packID = Integer.parseInt(questionMap.get("content_pack_id_fk"));
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
        CheckForUpdates checkForUpdatesTask = new CheckForUpdates();
        checkForUpdatesTask.execute(homeActivity);
    }

    public void updatesFinished(boolean success) {
        if (success) {
            Log.d(LOG_TAG, "Updates have finished! Can continue with program as normal.");
        } else {
            Log.d(LOG_TAG, "There was an error. Maybe handle this in the future?");
        }
    }

    public void setInfoText(String message) {
        TextView info = findViewById(R.id.progress_info);
        info.setText(message);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, Game.class);
        String message = "Have fun!";
        intent.putExtra("msg", message);
        startActivity(intent);
    }

    public void openSettings(View v) {
        Intent intent = new Intent(this, Settings.class);
        String message = "Go set some things!";
        intent.putExtra("msg", message);
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    public void openProposals(View v) {
        Intent intent = new Intent(this, ProposeQuestion.class);
        String message = "Go propose!";
        intent.putExtra("msg", message);
        startActivity(intent);
    }

    public void openContentManagement(View v) {
        Intent intent = new Intent(this, ContentPacks.class);
        String message = "Go manage!";
        intent.putExtra("msg", message);
        startActivityForResult(intent, CONTENTPACKS_REQUEST_CODE);
    }
}
