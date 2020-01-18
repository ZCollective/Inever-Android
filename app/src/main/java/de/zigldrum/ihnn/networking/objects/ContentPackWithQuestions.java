package de.zigldrum.ihnn.networking.objects;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContentPackWithQuestions extends ContentPack implements Serializable {

    private static final long serialVersionUid = 1L;

    private List<Question> questions;

    public ContentPackWithQuestions(int id, String name, String description, String keywords, int minAge, int version, @NonNull List<Question> possibleQuestions) {
        super(id, name, description, keywords, minAge, version);
        questions = new ArrayList<>();
        possibleQuestions.stream().filter(q -> q.getPackid() == id).forEach(q -> questions.add(q));
    }

    public boolean hasQuestions() {
        return !questions.isEmpty();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void refreshQuestions(@NonNull List<Question> newQuestions) {
        newQuestions.stream().filter(q -> (Objects.equals(q.getPackid(), getId()) && (!questions.contains(q)))).forEach(q -> questions.add(q));
    }
}
