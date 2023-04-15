package io.github.poa1024.chatgpt.mate.session;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import io.github.poa1024.chatgpt.mate.intellij.ui.GptSessionWindow;

public class GptSessionManager {


    private GptSessionWindow conversationWindow;
    private GptSession gptSession;

    public void setSessionWindow(GptSessionWindow conversationWindow) {
        this.conversationWindow = conversationWindow;
        updateView();
    }

    public void openNewSession(Project project, GptSession gptSession) {
        ToolWindowManager.getInstance(project).getToolWindow("GPT").show();
        this.gptSession = gptSession;
    }

    public void proceed() {
        proceed(null);
    }

    public void proceed(String userInput) {
        gptSession.proceed(userInput, this::updateView);
    }

    private void updateView() {
        if (conversationWindow != null && gptSession != null) {
            conversationWindow.printConversation(gptSession.getPrintableHtmlHistory());
        }
    }


}
