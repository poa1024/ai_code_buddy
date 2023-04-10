package io.github.poa1024.ui;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import io.github.poa1024.Configuration;
import io.github.poa1024.sesssion.GptCodeGenerationSessionManager;
import io.github.poa1024.sesssion.model.Question;
import org.jetbrains.annotations.NotNull;

public class GptCodeGenerationSessionWindowFactory implements ToolWindowFactory, DumbAware {

    private final GptCodeGenerationSessionManager gptCodeGenerationSessionManager = Configuration.GPT_CODE_GENERATION_SESSION_MANAGER;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        var gptSessionWindow = new GptSessionWindow(text -> {
            gptCodeGenerationSessionManager.askQuestion(new Question(text));
        });
        gptCodeGenerationSessionManager.setSessionWindow(gptSessionWindow);
        var contentFactory = ContentFactory.SERVICE.getInstance();
        var content = contentFactory.createContent(gptSessionWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
