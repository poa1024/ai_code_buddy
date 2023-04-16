package io.github.poa1024.chatgpt.mate.intellij.action.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import io.github.poa1024.chatgpt.mate.Configuration;
import io.github.poa1024.chatgpt.mate.gptclient.GptClient;
import io.github.poa1024.chatgpt.mate.intellij.BackgroundableExecutor;
import io.github.poa1024.chatgpt.mate.session.GptGenerationCodeSession;
import io.github.poa1024.chatgpt.mate.session.GptSessionManager;
import io.github.poa1024.chatgpt.mate.util.NotificationUtils;
import io.github.poa1024.chatgpt.mate.util.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class GenerateCodeAction extends AnAction {


    private final GptSessionManager gptSessionManager = Configuration.GPT_SESSION_MANAGER;
    private final GptClient gptClient = Configuration.GPT_CLIENT;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var editor = e.getRequiredData(CommonDataKeys.EDITOR);
        var psiFile = e.getData(LangDataKeys.PSI_FILE);
        var project = psiFile.getProject();
        var caretModel = editor.getCaretModel();
        var documentManager = PsiDocumentManager.getInstance(project);

        var selectedText = PsiUtils.getSelectedTextOrTheExpectedPsiElement(
                psiFile,
                editor.getCaretModel(),
                PsiComment.class
        );

        if (selectedText != null) {

            var userInput = selectedText.getText();
            int startOffset = selectedText.getStartOffset();
            int endOffset = selectedText.getEndOffset();

            var before = psiFile.getText().substring(0, startOffset);
            var after = psiFile.getText().substring(endOffset);
            var context = before + after;

            var session = new GptGenerationCodeSession(
                    (code) -> {
                        var document = documentManager.getDocument(psiFile);
                        var styleManager = CodeStyleManager.getInstance(project);
                        WriteCommandAction.runWriteCommandAction(
                                project,
                                () -> {
                                    document.setText(before + code + after);
                                    documentManager.commitDocument(document);
                                    styleManager.reformatText(psiFile, startOffset, startOffset + code.length() + 1);
                                }
                        );
                    },
                    gptClient,
                    new BackgroundableExecutor(editor.getProject()),
                    context,
                    startOffset
            );
            gptSessionManager.openNewSession(project, session);
            gptSessionManager.proceed(userInput);

            caretModel.getCurrentCaret().removeSelection();
        } else {
            NotificationUtils.notifyWarning(project, "Not found place for the code insertion");
        }

    }
}
