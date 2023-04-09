package io.github.poa1024.conversation;

import io.github.poa1024.conversation.model.Question;
import io.github.poa1024.ui.GptConversationWindow;
import lombok.Getter;
import lombok.Setter;

public class GptConversationManager {

    @Getter
    private GptConversation gptConversation;
    @Setter
    private GptConversationWindow conversationWindow;

    public void startConversation(Question question) {
        this.gptConversation = GptConversation.start(question);
    }

    public void updateView() {
        conversationWindow.printConversation(gptConversation.getHistory());
    }
}
