package de.zigldrum.ihnn.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.networking.objects.ProposalRequestBody;
import de.zigldrum.ihnn.networking.objects.ProposalResponse;
import de.zigldrum.ihnn.networking.services.ContentService;
import de.zigldrum.ihnn.networking.services.RequesterService;
import de.zigldrum.ihnn.networking.tasks.CheckProposalResponse;
import de.zigldrum.ihnn.utils.Utils;
import retrofit2.Call;

public class ProposeQuestion extends AppCompatActivity implements CheckProposalResponse.ProposalResponseMethods {

    private EditText string;
    private EditText sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propose_question);

        string = findViewById(R.id.proposal_string);
        sender = findViewById(R.id.proposal_sender);
    }

    public void goBack(View v) {
        finish();
    }

    public void sendProposal(View v) {
        String questionString = string.getText().toString();
        String senderName = sender.getText().toString();

        ContentService backendConn = RequesterService.getContentService();
        ProposalRequestBody requestBody = new ProposalRequestBody(questionString, senderName);
        Call<ProposalResponse> request = backendConn.proposeQuestion(requestBody);
        CheckProposalResponse responseChecker = new CheckProposalResponse(this, this);
        request.enqueue(responseChecker);
    }

    @Override
    public void proposalSent(boolean success) {
        string.setText("");
        sender.setText("");

        if (success) {
            Utils.showLongToast(getApplicationContext(), getResources().getString(R.string.info_proposal_sent_success));
        }
    }

    @Override
    public void notifyUser(String message) {
        Utils.showLongToast(getApplicationContext(), message);
    }

    @Override
    public void hideProgressBar() {
        Utils.setMainProgressVisible(this, false);
    }
}
