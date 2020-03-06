package de.zigldrum.ihnn.networking.objects;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContentPackWithQuestions extends ContentPack {

    private final List<Question> questions = new ArrayList<>();

    public ContentPackWithQuestions(int id, String name, String description, String keywords,
                                    @IntRange(from = 0) int minAge, @IntRange(from = 0) int version,
                                    @NonNull List<Question> possibleQuestions) {
        super(id, name, description, keywords, minAge, version);
        possibleQuestions.stream().filter(q -> q.getPackid() == id).forEach(questions::add);
    }

    public boolean hasQuestions() {
        return !questions.isEmpty();
    }

    @NonNull
    public List<Question> getQuestions() {
        return questions;
    }

    public void refreshQuestions(@NonNull List<Question> newQuestions) {
        newQuestions.stream().filter(q -> (
                Objects.equals(q.getPackid(), getId()) && (!questions.contains(q))
        )).forEach(questions::add);
    }
}
