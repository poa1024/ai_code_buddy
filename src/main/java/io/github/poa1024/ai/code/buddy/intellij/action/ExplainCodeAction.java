package io.github.poa1024.ai.code.buddy.intellij.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.conf.Configuration;
import io.github.poa1024.ai.code.buddy.intellij.BackgroundableExecutor;
import io.github.poa1024.ai.code.buddy.session.ExplainCodeSession;
import io.github.poa1024.ai.code.buddy.session.SessionManager;
import io.github.poa1024.ai.code.buddy.util.NotificationUtils;
import io.github.poa1024.ai.code.buddy.util.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class ExplainCodeAction extends AnAction {

    private final SessionManager sessionManager = Configuration.getInstance().getSessionManager();
    private final AIClient aiClient = Configuration.getInstance().getAiClient();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var editor = e.getRequiredData(CommonDataKeys.EDITOR);
        var psiFile = e.getData(LangDataKeys.PSI_FILE);
        var project = editor.getProject();

        var caretModel = editor.getCaretModel();
        var selectedText = PsiUtils.getSelectedTextOrTheExpectedPsiElement(psiFile, caretModel);

        if (selectedText == null) {
            NotificationUtils.notifyWarning(project, "Not found code to explain");
            return;
        }

        var code = selectedText.getText();
        var context = psiFile.getText();
        var executor = new BackgroundableExecutor(editor.getProject());

        sessionManager.openNewSession(project, new ExplainCodeSession(aiClient, executor, context, code));
        sessionManager.proceed();

        caretModel.getCurrentCaret().removeSelection();

    }

}