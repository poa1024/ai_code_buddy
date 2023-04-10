package io.github.poa1024.sesssion;

import io.github.poa1024.sesssion.model.Answer;
import io.github.poa1024.sesssion.model.QARound;
import io.github.poa1024.sesssion.model.Question;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GptSession {

    private final List<QARound> history = new ArrayList<>();
    private final String context;
    private final AnswerHandler answerHandler;

    public void addQuestion(Question question) {
        history.add(new QARound(question));
    }

    public void addAnswer(Answer answer) {
        answerHandler.call(answer);
        getLast().setAnswer(answer);
    }

    private QARound getLast() {
        return history.get(history.size() - 1);
    }

}
