package io.github.poa1024.action.explain;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import io.github.poa1024.Configuration;
import io.github.poa1024.gpt.GptClient;
import io.github.poa1024.gpt.GptRequestBuilder;
import org.jetbrains.annotations.NotNull;

public class ExplainInDetailsAction extends AnAction {

    private final GptClient gptClient = Configuration.GPT_CLIENT;
    private final GptRequestBuilder gptRequestBuilder = Configuration.GPT_REQUEST_BUILDER;

    private final ProgressManager progressManager = ProgressManager.getInstance();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var editor = e.getRequiredData(CommonDataKeys.EDITOR);
        var project = e.getRequiredData(CommonDataKeys.PROJECT);
        var styleManager = CodeStyleManager.getInstance(project);
        var documentManager = PsiDocumentManager.getInstance(project);
        var document = editor.getDocument();
        var psiFile = e.getData(LangDataKeys.PSI_FILE);

        var caretModel = editor.getCaretModel();
        var selectedText = caretModel.getCurrentCaret().getSelectedText();

        progressManager.run(new Task.Backgroundable(project, "GPT request...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                var req = gptRequestBuilder.askForDetailedExplanation();
                req = gptRequestBuilder.appendCode(req, selectedText);
                var res = gptClient
                        .ask(req)
                        .getFirstChoice();

                int selectionStart = caretModel.getCurrentCaret().getSelectionStart();
                int selectionEnd = caretModel.getCurrentCaret().getSelectionEnd();

                WriteCommandAction.runWriteCommandAction(
                        project,
                        () -> {
                            document.replaceString(selectionStart, selectionEnd, res);
                            documentManager.commitDocument(document);
                            styleManager.reformatText(psiFile, selectionStart, selectionStart + res.length());
                        }
                );
            }
        });
        caretModel.getCurrentCaret().removeSelection();
    }

}
