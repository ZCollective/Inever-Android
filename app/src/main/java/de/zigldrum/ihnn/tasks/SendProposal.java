package de.zigldrum.ihnn.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.net.ConnectException;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.objects.ProposalRequestBody;
import de.zigldrum.ihnn.objects.ProposalResponse;
import de.zigldrum.ihnn.services.RequesterService;
import de.zigldrum.ihnn.utils.Utils;
import de.zigldrum.ihnn.views.ProposeQuestion;
import retrofit2.Call;

public class SendProposal extends AsyncTask<ProposeQuestion, String, Boolean> {

    private static final String LOG_TAG = "SendProposal";

    private ProposeQuestion app;

    @Override
    protected Boolean doInBackground(ProposeQuestion... activities) {
        app = activities[0];
        try {
            Call<ProposalResponse> request = RequesterService.buildContentService().proposeQuestion(new ProposalRequestBody(app.questionString, app.senderName));
            ProposalResponse pr = request.execute().body();
            Log.i(LOG_TAG, "Received Response. Message: Success:" + pr.getSuccess() + " | Error: " + pr.getError());
            return new Boolean(pr.getSuccess());
        } catch (ConnectException ce) {
            Log.w(LOG_TAG, "Server is down!", ce);
            publishProgress(app.getResources().getString(R.string.info_server_down));
            Utils.setMainProgressVisible(app, false);
            return new Boolean(false);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception occurred in SendProposal Task!", e);
            return new Boolean(false);
        } finally {
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        app.proposalSent(result.booleanValue());
    }

    @Override
    protected void onProgressUpdate(String... messages) {
        Utils.showLongToast(app.getApplicationContext(), messages[0]);
    }
}
