package io.github.poa1024.ui;

import io.github.poa1024.conversation.model.QARound;
import lombok.Getter;
import org.intellij.lang.annotations.Language;

import javax.swing.*;
import java.util.List;

@Getter
public class GptConversationWindow {

    @Language("html")
    private static final String HISTORY_HTML = "<html><body>%s</body></html>";

    private JPanel content;
    private JTextPane history;
    private JTextField terminal;

    public void printConversation(List<QARound> conversation) {
        var text = String.format(HISTORY_HTML, toText(conversation));
        history.setText(text);
    }

    private static CharSequence toText(List<QARound> history) {
        var res = new StringBuilder();

        history.forEach(qaRound -> {
            res.append("<b>Q: </b>");
            res.append(qaRound.getQuestion().getText());
            res.append("<br>");
            if (qaRound.getAnswer() != null) {
                res.append("<b>A: </b>");
                res.append("<i>");
                res.append(qaRound.getAnswer().getDesc());
                res.append("</i>");
                res.append("<br>");
            }
            res.append("<br>");
        });

        return res;
    }

}
