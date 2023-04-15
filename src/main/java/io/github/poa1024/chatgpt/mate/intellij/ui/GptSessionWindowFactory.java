package io.github.poa1024.chatgpt.mate.intellij.ui;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import io.github.poa1024.chatgpt.mate.Configuration;
import io.github.poa1024.chatgpt.mate.session.GptSessionManager;
import org.jetbrains.annotations.NotNull;

public class GptSessionWindowFactory implements ToolWindowFactory, DumbAware {

    private final GptSessionManager gptSessionManager = Configuration.GPT_SESSION_MANAGER;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        var gptSessionWindow = new GptSessionWindow(gptSessionManager::proceed);
        gptSessionManager.setSessionWindow(gptSessionWindow);
        var contentFactory = ContentFactory.SERVICE.getInstance();
        var content = contentFactory.createContent(gptSessionWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
