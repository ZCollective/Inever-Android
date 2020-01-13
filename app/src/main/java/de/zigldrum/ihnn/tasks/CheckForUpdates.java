package de.zigldrum.ihnn.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.zigldrum.ihnn.BuildConfig;
import de.zigldrum.ihnn.Home;
import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.objects.ContentPack;
import de.zigldrum.ihnn.objects.ContentPackResponse;
import de.zigldrum.ihnn.objects.Question;
import de.zigldrum.ihnn.objects.QuestionResponse;
import de.zigldrum.ihnn.services.RequesterService;
import de.zigldrum.ihnn.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckForUpdates extends AsyncTask<Home, String, Boolean> {

    private Home app;
    @Override
    protected Boolean doInBackground(Home... activities) {
        app = activities[0];
        app.runOnUiThread(() -> app.setInfoText(app.getResources().getString(R.string.info_checking_updates)));
        try {
            Call<ContentPackResponse> request = RequesterService.buildContentService().getPacks();
            ContentPackResponse cpr = request.execute().body();
            System.out.println("Received Response. Message: Success:" + cpr.getSuccess() + " | Error: " + cpr.getError());
            List<ContentPack> remotePacks = cpr.getMsg();
            if (remotePacks == null) {
                System.out.println("Got null as message! Not correct!");
                publishProgress(app.getResources().getString(R.string.info_update_error));
                return new Boolean(false);
            }
            System.out.println(remotePacks.toString());
            List<ContentPack> availablePacks = app.state.getPacks();
            List<ContentPack> packsToSet = new ArrayList<>();
            for (ContentPack availablePack : availablePacks) {
                for (ContentPack remotePack : remotePacks) {
                    if (BuildConfig.DEBUG)
                        System.out.println("Available: " + availablePack.toString() + "\n Remote: " + remotePack.toString());
                    if (BuildConfig.DEBUG)
                        System.out.println("\n" +
                                availablePack.getId() + " == " + remotePack.getId() + " equals " + (availablePack.getId().intValue() == remotePack.getId().intValue()));
                    if (availablePack.getId().intValue() == remotePack.getId().intValue()) {
                        if (availablePack.getVersion().intValue() >= remotePack.getVersion().intValue()) {
                            System.out.println("Pack: " + availablePack.getName() + " (ID: " + availablePack.getId() + ") is already available with the same or higher version.");
                            remotePacks.remove(remotePack);
                            packsToSet.add(remotePack);
                        }
                        break;
                    }
                }
            }
            if (remotePacks.size() == 0) {
                System.out.println("Updated finished. No new packs found!");
                Utils.setMainProgressVisible(app, false);
                publishProgress(app.getResources().getString(R.string.info_up_to_date));
                return new Boolean(true);
            }

            System.out.println("Found " + remotePacks.size() + " new Packs. Getting Questions now...");
            Utils.setMainProgressProgress(app, false, 5);
            app.runOnUiThread(() -> app.setInfoText(app.getResources().getString(R.string.info_downloading_updates)));

            List<Question> questionsToSet = app.state.getQuestions()
                    .stream()
                    .filter(q -> remotePacks
                            .stream()
                            .filter(p -> p.getId().intValue() == q.getPackid().intValue()).count() == 0)
                    .collect(Collectors.toList());

            int totalProgress = 5;
            int progressInc = (90/remotePacks.size());
            for(ContentPack newPack : remotePacks) {
                try {
                    Call<QuestionResponse> questionRequest = RequesterService.buildContentService().getQuestions(newPack.getId());
                    QuestionResponse qr = questionRequest.execute().body();
                    if(qr.getError()) {
                        System.out.println("Get Questions for pack: " + newPack.getId() + " with version " + newPack.getVersion() + " Failed.");
                        break;
                    }
                    List<Question> questions = qr.getMsg();
                    questionsToSet.addAll(questions);
                    totalProgress += progressInc;
                    Utils.setMainProgressProgress(app, false, totalProgress);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    System.out.println("Exception occurred in CheckForUpdates Task!");
                }
            }
            packsToSet.addAll(remotePacks);
            app.runOnUiThread(() -> app.setInfoText(app.getResources().getString(R.string.info_storing_updates_to_phone)));
            app.state.setPacks(packsToSet);
            app.state.setQuestions(questionsToSet);
            if (app.state.saveState(app.getFilesDir())){
                Utils.setMainProgressProgress(app, false, 100);
                System.out.println("Saved AppState after Updates!");
                publishProgress(app.getResources().getString(R.string.info_update_success));
                return new Boolean(true);
            } else {
                System.out.println("Could not save AppState!");
                publishProgress(app.getResources().getString(R.string.info_update_error));
                return new Boolean(false);
            }
        } catch (ConnectException ce) {
            System.out.println("Server is down!");
            publishProgress(app.getResources().getString(R.string.info_server_down));
            return new Boolean(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred in CheckForUpdates Task!");
            return new Boolean(false);
        } finally{
            Utils.setMainProgressVisible(app, false);
            app.runOnUiThread(() -> app.setInfoText(""));
        }
    }
    @Override
    protected void onPostExecute(Boolean result) {

        app.updatesFinished(result.booleanValue());
    }

    @Override
    protected void onProgressUpdate(String... messages){
        Utils.showLongToast(app.getApplicationContext(), messages[0]);
    }

}
