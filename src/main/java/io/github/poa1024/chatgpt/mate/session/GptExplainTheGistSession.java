package io.github.poa1024.chatgpt.mate.session;

import io.github.poa1024.chatgpt.mate.Configuration;
import io.github.poa1024.chatgpt.mate.Executor;
import io.github.poa1024.chatgpt.mate.GptRequestBuilder;
import io.github.poa1024.chatgpt.mate.model.HumanReadableText;
import io.github.poa1024.chatgpt.mate.session.model.GptRequest;
import io.github.poa1024.chatgpt.mate.session.model.GptResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class GptExplainTheGistSession extends GptSession {

    private final GptRequestBuilder gptQuestionBuilder = Configuration.GPT_REQUEST_BUILDER;
    private final String code;

    public GptExplainTheGistSession(Executor executor, String context, String code) {
        super(executor, context);
        this.code = code;
    }

    @Override
    protected GptRequest createRequest(String userInput) {
        if (history.isEmpty()) {
            var gptRequest = gptQuestionBuilder.askForShortExplanation();
            gptRequest = gptQuestionBuilder.appendCode(gptRequest, code);
            gptRequest = gptQuestionBuilder.appendContext(gptRequest, initialContext);
            return GptRequest.builder()
                    .question(new HumanReadableText("Explain the gist"))
                    .body(gptRequest)
                    .build();
        } else {
            var gptRequest = gptQuestionBuilder.continueDialog(userInput);
            gptRequest = gptQuestionBuilder.appendCode(gptRequest, code);
            gptRequest = gptQuestionBuilder.appendHistory(gptRequest, history);
            gptRequest = gptQuestionBuilder.appendContext(gptRequest, initialContext);
            return GptRequest.builder()
                    .question(new HumanReadableText(userInput))
                    .body(gptRequest)
                    .build();
        }
    }

    @Override
    protected void handleResponse(GptResponse gptResponse) {
        //nothing
    }

    @Override
    protected List<Pair<String, String>> getPrintableHtmlHistory() {
        return history.stream()
                .map(qa -> Pair.of(
                        qa.getGptRequest().getQuestion().getText(),
                        qa.getGptResponse() != null ? qa.getGptResponse().getText() : null)
                )
                .collect(Collectors.toList());
    }

}
