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
        this.session = session;
        updateView(false);
        ToolWindowManager.getInstance(project).getToolWindow(toolWindowName).show();
        if (session instanceof Focusable) {
            conversationWindow.grabFocus();
        }
    }

    public void proceed() {
        proceed(null);
    }

    public void proceed(String userInput) {
        session.proceed(userInput, this::updateView);
    }

    private void updateView(boolean intermediateState) {
        if (conversationWindow != null && session != null) {
            if (session instanceof GenerateCodeSession generateCodeSession) {
                var conversation = AICBContextHolder.getContext()
                        .getGenerateCodeSessionHtmlMapper()
                        .mapHistory(generateCodeSession);
                var prompt = intermediateState ? "" : "ask to change the code";
                conversationWindow.printConversation(prompt, conversation);
            } else if (session instanceof RefactorCodeSession refactorCodeSession) {
                var conversation = AICBContextHolder.getContext()
                        .getRefactorCodeSessionHtmlMapper()
                        .mapHistory(refactorCodeSession);
                var prompt = intermediateState ? "" : "ask to change the code";
                conversationWindow.printConversation(prompt, conversation);
            } else if (session instanceof ExplainCodeSession explainCodeSession) {
                var conversation = AICBContextHolder.getContext()
                        .getExplainCodeSessionHtmlMapper()
                        .mapHistory(explainCodeSession);
                var prompt = intermediateState ? "" : "ask something...";
                conversationWindow.printConversation(prompt, conversation);
            } else if (session instanceof ConversationSession conversationSession) {
                var conversation = AICBContextHolder.getContext()
                        .getConversationSessionHtmlMapper()
                        .mapHistory(conversationSession);
                var prompt = intermediateState ? "" : "ask something...";
                conversationWindow.printConversation(prompt, conversation);
            } else {
                throw new IllegalStateException();
            }

            if (!intermediateState && session instanceof Focusable) {
                conversationWindow.grabFocus();
            }

            conversationWindow.setEnabled(!intermediateState);

        }
    }

}
