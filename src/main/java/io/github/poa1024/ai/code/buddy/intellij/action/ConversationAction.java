package io.github.poa1024.ai.code.buddy.intellij.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.intellij.BackgroundableExecutor;
import io.github.poa1024.ai.code.buddy.session.ConversationSession;
import io.github.poa1024.ai.code.buddy.session.SessionManager;
import org.jetbrains.annotations.NotNull;

public class ConversationAction extends AnAction {

    private final SessionManager sessionManager = AICBContextHolder.getContext().getSessionManager();
    private final AIClient aiClient = AICBContextHolder.getContext().getAiClient();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getRequiredData(CommonDataKeys.PROJECT);
        var executor = new BackgroundableExecutor(project);
        sessionManager.openNewSession(project, new ConversationSession(aiClient, executor));
    }

}
