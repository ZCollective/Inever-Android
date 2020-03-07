package de.zigldrum.ihnn.networking.tasks;

import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import static de.zigldrum.ihnn.utils.Constants.UpdateProgress.DOWNLOADED_NEW_CONTENT_PACKS;
import static de.zigldrum.ihnn.utils.Constants.UpdateProgress.EVALUATED_CONTENT_PACKS;
import static de.zigldrum.ihnn.utils.Constants.UpdateProgress.GOT_VALID_RESPONSE;
import static de.zigldrum.ihnn.utils.Constants.UpdateProgress.START;

public class CheckUpdateResponse implements Callback<ContentPackResponse> {

    private static final String LOG_TAG = "CheckUpdateResponse";

    private final UpdateMethods caller;
    private final AppState state = AppState.getInstance();

    public CheckUpdateResponse(@NonNull UpdateMethods caller) {
        this.caller = caller;
    }

    @Override
    public void onResponse(@NonNull Call<ContentPackResponse> call,
                           @NonNull Response<ContentPackResponse> response) {
        Log.i(LOG_TAG, "Running CheckUpdateResponse::onResponse()");

        try {
            caller.setNetworkingProgress(false, START);

            if (!isRequestSuccessful(call, response)) return;

            ContentPackResponse cpr = response.body();
            if (!validResponseBody(cpr)) return;

            Log.i(LOG_TAG, "Received Response. Message: " + cpr.getStatus());

            List<ContentPack> remotePacks = cpr.getMsg();
            if (!validResponseAttribute(remotePacks)) return;

            Log.d(LOG_TAG, remotePacks.toString());

            Set<ContentPack> packsToSet = getPackDifferences(remotePacks);

            if (!newPacksAvailable(remotePacks.size())) return;

            Log.i(LOG_TAG, "Found new pack(s): amount=" + remotePacks.size() + ". Getting " +
                           "Questions now.");

            Set<Question> questionsToSet = downloadQuestions(remotePacks);
            packsToSet.addAll(remotePacks);

            saveChanges(packsToSet, questionsToSet);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception occurred processing update-response", e);
        }
    }

    private Set<ContentPack> getPackDifferences(List<ContentPack> remotePacks) {
        Set<ContentPack> packsToSet = new HashSet<>();

        for (ContentPack availablePack : state.getPacks()) {
            for (ContentPack remotePack : remotePacks) {
                if (BuildConfig.DEBUG) {
                    Log.i(LOG_TAG, "Available: " + availablePack.toString());
                    Log.i(LOG_TAG, "Remote: " + remotePack.toString());
                    Log.i(LOG_TAG, availablePack.getId() + " == " + remotePack.getId() + " -> " +
                                   (availablePack.getId().intValue() ==
                                    remotePack.getId().intValue()));
                }

                if (availablePack.getId().intValue() == remotePack.getId().intValue()) {
                    if (availablePack.getVersion() >= remotePack.getVersion()) {
                        Log.d(LOG_TAG, "{ Pack=\"" + availablePack.getName() + "\", CP_ID=" +
                                       availablePack.getId() +
                                       " } is already available with the same or higher version.");
                        remotePacks.remove(remotePack);
                        packsToSet.add(remotePack);
                    }
                    break;
                }
            }
        }

        return packsToSet;
    }

    private Set<Question> downloadQuestions(
            @NonNull List<ContentPack> remotePacks) throws IllegalStateException {
        Set<Question> questionsToSet = state.getQuestions().stream()
                                            .filter(q -> remotePacks.stream().noneMatch(p ->
                                                    p.getId().intValue() ==
                                                    q.getPackid().intValue()))
                                            .collect(Collectors.toSet());

        for (ContentPack newPack : remotePacks) {

            try {
                Call<QuestionResponse> questionRequest =
                        RequesterService.getInstance().getQuestions(newPack.getId());
                QuestionResponse qr = questionRequest.execute().body();

                if (qr == null) {
                    Log.w(LOG_TAG, "Response was null for id=" + newPack.getId());
                    continue;
                }

                if (qr.getError()) {
                    Log.w(LOG_TAG,
                            "Obtaining questions failed for pack: id=" + newPack.getId() + ", " +
                            "version=" + newPack.getVersion());

                    throw new IllegalStateException(
                            "Got an error for pack: id=" + newPack.getId() + ", version=" +
                            newPack.getVersion());
                }

                List<Question> questions = qr.getMsg();
                if (questions != null) questionsToSet.addAll(questions);
            } catch (Exception e) {
                Log.w(LOG_TAG, "Exception occurred while getting Updates!", e);

                throw new IllegalStateException(
                        "Got an exception for pack: id=" + newPack.getId() + ", version=" +
                        newPack.getVersion());
            }
        }

        return questionsToSet;
    }

    private boolean isRequestSuccessful(@NonNull Call<ContentPackResponse> call,
                                        @NonNull Response<ContentPackResponse> response) {
        if (!response.isSuccessful()) {
            call.cancel();
            Log.w(LOG_TAG, "Network-call not successful. Status-Code: " + response.code());

            try {
                Log.w(LOG_TAG, "Additional error-info: " + response.errorBody().string());
            } catch (Exception e) {
                Log.w(LOG_TAG, "Can't convert error-body to string", e);
            }

            caller.updateFinished(false);
            return false;
        }

        return true;
    }

    private boolean validResponseBody(@Nullable ContentPackResponse response) {
        if (response == null) {
            Log.w(LOG_TAG, "Got null as message! Not correct!");
            caller.showLongSnackbar(R.string.info_update_error);
            caller.updateFinished(false);
            return false;
        } else {
            caller.setNetworkingProgress(false, GOT_VALID_RESPONSE);
            return true;
        }
    }

    private boolean validResponseAttribute(@Nullable List<ContentPack> remotePacks) {
        if (remotePacks == null) {
            Log.w(LOG_TAG, "Got null as message! Not correct!");
            caller.showLongSnackbar(R.string.info_update_error);
            caller.updateFinished(false);
            return false;
        }

        return true;
    }

    private boolean newPacksAvailable(int remotePackages) {
        if (remotePackages == 0) {
            Log.i(LOG_TAG, "Update finished. No new packs found!");
            caller.setNetworkingProgressVisibility(false);
            caller.showLongSnackbar(R.string.info_up_to_date);
            caller.updateFinished(true);
            return false;
        } else {
            caller.setNetworkingProgress(false, EVALUATED_CONTENT_PACKS);
            caller.setNetworkingInfoText(R.string.info_downloading_updates);
            return true;
        }
    }

    private void saveChanges(@NonNull Set<ContentPack> contentPacks,
                             @NonNull Set<Question> questions) {
        caller.setNetworkingProgress(false, DOWNLOADED_NEW_CONTENT_PACKS);
        caller.setNetworkingInfoText(R.string.info_storing_updates_to_phone);

        state.setPacks(contentPacks);
        state.setQuestions(questions);

        if (state.saveState()) {
            caller.setNetworkingProgress(false, 100);
            Log.i(LOG_TAG, "Saved AppState after Updates!");
            caller.showLongSnackbar(R.string.info_update_success);
            caller.updateFinished(true);
        } else {
            Log.w(LOG_TAG, "Could not save AppState!");
            caller.showLongSnackbar(R.string.info_update_error);
            caller.updateFinished(false);
        }
    }

    @Override
    public void onFailure(@NonNull Call<ContentPackResponse> call, @NonNull Throwable t) {
        Log.i(LOG_TAG, "Running CheckUpdateResponse::onFailure()");

        call.cancel();

        if (t instanceof IOException) {
            Log.w(LOG_TAG, "Network failure!");
            caller.showLongSnackbar(R.string.info_server_down);
        } else {
            Log.w(LOG_TAG, "Exception occurred while fetching server-data!", t);
        }

        caller.setNetworkingProgressVisibility(false);
        caller.updateFinished(false);
    }

    public interface UpdateMethods {
        <T> void setNetworkingInfoText(@NonNull T text);

        <T> void showLongSnackbar(@NonNull T text);

        void updateFinished(boolean result);

        void setNetworkingProgressVisibility(boolean isVisible);

        void setNetworkingProgress(boolean indeterminate,
                                   @IntRange(from = 0, to = 100) int progress);
    }
}
