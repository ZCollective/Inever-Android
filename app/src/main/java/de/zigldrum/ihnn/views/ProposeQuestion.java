package de.zigldrum.ihnn.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.tasks.SendProposal;
import de.zigldrum.ihnn.utils.Utils;

public class ProposeQuestion extends AppCompatActivity {

    private static final String LOG_TAG = "ProposeQuestion";

    public String questionString;
    public String senderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propose_question);
    }

    public void goBack(View v) {
        finish();
    }

    public void sendProposal (View v) {
        Log.d(LOG_TAG, "Currently a stub!");
        EditText string = findViewById(R.id.proposal_string);
        EditText sender = findViewById(R.id.proposal_sender);
        questionString = string.getText().toString();
        senderName = sender.getText().toString();
        SendProposal sp = new SendProposal();
        sp.execute(this);

    }

    public void proposalSent(boolean success) {
        EditText string = findViewById(R.id.proposal_string);
        EditText sender = findViewById(R.id.proposal_sender);
        string.setText("");
        sender.setText("");
        if (success) {
            Utils.showLongToast(getApplicationContext(), getResources().getString(R.string.info_proposal_sent_success));
        }
    }
}
