package io.github.poa1024.session;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import io.github.poa1024.Configuration;
import io.github.poa1024.gpt.GptRequestBuilder;
import io.github.poa1024.session.model.GptRequest;
import io.github.poa1024.session.model.GptResponse;
import io.github.poa1024.util.HumanReadableText;

import java.util.List;
import java.util.stream.Collectors;

public class GptExplainTheGistSession extends GptSession {

    private final GptRequestBuilder gptQuestionBuilder = Configuration.GPT_REQUEST_BUILDER;
    private final String code;

    public GptExplainTheGistSession(PsiFile psiFile, String code) {
        super(psiFile, psiFile.getText().replace(code, ""));
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
                .map(qa -> Pair.create(
                        qa.getGptRequest().getQuestion().getText(),
                        qa.getGptResponse() != null ? qa.getGptResponse().getText() : null)
                )
                .collect(Collectors.toList());
    }

}
