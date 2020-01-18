package de.zigldrum.ihnn.networking.tasks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.networking.objects.ProposalResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andreas Knipl <Andreas.Knipl@medicospeaker.com>
 */
public class CheckProposalResponse implements Callback<ProposalResponse> {

    private static final String LOG_TAG = "CheckProposalResponse";

    private final ProposalResponseMethods caller;
    private final String infoServerDown;

    public CheckProposalResponse(@NonNull ProposalResponseMethods caller, @NonNull Context ctx) {
        this.caller = caller;
        this.infoServerDown = ctx.getResources().getString(R.string.info_server_down);
    }

    @Override
    public void onResponse(@NonNull Call<ProposalResponse> call, @NonNull Response<ProposalResponse> response) {
        if (!response.isSuccessful()) {
            call.cancel();
            Log.w(LOG_TAG, "Network-call not successful. Status-Code: " + response.code());

            try {
                Log.w(LOG_TAG, "Additional error-info: " + response.errorBody().string());
            } catch (IOException ioe) {
                Log.w(LOG_TAG, "Can't convert error-body to string", ioe);
            }

            caller.proposalSent(false);
            return;
        }

        ProposalResponse pr = response.body();

        if (pr == null) {
            Log.w(LOG_TAG, "Got null as message! Not correct!");
            caller.proposalSent(false);
        } else {
            Log.d(LOG_TAG, "Received Response. Message: " + pr.getStatus());
            caller.proposalSent(pr.getSuccess());
        }
    }

    @Override
    public void onFailure(Call<ProposalResponse> call, Throwable t) {
        call.cancel();

        if (t instanceof IOException) {
            Log.w(LOG_TAG, "Network failure!");
            caller.notifyUser(infoServerDown);
            caller.hideProgressBar();
        } else {
            Log.d(LOG_TAG, "Exception occurred while sending proposal!", t);
        }

        caller.proposalSent(false);
    }

    public interface ProposalResponseMethods {
        void proposalSent(boolean success);

        void notifyUser(String message);

        void hideProgressBar();
    }
}