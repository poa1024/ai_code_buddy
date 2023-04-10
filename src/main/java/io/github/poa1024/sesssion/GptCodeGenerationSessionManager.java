package io.github.poa1024.sesssion;

import io.github.poa1024.Configuration;
import io.github.poa1024.gpt.GptClient;
import io.github.poa1024.gpt.GptQuestionBuilder;
import io.github.poa1024.sesssion.model.Answer;
import io.github.poa1024.sesssion.model.QARound;
import io.github.poa1024.sesssion.model.Question;
import io.github.poa1024.ui.GptSessionWindow;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GptCodeGenerationSessionManager {

    private final GptClient gptClient = Configuration.GPT_CLIENT;
    private final GptQuestionBuilder gptQuestionBuilder = Configuration.GPT_QUESTION_BUILDER;

    private GptSessionWindow conversationWindow;
    private GptSession gptSession;

    public void setSessionWindow(GptSessionWindow conversationWindow) {
        this.conversationWindow = conversationWindow;
        updateView();
    }

    public void openNewSession(String context, AnswerHandler answerHandler) {
        this.gptSession = new GptSession(context, answerHandler);
    }

    public void askQuestion(Question question) {
        var gptRequest = createRequest(question);
        var gptResponse = gptClient
                .ask(gptRequest)
                .getFirstChoice();
        var answer = new Answer(gptResponse, "code was generated successfully");
        gptSession.addQuestion(question);
        updateView();
        gptSession.addAnswer(answer);
        updateView();
    }

    private void updateView() {
        if (conversationWindow != null && gptSession != null) {
            conversationWindow.printConversation(gptSession.getHistory());
        }
    }

    private String createRequest(Question question) {
        String generateCodeRequest;
        if (gptSession.getHistory().isEmpty()) {
            generateCodeRequest = gptQuestionBuilder.askToGenerateCode(question.getText(), gptSession.getContext());
        } else {
            generateCodeRequest = gptQuestionBuilder.askToGenerateCode(
                    question.getText(),
                    gptSession.getContext(),
                    mapHistory(gptSession.getHistory())
            );
        }
        return generateCodeRequest;
    }

    private static List<String> mapHistory(List<QARound> history) {
        return history
                .stream()
                .flatMap(qa -> Stream.of(
                        "Q:" + qa.getQuestion().getText(),
                        "A:" + qa.getAnswer().getText()
                ))
                .collect(Collectors.toList());
    }
}
