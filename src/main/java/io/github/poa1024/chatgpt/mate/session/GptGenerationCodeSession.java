package io.github.poa1024.chatgpt.mate.session;

import io.github.poa1024.chatgpt.mate.Configuration;
import io.github.poa1024.chatgpt.mate.Executor;
import io.github.poa1024.chatgpt.mate.GptRequestBuilder;
import io.github.poa1024.chatgpt.mate.gptclient.GptClient;
import io.github.poa1024.chatgpt.mate.model.HumanReadableText;
import io.github.poa1024.chatgpt.mate.session.model.GptInteraction;
import io.github.poa1024.chatgpt.mate.session.model.GptRequest;
import io.github.poa1024.chatgpt.mate.session.model.GptResponse;
import io.github.poa1024.chatgpt.mate.util.TextUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class GptGenerationCodeSession extends GptSession {

    private final GptRequestBuilder gptQuestionBuilder = Configuration.GPT_REQUEST_BUILDER;

    private final GeneratedCodeHandler generatedCodeHandler;
    private final String before;
    private final String after;

    public interface GeneratedCodeHandler {
        void handle(String code, String contextWithCode, int codeOffset);
    }

    public GptGenerationCodeSession(
            GeneratedCodeHandler generatedCodeHandler,
            GptClient gptClient,
            Executor executor,
            String context,
            int start,
            int end
    ) {
        super(gptClient, executor, context);
        this.generatedCodeHandler = generatedCodeHandler;
        this.before = context.substring(0, start);
        this.after = context.substring(end);
    }

    @Override
    protected GptRequest createRequest(String userInput) {
        String gptRequest;
        if (history.isEmpty()) {
            gptRequest = gptQuestionBuilder.askToGeneratedCode(userInput);
        } else {
            gptRequest = gptQuestionBuilder.askToChangeGeneratedCode(userInput);
            gptRequest = gptQuestionBuilder.appendPreviouslyGenerateCode(
                    gptRequest, history.stream()
                            .map(GptInteraction::requireGptResponse)
                            .map(GptResponse::getText)
                            .collect(Collectors.toList())

            );
        }
        gptRequest = gptQuestionBuilder.appendContext(gptRequest, initialContext);
        return GptRequest.builder()
                .question(new HumanReadableText(userInput))
                .body(gptRequest)
                .build();
    }

    @Override
    protected void handleResponse(GptResponse gptResponse) {
        gptResponse.setText(TextUtils.cleanCode(gptResponse.getText()));
        var newCode = gptResponse.getText();
        var newContext = before + newCode + after;
        generatedCodeHandler.handle(newCode, newContext, before.length());
    }


    @Override
    protected List<Pair<String, String>> getPrintableHtmlHistory() {
        return history.stream()
                .map(qa -> Pair.of(
                                qa.getGptRequest().getQuestion().getText(),
                                qa.getGptResponse() != null ? "<i>code was generated successfully</i>" : null
                        )
                )
                .collect(Collectors.toList());
    }


}
