package de.zigldrum.ihnn.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContentPackWithQuestions extends ContentPack implements Serializable {

    private static final long serialVersionUid = 1L;

    private List<Question> questions;

    public ContentPackWithQuestions(int id, String name, String description, String keywords, int minAge, int version, List<Question> possibleQuestions) {
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

    public void refreshQuestions(List<Question> newQuestions) {
        newQuestions.stream().filter(q -> (q.getPackid() == getId() && (!questions.contains(q)))).forEach(q -> questions.add(q));
    }
}
