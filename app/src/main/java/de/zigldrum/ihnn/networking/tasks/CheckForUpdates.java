package de.zigldrum.ihnn.networking.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.zigldrum.ihnn.BuildConfig;
import de.zigldrum.ihnn.Home;
import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.networking.objects.ContentPackResponse;
import de.zigldrum.ihnn.networking.objects.Question;
import de.zigldrum.ihnn.networking.objects.QuestionResponse;
import de.zigldrum.ihnn.networking.services.RequesterService;
import de.zigldrum.ihnn.utils.AppState;
import retrofit2.Call;

// TODO: in enqueue umwandeln, also hier Callback extenden
public class CheckForUpdates extends AsyncTask<Home, String, Boolean> {

    private static final String LOG_TAG = "CheckForUpdates";

    private final UpdateMethods app;
    private final Resources resources;

    public CheckForUpdates(@NonNull UpdateMethods app, @NonNull Context ctx) {
        this.app = app;
        this.resources = ctx.getResources();
    }

    @Override
    protected Boolean doInBackground(Home... activities) {
        app.setInfoText(resources.getString(R.string.info_checking_updates));
        try {
            Call<ContentPackResponse> request = RequesterService.getContentService().getPacks();
            ContentPackResponse cpr = request.execute().body();

            if (cpr == null) {
                Log.w(LOG_TAG, "Got null as message! Not correct!");
                publishProgress(resources.getString(R.string.info_update_error));
                return Boolean.FALSE;
            }

            Log.i(LOG_TAG, "Received Response. Message: " + cpr.getStatus());

            List<ContentPack> remotePacks = cpr.getMsg();
            if (remotePacks == null) {
                Log.w(LOG_TAG, "Got null as message! Not correct!");
                publishProgress(resources.getString(R.string.info_update_error));
                return Boolean.FALSE;
            }

            Log.d(LOG_TAG, remotePacks.toString());

            AppState state = AppState.getInstance(null);  // null allowed -> should already be instantiated

            Set<ContentPack> availablePacks = state.getPacks();
            Set<ContentPack> packsToSet = new HashSet<>();
            for (ContentPack availablePack : availablePacks) {
                for (ContentPack remotePack : remotePacks) {
                    if (BuildConfig.DEBUG) {
                        Log.i(LOG_TAG, "Available: " + availablePack.toString());
                        Log.i(LOG_TAG, "Remote: " + remotePack.toString());
                        Log.i(LOG_TAG, availablePack.getId() + " == " + remotePack.getId() + " equals " + (availablePack.getId().intValue() == remotePack.getId().intValue()));
                    }

                    if (availablePack.getId().intValue() == remotePack.getId().intValue()) {
                        if (availablePack.getVersion() >= remotePack.getVersion()) {
                            Log.d(LOG_TAG, "Pack: " + availablePack.getName() + " (ID: " + availablePack.getId() + ") is already available with the same or higher version.");
                            remotePacks.remove(remotePack);
                            packsToSet.add(remotePack);
                        }
                        break;
                    }
                }
            }

            if (remotePacks.size() == 0) {
                Log.i(LOG_TAG, "Update finished. No new packs found!");
                app.setMainProgressVisible(false);
                publishProgress(resources.getString(R.string.info_up_to_date));
                return Boolean.TRUE;
            }

            Log.i(LOG_TAG, "Found " + remotePacks.size() + " new Packs. Getting Questions now.");

            app.setMainProgressProgress(false, 5);
            app.setInfoText(resources.getString(R.string.info_downloading_updates));

            Set<Question> questionsToSet = state.getQuestions()
                    .stream()
                    .filter(q -> remotePacks
                            .stream()
                            .noneMatch(p -> p.getId().intValue() == q.getPackid().intValue()))
                    .collect(Collectors.toSet());

            int totalProgress = 5;
            int progressInc = (90 / remotePacks.size());
            for (ContentPack newPack : remotePacks) {
                try {
                    Call<QuestionResponse> questionRequest = RequesterService.getContentService().getQuestions(newPack.getId());
                    QuestionResponse qr = questionRequest.execute().body();
                    if (qr == null) {
                        Log.w(LOG_TAG, "Response for " + newPack.getId() + " was null");
                        continue;
                    }

                    if (qr.getError()) {
                        Log.w(LOG_TAG, "Get Questions for pack: " + newPack.getId() + " with version " + newPack.getVersion() + " Failed.");
                        break;
                    }
                    List<Question> questions = qr.getMsg();
                    questionsToSet.addAll(questions);
                    totalProgress += progressInc;
                    app.setMainProgressProgress(false, totalProgress);
                } catch (IOException ioe) {
                    Log.w(LOG_TAG, "Exception occurred in CheckForUpdates Task!", ioe);
                }
            }
            packsToSet.addAll(remotePacks);
            app.setInfoText(resources.getString(R.string.info_storing_updates_to_phone));
            state.setPacks(packsToSet);
            state.setQuestions(questionsToSet);

            if (state.saveState()) {
                app.setMainProgressProgress(false, 100);
                Log.i(LOG_TAG, "Saved AppState after Updates!");
                publishProgress(resources.getString(R.string.info_update_success));
                return Boolean.TRUE;
            } else {
                Log.w(LOG_TAG, "Could not save AppState!");
                publishProgress(resources.getString(R.string.info_update_error));
                return Boolean.FALSE;
            }
        } catch (ConnectException ce) {
            Log.d(LOG_TAG, "Server is down!");
            publishProgress(resources.getString(R.string.info_server_down));
            return Boolean.FALSE;
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception occurred in CheckForUpdates Task!", e);
            return Boolean.FALSE;
        } finally {
            app.setMainProgressVisible(false);
            app.setInfoText("");
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        app.updatesFinished(result);
    }

    @Override
    protected void onProgressUpdate(@NonNull String... messages) {
        app.showLongToast(messages[0]);
    }

    public interface UpdateMethods {
        void setInfoText(String txt);

        void updatesFinished(Boolean result);

        void showLongToast(String text);

        void setMainProgressVisible(boolean isVisible);

        void setMainProgressProgress(boolean indeterminate, int progress);
    }
}
