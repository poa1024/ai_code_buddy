package io.github.poa1024.ai.code.buddy.intellij.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.conf.Configuration;
import io.github.poa1024.ai.code.buddy.intellij.BackgroundableExecutor;
import io.github.poa1024.ai.code.buddy.session.GenerationCodeSession;
import io.github.poa1024.ai.code.buddy.session.SessionManager;
import io.github.poa1024.ai.code.buddy.util.NotificationUtils;
import io.github.poa1024.ai.code.buddy.util.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class GenerateCodeAction extends AnAction {


    private final SessionManager sessionManager = Configuration.getInstance().getSessionManager();
    private final AIClient aiClient = Configuration.getInstance().getAiClient();

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

            var session = new GenerationCodeSession(
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
                    aiClient,
                    new BackgroundableExecutor(editor.getProject()),
                    context,
                    startOffset
            );
            sessionManager.openNewSession(project, session);
            sessionManager.proceed(userInput);

            caretModel.getCurrentCaret().removeSelection();
        } else {
            NotificationUtils.notifyWarning(project, "Not found place for the code insertion");
        }

    }
}
