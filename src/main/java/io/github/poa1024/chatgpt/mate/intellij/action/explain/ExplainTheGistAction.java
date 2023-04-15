package io.github.poa1024.chatgpt.mate.intellij.action.explain;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import io.github.poa1024.chatgpt.mate.Configuration;
import io.github.poa1024.chatgpt.mate.gptclient.GptClient;
import io.github.poa1024.chatgpt.mate.intellij.BackgroundableExecutor;
import io.github.poa1024.chatgpt.mate.session.GptExplainTheGistSession;
import io.github.poa1024.chatgpt.mate.session.GptSessionManager;
import io.github.poa1024.chatgpt.mate.util.NotificationUtils;
import io.github.poa1024.chatgpt.mate.util.PsiUtils;
import io.github.poa1024.chatgpt.mate.util.TextUtils;
import org.jetbrains.annotations.NotNull;

public class ExplainTheGistAction extends AnAction {

    private final GptSessionManager gptSessionManager = Configuration.GPT_SESSION_MANAGER;
    private final GptClient gptClient = Configuration.GPT_CLIENT;

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
        var context = TextUtils.removeCodeFromTheContext(psiFile.getText(), code);
        var executor = new BackgroundableExecutor(editor.getProject());

        gptSessionManager.openNewSession(project, new GptExplainTheGistSession(gptClient, executor, context, code));
        gptSessionManager.proceed();

        caretModel.getCurrentCaret().removeSelection();

    }

}
