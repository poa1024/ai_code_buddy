package io.github.poa1024.conversation;

import io.github.poa1024.conversation.model.Answer;
import io.github.poa1024.conversation.model.QARound;
import io.github.poa1024.conversation.model.Question;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GptConversation {

    @Getter
    private final List<QARound> history = new ArrayList<>();

    private GptConversation() {

    }

    public static GptConversation start(Question question) {
        var res = new GptConversation();
        res.history.add(new QARound(question));
        return res;
    }

    public void addAnswer(Answer answer) {
        getLast().setAnswer(answer);
    }

    public QARound getLast() {
        return history.get(history.size() - 1);
    }


}
