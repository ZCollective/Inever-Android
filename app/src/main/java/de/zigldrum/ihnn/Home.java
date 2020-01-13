package de.zigldrum.ihnn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.opencsv.CSVReaderHeaderAware;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import de.zigldrum.ihnn.finals.ContentPacksResults;
import de.zigldrum.ihnn.objects.AppState;
import de.zigldrum.ihnn.objects.ContentPack;
import de.zigldrum.ihnn.objects.Question;
import de.zigldrum.ihnn.finals.SettingsResults;
import de.zigldrum.ihnn.tasks.CheckForUpdates;
import de.zigldrum.ihnn.utils.Utils;
import de.zigldrum.ihnn.views.Game;
import de.zigldrum.ihnn.views.ProposeQuestion;
import de.zigldrum.ihnn.views.Settings;
import de.zigldrum.ihnn.views.contentpacks.view.ContentPacks;

import static de.zigldrum.ihnn.finals.RequestCodes.CONTENTPACKS_REQUEST_CODE;
import static de.zigldrum.ihnn.finals.RequestCodes.SETTINGS_REQUEST_CODE;

public class Home extends AppCompatActivity {

    private final Home homeActivity = this;

    public AppState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("Running Home onCreate...");

        Utils.setMainProgressVisible(this, true);
        Utils.setMainProgressProgress(this, true, 0);
        if (AppState.isFirstStart(getFilesDir())) {
            System.out.println("No AppState! First startup...");
            state = firstStart();
            if (state.isInitialized()) {
                System.out.println("First Start was successful!");
            } else {
                System.out.println("First Start Failed!");
            }
        } else {
            state = AppState.loadState(getFilesDir());
        }

        if (state == null || !state.isInitialized()) {
            System.out.println("AppState is null or not initialized.");
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
            System.out.println("Commencing Normal Startup...");
            if (state.getEnableAutoUpdates()) checkForUpdates();
            else Utils.setMainProgressVisible(this, false);
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("Resuming Home activity!");
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if (requestCode == SETTINGS_REQUEST_CODE) {
            switch (resultCode) {
                case SettingsResults.DEFAULT:
                System.out.println("Received 0 from Settings Activity. Doing nothing.");
                    break;
                case SettingsResults.STATE_CHANGED:
                    System.out.println("Received 1 from Settings Activity. Reloading State");
                    state = AppState.loadState(getFilesDir());
                    break;
                case SettingsResults.UPDATENOW:
                    System.out.println("Received 2 from Settings Activity. Triggering Content Update!");
                    checkForUpdates();
                    break;
                case SettingsResults.STATE_AND_UPDATE:
                    System.out.println("Received 3 from Settings Activity. Reloading state and updating content!");
                    state = AppState.loadState(getFilesDir());
                    checkForUpdates();
                default: System.out.println("Got undefined result code from Settings!");
                break;
            }
        } else if (requestCode == CONTENTPACKS_REQUEST_CODE) {
            switch (resultCode) {
                case ContentPacksResults.DEFAULT: System.out.println("Received 0 from ContentPacks Activity. Doing nothing.");
                    break;
                    case ContentPacksResults.UPDATED: System.out.println("Received 1 from ContentPacks Activity. Updating State");
                        state = AppState.loadState(getFilesDir());
                        break;
                default:
                    System.out.println("Got undefined result code from ContentPacks!");
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
            System.out.println("Found " + packList.size() + " Packs!");
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
            System.out.println("Found " + questionList.size() + " Questions!");
            state.setQuestions(questionList);

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return state;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return state;
        }

        state.setInitialized(true);
        if (state.saveState(getFilesDir())) {
            System.out.println("Saved State to Disk!");
        } else {
            System.out.println("Error when saving state!");
        }
        return state;
    }

    private void checkForUpdates() {
        CheckForUpdates checkForUpdatesTask = new CheckForUpdates();
        checkForUpdatesTask.execute(homeActivity);
    }

    public void updatesFinished(boolean success){
        if (success) {
            System.out.println("Updates have finished! Can continue with program as normal.");
        } else {
            System.out.println("There was an error. Maybe handle this in the future?");
        }
    }

    public void setInfoText(String message) {
        TextView info = (TextView) findViewById(R.id.progress_info);
        info.setText(message);
    }

    public void startGame(View view){
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
