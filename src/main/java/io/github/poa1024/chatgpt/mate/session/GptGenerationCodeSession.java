package io.github.poa1024.chatgpt.mate.session;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import io.github.poa1024.chatgpt.mate.Configuration;
import io.github.poa1024.chatgpt.mate.GptRequestBuilder;
import io.github.poa1024.chatgpt.mate.model.HumanReadableText;
import io.github.poa1024.chatgpt.mate.session.model.GptInteraction;
import io.github.poa1024.chatgpt.mate.session.model.GptRequest;
import io.github.poa1024.chatgpt.mate.session.model.GptResponse;
import io.github.poa1024.chatgpt.mate.util.TextUtils;

import java.util.List;
import java.util.stream.Collectors;

public class GptGenerationCodeSession extends GptSession {

    private final GptRequestBuilder gptQuestionBuilder = Configuration.GPT_REQUEST_BUILDER;

    private final String before;
    private final String after;

    public GptGenerationCodeSession(PsiFile psiFile, int start, int end) {
        super(psiFile, psiFile.getText());
        this.before = psiFile.getText().substring(0, start);
        this.after = psiFile.getText().substring(end);
    }

    @Override
    protected GptRequest createRequest(String userInput) {
        if (history.isEmpty()) {
            var gptRequest = gptQuestionBuilder.askToGeneratedCode(userInput);
            gptRequest = gptQuestionBuilder.appendContext(gptRequest, initialContext);
            return GptRequest.builder()
                    .question(new HumanReadableText(userInput))
                    .body(gptRequest)
                    .build();

        } else {
            var gptRequest = gptQuestionBuilder.askToChangeGeneratedCode(userInput);
            gptRequest = gptQuestionBuilder.appendPreviouslyGenerateCode(
                    gptRequest, history.stream()
                            .map(GptInteraction::requireGptResponse)
                            .map(GptResponse::getText)
                            .collect(Collectors.toList())

            );
            gptRequest = gptQuestionBuilder.appendContext(gptRequest, initialContext);
            return GptRequest.builder()
                    .question(new HumanReadableText(userInput))
                    .body(gptRequest)
                    .build();
        }
    }

    @Override
    protected void handleResponse(GptResponse gptResponse) {

        gptResponse.setText(TextUtils.cleanCode(gptResponse.getText()));

        var project = psiFile.getProject();
        var documentManager = PsiDocumentManager.getInstance(project);
        var document = documentManager.getDocument(psiFile);
        var styleManager = CodeStyleManager.getInstance(project);
        WriteCommandAction.runWriteCommandAction(
                project,
                () -> {
                    var generatedCode = gptResponse.getText();
                    document.setText(before + generatedCode + after);
                    documentManager.commitDocument(document);

                    styleManager.reformatText(psiFile, before.length(), before.length() + generatedCode.length() + 1);
                }
        );
    }


    @Override
    protected List<Pair<String, String>> getPrintableHtmlHistory() {
        return history.stream()
                .map(qa -> Pair.create(
                                qa.getGptRequest().getQuestion().getText(),
                                qa.getGptResponse() != null ? "<i>code was generated successfully</i>" : null
                        )
                )
                .collect(Collectors.toList());
    }


}
