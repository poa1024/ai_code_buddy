package io.github.poa1024.action.explain;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import io.github.poa1024.Configuration;
import io.github.poa1024.session.GptExplainTheGistSession;
import io.github.poa1024.session.GptSessionManager;
import org.jetbrains.annotations.NotNull;

public class ExplainTheGistAction extends AnAction {

    private final GptSessionManager gptSessionManager = Configuration.GPT_SESSION_MANAGER;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var editor = e.getRequiredData(CommonDataKeys.EDITOR);
        var psiFile = e.getData(LangDataKeys.PSI_FILE);

        var caretModel = editor.getCaretModel();
        var selectedText = caretModel.getCurrentCaret().getSelectedText();

        gptSessionManager.openNewSession(editor.getProject(), new GptExplainTheGistSession(psiFile, selectedText));
        gptSessionManager.proceed();
        caretModel.getCurrentCaret().removeSelection();

    }

}
