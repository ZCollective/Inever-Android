package de.zigldrum.ihnn.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.networking.objects.ProposalRequestBody;
import de.zigldrum.ihnn.networking.objects.ProposalResponse;
import de.zigldrum.ihnn.networking.services.ContentService;
import de.zigldrum.ihnn.networking.services.RequesterService;
import de.zigldrum.ihnn.networking.tasks.CheckProposalResponse;
import de.zigldrum.ihnn.utils.Utils;
import retrofit2.Call;

public class ProposeQuestion extends AppCompatActivity implements CheckProposalResponse.ProposalResponseMethods {

    private static final String LOG_TAG = "ProposeQuestion";

    private TextInputLayout string;
    private TextInputLayout sender;
    private String errorEmptyProposal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propose_question);

        Log.i(LOG_TAG, "Running ProposeQuestion::onCreate()");

        string = findViewById(R.id.proposal_string);
        sender = findViewById(R.id.proposal_sender);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Log.i(LOG_TAG, "Running ProposeQuestion::onPostCreate()");

        errorEmptyProposal = getString(R.string.proposal_string_empty);

        Objects.requireNonNull(string.getEditText());
        Objects.requireNonNull(sender.getEditText());

        string.getEditText().setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) string.setError(null);
        });
    }

    public void goBack(View v) {
        finish();
    }

    public void sendProposal(View v) {
        Objects.requireNonNull(string.getEditText());
        Objects.requireNonNull(sender.getEditText());

        String questionString = string.getEditText().getText().toString().trim();
        String senderName = sender.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(questionString)) {
            string.setError(errorEmptyProposal);
            notifyUser(getString(R.string.proposal_string_empty));
        } else {
            string.setError(null);

            // This enables us to send multiple proposals after each other
            AsyncTask.SERIAL_EXECUTOR.execute(() -> {
                // Make Backend-Request
                ContentService backendConn = RequesterService.getInstance();
                ProposalRequestBody requestBody = new ProposalRequestBody(questionString, senderName);
                Call<ProposalResponse> request = backendConn.proposeQuestion(requestBody);
                CheckProposalResponse responseChecker = new CheckProposalResponse(this, this);
                request.enqueue(responseChecker);
            });
        }
    }

    @Override
    public void proposalSent(boolean success) {
        Objects.requireNonNull(string.getEditText());
        Objects.requireNonNull(sender.getEditText());

        string.getEditText().getText().clear();
        sender.getEditText().getText().clear();

        if (success) notifyUser(getString(R.string.info_proposal_sent_success));
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
