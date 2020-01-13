package de.zigldrum.ihnn.tasks;

import android.os.AsyncTask;

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
import de.zigldrum.ihnn.objects.ProposalRequestBody;
import de.zigldrum.ihnn.objects.ProposalResponse;
import de.zigldrum.ihnn.objects.Question;
import de.zigldrum.ihnn.objects.QuestionResponse;
import de.zigldrum.ihnn.services.RequesterService;
import de.zigldrum.ihnn.utils.Utils;
import de.zigldrum.ihnn.views.ProposeQuestion;
import retrofit2.Call;

public class SendProposal extends AsyncTask<ProposeQuestion, String, Boolean> {
    private ProposeQuestion app;
    @Override
    protected Boolean doInBackground(ProposeQuestion... activities) {
        app = activities[0];
        try {
            Call<ProposalResponse> request = RequesterService.buildContentService().proposeQuestion(new ProposalRequestBody(app.questionString, app.senderName));
            ProposalResponse pr = request.execute().body();
            System.out.println("Received Response. Message: Success:" + pr.getSuccess() + " | Error: " + pr.getError());
            return new Boolean(pr.getSuccess());
        } catch (ConnectException ce) {
            System.out.println("Server is down!");
            publishProgress(app.getResources().getString(R.string.info_server_down));
            Utils.setMainProgressVisible(app, false);
            return new Boolean(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred in SendProposal Task!");
            return new Boolean(false);
        } finally{
        }
    }
    @Override
    protected void onPostExecute(Boolean result) {
        app.proposalSent(result.booleanValue());
    }

    @Override
    protected void onProgressUpdate(String... messages){
        Utils.showLongToast(app.getApplicationContext(), messages[0]);
    }
}
