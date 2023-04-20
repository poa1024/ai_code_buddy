package io.github.poa1024.ai.code.buddy.session;

import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.AIRequestBuilder;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.conf.Configuration;
import io.github.poa1024.ai.code.buddy.model.HumanReadableText;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import lombok.Getter;

import java.util.regex.Pattern;

public class ExplainCodeSession extends Session {

    private final AIRequestBuilder aiRequestBuilder = Configuration.getInstance().getAiRequestBuilder();
    @Getter
    private final String code;

    public ExplainCodeSession(AIClient aiClient, Executor executor, String context, String code) {
        super(aiClient, executor, removeCodeFromTheContext(context, code));
        this.code = code;
    }

    @Override
    protected AIRequest createRequest(String userInput) {
        String request;
        if (getHistory().isEmpty()) {
            userInput = "Explain the code";
            request = aiRequestBuilder.askForShortExplanation();
            request = aiRequestBuilder.appendCode(request, code);

        } else {
            request = aiRequestBuilder.continueDialog(userInput);
            request = aiRequestBuilder.appendCode(request, code);
            request = aiRequestBuilder.appendHistory(request, getHistory());
        }
        request = aiRequestBuilder.appendContext(request, initialContext);
        return AIRequest.builder()
                .question(new HumanReadableText(userInput))
                .body(request)
                .build();
    }

    public static String removeCodeFromTheContext(String context, String code) {
        return context.replaceAll(Pattern.quote(code), "/*discussed code is here*/");
    }

}
