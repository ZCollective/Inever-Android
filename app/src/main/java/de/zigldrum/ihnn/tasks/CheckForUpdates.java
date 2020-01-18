package de.zigldrum.ihnn.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.zigldrum.ihnn.BuildConfig;
import de.zigldrum.ihnn.Home;
import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.networking.objects.ContentPackResponse;
import de.zigldrum.ihnn.networking.objects.Question;
import de.zigldrum.ihnn.networking.objects.QuestionResponse;
import de.zigldrum.ihnn.services.RequesterService;
import de.zigldrum.ihnn.utils.Utils;
import retrofit2.Call;

public class CheckForUpdates extends AsyncTask<Home, String, Boolean> {

    private static final String LOG_TAG = "CheckForUpdates";

    private Home app;

    @Override
    protected Boolean doInBackground(Home... activities) {
        app = activities[0];
        app.runOnUiThread(() -> app.setInfoText(app.getResources().getString(R.string.info_checking_updates)));
        try {
            Call<ContentPackResponse> request = RequesterService.buildContentService().getPacks();
            ContentPackResponse cpr = request.execute().body();
            Log.i(LOG_TAG, "Received Response. Message: Success:" + cpr.getSuccess() + " | Error: " + cpr.getError());
            List<ContentPack> remotePacks = cpr.getMsg();
            if (remotePacks == null) {
                Log.w(LOG_TAG, "Got null as message! Not correct!");
                publishProgress(app.getResources().getString(R.string.info_update_error));
                return new Boolean(false);
            }
            Log.d(LOG_TAG, remotePacks.toString());
            List<ContentPack> availablePacks = app.state.getPacks();
            List<ContentPack> packsToSet = new ArrayList<>();
            for (ContentPack availablePack : availablePacks) {
                for (ContentPack remotePack : remotePacks) {
                    if (BuildConfig.DEBUG) {
                        Log.i(LOG_TAG, "Available: " + availablePack.toString());
                        Log.i(LOG_TAG, "Remote: " + remotePack.toString());
                        Log.i(LOG_TAG, availablePack.getId() + " == " + remotePack.getId() + " equals " + (availablePack.getId().intValue() == remotePack.getId().intValue()));
                    }

                    if (availablePack.getId().intValue() == remotePack.getId().intValue()) {
                        if (availablePack.getVersion().intValue() >= remotePack.getVersion().intValue()) {
                            Log.d(LOG_TAG, "Pack: " + availablePack.getName() + " (ID: " + availablePack.getId() + ") is already available with the same or higher version.");
                            remotePacks.remove(remotePack);
                            packsToSet.add(remotePack);
                        }
                        break;
                    }
                }
            }
            if (remotePacks.size() == 0) {
                Log.i(LOG_TAG, "Updated finished. No new packs found!");
                Utils.setMainProgressVisible(app, false);
                publishProgress(app.getResources().getString(R.string.info_up_to_date));
                return new Boolean(true);
            }

            Log.i(LOG_TAG, "Found " + remotePacks.size() + " new Packs. Getting Questions now...");
            Utils.setMainProgressProgress(app, false, 5);
            app.runOnUiThread(() -> app.setInfoText(app.getResources().getString(R.string.info_downloading_updates)));

            List<Question> questionsToSet = app.state.getQuestions()
                    .stream()
                    .filter(q -> remotePacks
                            .stream()
                            .filter(p -> p.getId().intValue() == q.getPackid().intValue()).count() == 0)
                    .collect(Collectors.toList());

            int totalProgress = 5;
            int progressInc = (90 / remotePacks.size());
            for (ContentPack newPack : remotePacks) {
                try {
                    Call<QuestionResponse> questionRequest = RequesterService.buildContentService().getQuestions(newPack.getId());
                    QuestionResponse qr = questionRequest.execute().body();
                    if (qr.getError()) {
                        Log.w(LOG_TAG, "Get Questions for pack: " + newPack.getId() + " with version " + newPack.getVersion() + " Failed.");
                        break;
                    }
                    List<Question> questions = qr.getMsg();
                    questionsToSet.addAll(questions);
                    totalProgress += progressInc;
                    Utils.setMainProgressProgress(app, false, totalProgress);
                } catch (IOException ioe) {
                    Log.w(LOG_TAG, "Exception occurred in CheckForUpdates Task!", ioe);
                }
            }
            packsToSet.addAll(remotePacks);
            app.runOnUiThread(() -> app.setInfoText(app.getResources().getString(R.string.info_storing_updates_to_phone)));
            app.state.setPacks(packsToSet);
            app.state.setQuestions(questionsToSet);
            if (app.state.saveState(app.getFilesDir())) {
                Utils.setMainProgressProgress(app, false, 100);
                Log.i(LOG_TAG, "Saved AppState after Updates!");
                publishProgress(app.getResources().getString(R.string.info_update_success));
                return new Boolean(true);
            } else {
                Log.w(LOG_TAG, "Could not save AppState!");
                publishProgress(app.getResources().getString(R.string.info_update_error));
                return new Boolean(false);
            }
        } catch (ConnectException ce) {
            Log.w(LOG_TAG, "Server is down!", ce);
            publishProgress(app.getResources().getString(R.string.info_server_down));
            return new Boolean(false);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception occurred in CheckForUpdates Task!", e);
            return new Boolean(false);
        } finally {
            Utils.setMainProgressVisible(app, false);
            app.runOnUiThread(() -> app.setInfoText(""));
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        app.updatesFinished(result.booleanValue());
    }

    @Override
    protected void onProgressUpdate(String... messages) {
        Utils.showLongToast(app.getApplicationContext(), messages[0]);
    }

}
