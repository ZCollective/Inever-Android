package de.zigldrum.ihnn.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.objects.AppState;
import de.zigldrum.ihnn.objects.ContentPack;
import de.zigldrum.ihnn.objects.Question;
import de.zigldrum.ihnn.utils.Utils;

import static de.zigldrum.ihnn.utils.Constants.AgeRestrictions.NSFW_BORDER;

public class Game extends AppCompatActivity {

    private static final String LOG_TAG = "Game";

    public AppState state;

    private List<Question> questions;

    private boolean shownRepeatOption = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.i(LOG_TAG, "Game was created!");
        Log.d(LOG_TAG, "Message: " + getIntent().getStringExtra("msg"));
        state = AppState.loadState(getFilesDir());
        questions = getAndFilterQuestions();
        if (questions.isEmpty()) {
            Utils.showLongToast(getApplicationContext(), getResources().getString(R.string.info_no_questions_available));
            finish();
        }
    }

    public void showNext(View v) {
        Log.d(LOG_TAG, "Getting next Question!");
        TextView questionDisplay = findViewById(R.id.question_text);
        if (questions.isEmpty()) {
            if (!shownRepeatOption) {
                questionDisplay.setText(R.string.game_no_questions_left);
                Button nextButton = findViewById(R.id.btn_game_next);
                nextButton.setText(R.string.btn_game_repeat);
                shownRepeatOption = true;
            } else {
                questions = getAndFilterQuestions();
                Button nextButton = findViewById(R.id.btn_game_next);
                nextButton.setText(R.string.btn_next);
                shownRepeatOption = false;
                Question question = questions.remove((int) (Math.random() * questions.size()));
                questionDisplay.setText(question.getString());
            }
        } else {
            Question question = questions.remove((int) (Math.random() * questions.size()));
            questionDisplay.setText(question.getString());
        }
    }

    public void goBack(View view) {
        Log.i(LOG_TAG, "Bye bye :(");
        finish();
    }

    private List<Question> getAndFilterQuestions() {
        List<ContentPack> enabledPacks = state.getPacks().stream().filter(p -> !state.getDisabledPacks().contains(p.getId())).collect(Collectors.toList());
        if (state.isOnlyNSFW()) {
            Log.d(LOG_TAG, "Ignoring other age settings. NSFWOnly mode overrides!");
            List<Integer> nsfwPackIDs = enabledPacks.stream().filter(p -> p.getMinAge() >= NSFW_BORDER).map(p -> p.getId()).collect(Collectors.toList());
            List<Question> nsfwQuestions = state.getQuestions().parallelStream().filter(q -> nsfwPackIDs.contains(q.getPackid())).collect(Collectors.toList());
            return nsfwQuestions;
        } else if (state.getEnableNSFW()) {
            List<Question> questions = new ArrayList<>();
            List<Integer> enabledPackIDs = enabledPacks.stream().map(p -> p.getId()).collect(Collectors.toList());
            List<Question> filtered = state.getQuestions().parallelStream().filter(q -> enabledPackIDs.contains(q.getPackid())).collect(Collectors.toList());
            return filtered;
        } else {
            Log.d(LOG_TAG, "NON NSFW Mode enabled.");
            List<Integer> nonNsfwPackIDs = enabledPacks.stream().filter(p -> p.getMinAge() < NSFW_BORDER).map(p -> p.getId()).collect(Collectors.toList());
            List<Question> nonNsfwQuestions = state.getQuestions().parallelStream().filter(q -> nonNsfwPackIDs.contains(q.getPackid())).collect(Collectors.toList());

            return nonNsfwQuestions;
        }
    }

}
