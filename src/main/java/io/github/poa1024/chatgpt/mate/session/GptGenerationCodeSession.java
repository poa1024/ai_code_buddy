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
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GptGenerationCodeSession extends GptSession {

    private final GptRequestBuilder gptQuestionBuilder = Configuration.GPT_REQUEST_BUILDER;

    private final Consumer<String> generatedCodeHandler;

    public GptGenerationCodeSession(
            Consumer<String> generatedCodeHandler,
            GptClient gptClient,
            Executor executor,
            String context,
            int offset
    ) {
        super(gptClient, executor, createContext(context, offset));
        this.generatedCodeHandler = generatedCodeHandler;
    }

    @Override
    protected GptRequest createRequest(String userInput) {
        String gptRequest;
        if (history.isEmpty()) {
            gptRequest = gptQuestionBuilder.askToGenerateCode(userInput);
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
        var code = gptResponse.getText();
        generatedCodeHandler.accept(code);
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

    private static String createContext(String context, int offset) {
        return context.substring(0, offset) + "/*your code will be here*/" + context.substring(offset);
    }


}
