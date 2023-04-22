package io.github.poa1024.ai.code.buddy.session;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import io.github.poa1024.ai.code.buddy.conf.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import io.github.poa1024.ai.code.buddy.intellij.ui.SessionWindow;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SessionManager {

    private final String toolWindowName;

    private SessionWindow conversationWindow;
    private Session session;

    public void setSessionWindow(SessionWindow conversationWindow) {
        this.conversationWindow = conversationWindow;
        updateView();
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

    private void updateView() {
        if (conversationWindow != null && session != null) {
            List<HtmlBlock> conversation;
            if (session instanceof GenerateCodeSession) {
                conversation = AICBContextHolder.getContext().getGenerateCodeSessionHtmlMapper().mapHistory((GenerateCodeSession) session);
            } else if (session instanceof ExplainCodeSession) {
                conversation = AICBContextHolder.getContext().getExplainCodeSessionHtmlMapper().mapHistory((ExplainCodeSession) session);
            } else {
                throw new IllegalStateException();
            }
            conversationWindow.printConversation(conversation);
        }
    }


}
