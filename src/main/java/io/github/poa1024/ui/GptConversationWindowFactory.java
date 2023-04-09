package io.github.poa1024.ui;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import io.github.poa1024.Configuration;
import io.github.poa1024.conversation.GptConversationManager;
import org.jetbrains.annotations.NotNull;

public class GptConversationWindowFactory implements ToolWindowFactory, DumbAware {

    private final GptConversationManager conversationManager = Configuration.CONVERSATION_MANAGER;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        var gptConversationWindow = new GptConversationWindow();
        conversationManager.setConversationWindow(gptConversationWindow);
        var contentFactory = ContentFactory.SERVICE.getInstance();
        var content = contentFactory.createContent(gptConversationWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
