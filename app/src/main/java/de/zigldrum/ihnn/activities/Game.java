package de.zigldrum.ihnn.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.networking.objects.Question;
import de.zigldrum.ihnn.utils.AppState;
import de.zigldrum.ihnn.utils.Constants.AgeRestrictions;
import de.zigldrum.ihnn.utils.Constants.GameResults;

public class Game extends AppCompatActivity {

    private static final String LOG_TAG = "Game";

    private final AppState state = AppState.getInstance();

    private volatile Iterator<Question> questionIterator;
    private volatile List<Question> questions = new ArrayList<>();
    private volatile boolean shownRepeatOption = false;

    private TextView questionDisplay;
    private Button nextButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Log.i(LOG_TAG, "Running Game::onCreate()");

        questionDisplay = findViewById(R.id.question_text);
        nextButton = findViewById(R.id.btn_game_next);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Log.i(LOG_TAG, "Running Game::onPostCreate()");

        addFilteredQuestions();

        boolean isEmpty = questions.isEmpty();
        setResult(isEmpty ? GameResults.GAME_QUESTIONS_EMPTY : GameResults.GAME_DEFAULT);
        if (isEmpty) finish();

        questionIterator = questions.iterator();
    }

    public void showNext(View v) {
        // This increases our ability to handle fast clicking, otherwise we might crash
        AsyncTask.SERIAL_EXECUTOR.execute(() -> {
            Log.d(LOG_TAG, "Getting next Question!");

            if (shownRepeatOption) {
                Log.d(LOG_TAG, "Starting a new round!");
                runOnUiThread(() -> nextButton.setText(R.string.btn_next));
                Collections.shuffle(questions);
                questionIterator = questions.iterator();
                shownRepeatOption = false;
            }

            if (questionIterator.hasNext()) {
                Question nextQuestion = questionIterator.next();
                runOnUiThread(() -> questionDisplay.setText(nextQuestion.getString()));
            } else {
                Log.d(LOG_TAG, "Last question in selection!");

                runOnUiThread(() -> {
                    questionDisplay.setText(R.string.game_no_questions_left);
                    nextButton.setText(R.string.btn_game_repeat);
                });
                shownRepeatOption = true;
            }
        });
    }

    public void goBack(View view) {
        Log.i(LOG_TAG, "Bye bye :(");
        finish();
    }

    private void addFilteredQuestions() {
        questions.clear();

        List<ContentPack> enabledPacks = state.getPacks()
                                              .stream()
                                              .filter(p -> !state.getDisabledPacks().contains(p.getId()))
                                              .collect(Collectors.toList());

        if (state.isNSFWOnly()) {
            Log.d(LOG_TAG, "Ignoring other age settings. NSFWOnly mode overrides!");
            List<Integer> nsfwPackIDs = enabledPacks
                    .stream()
                    .filter(p -> p.getMinAge() >= AgeRestrictions.NSFW_BORDER)
                    .map(ContentPack::getId)
                    .collect(Collectors.toList());

            state.getQuestions()
                 .stream()
                 .filter(q -> nsfwPackIDs.contains(q.getPackid()))
                 .forEach(questions::add);
        } else if (state.getNSFWEnabled()) {
            Log.d(LOG_TAG, "NSFW Mode enabled.");
            List<Integer> enabledPackIDs = enabledPacks
                    .stream()
                    .map(ContentPack::getId)
                    .collect(Collectors.toList());

            state.getQuestions()
                 .stream()
                 .filter(q -> enabledPackIDs.contains(q.getPackid()))
                 .forEach(questions::add);
        } else {
            Log.d(LOG_TAG, "NON-NSFW Mode enabled.");
            List<Integer> nonNsfwPackIDs = enabledPacks
                    .stream()
                    .filter(p -> p.getMinAge() < AgeRestrictions.NSFW_BORDER)
                    .map(ContentPack::getId)
                    .collect(Collectors.toList());

            state.getQuestions()
                 .stream()
                 .filter(q -> nonNsfwPackIDs.contains(q.getPackid()))
                 .forEach(questions::add);
        }

        Collections.shuffle(questions);
    }
}
