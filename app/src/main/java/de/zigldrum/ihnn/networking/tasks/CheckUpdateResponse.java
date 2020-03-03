package de.zigldrum.ihnn.networking.tasks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.zigldrum.ihnn.BuildConfig;
import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.networking.objects.ContentPackResponse;
import de.zigldrum.ihnn.networking.objects.Question;
import de.zigldrum.ihnn.networking.objects.QuestionResponse;
import de.zigldrum.ihnn.networking.services.RequesterService;
import de.zigldrum.ihnn.utils.AppState;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckUpdateResponse implements Callback<ContentPackResponse> {

    private static final String LOG_TAG = "CheckUpdateResponse";

    private final UpdateMethods caller;
    private final ResponseProgressStrings info;

    public CheckUpdateResponse(@NonNull UpdateMethods caller, @NonNull Context ctx) {
        this.caller = caller;
        this.info = new ResponseProgressStrings(ctx);
    }

    @Override
    public void onResponse(@NonNull Call<ContentPackResponse> call, @NonNull Response<ContentPackResponse> response) {
        if (!response.isSuccessful()) {
            call.cancel();
            Log.w(LOG_TAG, "Network-call not successful. Status-Code: " + response.code());

            try {
                Log.w(LOG_TAG, "Additional error-info: " + response.errorBody().string());
            } catch (Exception e) {
                Log.w(LOG_TAG, "Can't convert error-body to string", e);
            }

            caller.updatesFinished(false);
            return;
        }

        ContentPackResponse cpr = response.body();

        if (cpr == null) {
            Log.w(LOG_TAG, "Got null as message! Not correct!");
            caller.showLongToast(info.UPDATE_ERROR);
            caller.updatesFinished(false);
            return;
        }

        Log.i(LOG_TAG, "Received Response. Message: " + cpr.getStatus());

        List<ContentPack> remotePacks = cpr.getMsg();
        if (remotePacks == null) {
            Log.w(LOG_TAG, "Got null as message! Not correct!");
            caller.showLongToast(info.UPDATE_ERROR);
            caller.updatesFinished(false);
            return;
        }

        Log.d(LOG_TAG, remotePacks.toString());

        AppState state = AppState.getInstance();

        Set<ContentPack> availablePacks = state.getPacks();
        Set<ContentPack> packsToSet = new HashSet<>();

        for (ContentPack availablePack : availablePacks) {
            for (ContentPack remotePack : remotePacks) {
                if (BuildConfig.DEBUG) {
                    Log.i(LOG_TAG, "Available: " + availablePack.toString());
                    Log.i(LOG_TAG, "Remote: " + remotePack.toString());
                    Log.i(LOG_TAG, availablePack.getId() + " == " + remotePack.getId() + " -> " + (availablePack.getId().intValue() == remotePack.getId().intValue()));
                }

                if (availablePack.getId().intValue() == remotePack.getId().intValue()) {
                    if (availablePack.getVersion() >= remotePack.getVersion()) {
                        Log.d(LOG_TAG, "{ Pack=\"" + availablePack.getName() + "\", ID=" + availablePack.getId() + " } is already available with the same or higher version.");
                        remotePacks.remove(remotePack);
                        packsToSet.add(remotePack);
                    }
                    break;
                }
            }
        }

        if (remotePacks.size() == 0) {
            Log.i(LOG_TAG, "Update finished. No new packs found!");
            caller.setMainProgressVisible(false);
            caller.showLongToast(info.UP_TO_DATE);
            caller.updatesFinished(true);
            return;
        }

        Log.i(LOG_TAG, "Found " + remotePacks.size() + " new Pack(s). Getting Questions now.");

        caller.setMainProgressProgress(false, 5);
        caller.setInfoText(info.DOWNLOADING_UPDATES);

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
                Call<QuestionResponse> questionRequest = RequesterService.getInstance().getQuestions(newPack.getId());
                QuestionResponse qr = questionRequest.execute().body();

                if (qr == null) {
                    Log.w(LOG_TAG, "Response for id=" + newPack.getId() + " was null");
                    continue;
                }

                if (qr.getError()) {
                    Log.w(LOG_TAG, "Get Questions for pack: \"" + newPack.getId() + "\" with version \"" + newPack.getVersion() + "\" failed.");
                    break;
                }

                List<Question> questions = qr.getMsg();
                if (questions != null) questionsToSet.addAll(questions);

                totalProgress += progressInc;
                caller.setMainProgressProgress(false, totalProgress);
            } catch (Exception e) {
                Log.w(LOG_TAG, "Exception occurred while getting Updates!", e);
            }
        }
        packsToSet.addAll(remotePacks);
        caller.setInfoText(info.STORING_UPDATES_TO_PHONE);
        state.setPacks(packsToSet);
        state.setQuestions(questionsToSet);

        if (state.saveState()) {
            caller.setMainProgressProgress(false, 100);
            Log.i(LOG_TAG, "Saved AppState after Updates!");
            caller.showLongToast(info.UPDATE_SUCCESS);
            caller.updatesFinished(true);
        } else {
            Log.w(LOG_TAG, "Could not save AppState!");
            caller.showLongToast(info.UPDATE_ERROR);
            caller.updatesFinished(false);
        }
    }

    @Override
    public void onFailure(@NonNull Call<ContentPackResponse> call, @NonNull Throwable t) {
        call.cancel();

        if (t instanceof IOException) {
            Log.w(LOG_TAG, "Network failure!");
            caller.showLongToast(info.SERVER_DOWN);
        } else {
            Log.w(LOG_TAG, "Exception occurred while fetching server-data!", t);
        }

        caller.setMainProgressVisible(false);
        caller.updatesFinished(false);
    }

    public interface UpdateMethods {
        void setInfoText(String txt);

        void showLongToast(String text);

        void updatesFinished(boolean result);

        void setMainProgressVisible(boolean isVisible);

        void setMainProgressProgress(boolean indeterminate, int progress);
    }

    private static class ResponseProgressStrings {

        final String UP_TO_DATE;
        final String SERVER_DOWN;
        final String UPDATE_ERROR;
        final String UPDATE_SUCCESS;
        final String CHECKING_UPDATES;
        final String DOWNLOADING_UPDATES;
        final String STORING_UPDATES_TO_PHONE;

        ResponseProgressStrings(@NonNull Context ctx) {
            UP_TO_DATE = ctx.getString(R.string.info_up_to_date);
            SERVER_DOWN = ctx.getString(R.string.info_server_down);
            UPDATE_ERROR = ctx.getString(R.string.info_update_error);
            UPDATE_SUCCESS = ctx.getString(R.string.info_update_success);
            CHECKING_UPDATES = ctx.getString(R.string.info_checking_updates);
            DOWNLOADING_UPDATES = ctx.getString(R.string.info_downloading_updates);
            STORING_UPDATES_TO_PHONE = ctx.getString(R.string.info_storing_updates_to_phone);
        }
    }
}
