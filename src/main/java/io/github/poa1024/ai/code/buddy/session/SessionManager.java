package io.github.poa1024.ai.code.buddy.session;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.intellij.ui.SessionWindow;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionManager {

    private final String toolWindowName;

    private SessionWindow conversationWindow;
    private Session session;

    public void setSessionWindow(SessionWindow conversationWindow) {
        this.conversationWindow = conversationWindow;
    }

    public void openNewSession(Project project, Session session) {
        ToolWindowManager.getInstance(project).getToolWindow(toolWindowName).show();
        this.session = session;
    }

    public void proceed() {
        proceed(null);
    }

    public void proceed(String userInput) {
        session.proceed(userInput, this::updateView);
    }

    private void updateView(boolean intermediateState) {
        if (conversationWindow != null && session != null) {
            if (session instanceof GenerateCodeSession) {
                var conversation = AICBContextHolder.getContext()
                        .getGenerateCodeSessionHtmlMapper()
                        .mapHistory((GenerateCodeSession) session);
                var prompt = intermediateState ? "" : "ask to change the code";
                conversationWindow.printConversation(prompt, conversation);
            } else if (session instanceof ExplainCodeSession) {
                var conversation = AICBContextHolder.getContext()
                        .getExplainCodeSessionHtmlMapper()
                        .mapHistory((ExplainCodeSession) session);
                var prompt = intermediateState ? "" : "ask а new question";
                conversationWindow.printConversation(prompt, conversation);
            } else {
                throw new IllegalStateException();
            }

        }
    }


}
