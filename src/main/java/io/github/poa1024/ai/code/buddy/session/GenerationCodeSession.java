package io.github.poa1024.ai.code.buddy.session;

import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.AIRequestBuilder;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.conf.Configuration;
import io.github.poa1024.ai.code.buddy.model.HumanReadableText;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import io.github.poa1024.ai.code.buddy.util.TextUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GenerationCodeSession extends Session {

    private final AIRequestBuilder aiRequestBuilder = Configuration.getInstance().getAiRequestBuilder();

    private final Consumer<String> generatedCodeHandler;

    public GenerationCodeSession(
            Consumer<String> generatedCodeHandler,
            AIClient aiClient,
            Executor executor,
            String context,
            int offset
    ) {
        super(aiClient, executor, createContext(context, offset));
        this.generatedCodeHandler = generatedCodeHandler;
    }

    @Override
    protected AIRequest createRequest(String userInput) {
        String request;
        if (history.isEmpty()) {
            request = aiRequestBuilder.askToGenerateCode(userInput);
        } else {
            request = aiRequestBuilder.askToChangeGeneratedCode(userInput);
            request = aiRequestBuilder.appendPreviouslyGenerateCode(
                    request, history.stream()
                            .map(AIInteraction::requireResponse)
                            .map(AIResponse::getText)
                            .collect(Collectors.toList())

            );
        }
        request = aiRequestBuilder.appendContext(request, initialContext);
        return AIRequest.builder()
                .question(new HumanReadableText(userInput))
                .body(request)
                .build();
    }

    @Override
    protected void handleResponse(AIResponse response) {
        response.setText(TextUtils.cleanCode(response.getText()));
        var code = response.getText();
        generatedCodeHandler.accept(code);
    }


    @Override
    protected List<Pair<String, String>> getPrintableHtmlHistory() {
        return history.stream()
                .map(qa -> Pair.of(
                                qa.getRequest().getQuestion().text(),
                                qa.getResponse() != null ? "<i>code was generated successfully</i>" : null
                        )
                )
                .collect(Collectors.toList());
    }

    private static String createContext(String context, int offset) {
        return context.substring(0, offset) + "/*your code will be here*/" + context.substring(offset);
    }


}
