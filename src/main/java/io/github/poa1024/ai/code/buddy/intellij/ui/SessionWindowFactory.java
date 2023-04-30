package io.github.poa1024.ai.code.buddy.intellij.ui;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.session.SessionManager;
import org.jetbrains.annotations.NotNull;

public class SessionWindowFactory implements ToolWindowFactory, DumbAware {

    private final SessionManager sessionManager = AICBContextHolder.getContext().getSessionManager();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        var sessionWindow = new SessionWindow(sessionManager::proceed);
        sessionManager.setSessionWindow(sessionWindow);
        var contentFactory = ContentFactory.getInstance();
        var content = contentFactory.createContent(sessionWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
