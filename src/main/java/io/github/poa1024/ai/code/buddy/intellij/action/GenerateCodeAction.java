package io.github.poa1024.ai.code.buddy.intellij.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiComment;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.intellij.BackgroundableExecutor;
import io.github.poa1024.ai.code.buddy.intellij.CodeToDocumentInserter;
import io.github.poa1024.ai.code.buddy.session.GenerateCodeSession;
import io.github.poa1024.ai.code.buddy.session.SessionManager;
import io.github.poa1024.ai.code.buddy.util.NotificationUtils;
import io.github.poa1024.ai.code.buddy.util.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class GenerateCodeAction extends AnAction {


    private final SessionManager sessionManager = AICBContextHolder.getContext().getSessionManager();
    private final AIClient aiClient = AICBContextHolder.getContext().getAiClient();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var editor = e.getRequiredData(CommonDataKeys.EDITOR);
        var psiFile = e.getData(LangDataKeys.PSI_FILE);
        var project = psiFile.getProject();
        var caretModel = editor.getCaretModel();

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

            var session = new GenerateCodeSession(
                    new CodeToDocumentInserter(psiFile, before, after),
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
