package io.github.poa1024.action.explain;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import io.github.poa1024.Configuration;
import io.github.poa1024.session.GptExplainTheGistSession;
import io.github.poa1024.session.GptSessionManager;
import io.github.poa1024.util.NotificationUtils;
import io.github.poa1024.util.PsiUtils;
import org.jetbrains.annotations.NotNull;

public class ExplainTheGistAction extends AnAction {

    private final GptSessionManager gptSessionManager = Configuration.GPT_SESSION_MANAGER;

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

        gptSessionManager.openNewSession(project, new GptExplainTheGistSession(psiFile, selectedText.getText()));
        gptSessionManager.proceed();
        caretModel.getCurrentCaret().removeSelection();

    }

}
