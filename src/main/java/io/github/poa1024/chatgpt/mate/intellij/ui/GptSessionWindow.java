package io.github.poa1024.chatgpt.mate.intellij.ui;

import io.github.poa1024.chatgpt.mate.util.TextUtils;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class GptSessionWindow {

    //language=HTML
    private static final String BASE_HTML = """
            <html lang="eng">
            <head>
                <title>GPT Session</title>
                <style>
                    body {
                        font-family: Arial, sans-serif
                        font-size: medium;
                    }
                        
                    blockquote {
                        padding: 0.5em 15px;
                        font-family: Monospaced, sans-serif;
                        border: 1px dashed black;
                    }
                </style>
            </head>
            <body>%s</body>
            </html>
                       \s""".stripIndent();

    private JPanel content;
    private JTextPane history;
    private JTextField terminal;
    private JScrollPane scrollPanel;

    public GptSessionWindow(Consumer<String> onEnter) {
        terminal.addActionListener(e -> {
            var text = terminal.getText();
            terminal.setText(null);
            onEnter.accept(text);
        });
    }

    public void printConversation(List<Pair<String, String>> conversation) {
        var conversationAsString = conversation.stream()
                .map(qa -> {
                            var list = new ArrayList<String>();
                            list.add("<b>Q:</b>&ensp;" + TextUtils.toHtml(qa.getLeft()));
                            list.add("<br>");
                            if (qa.getRight() != null) {
                                list.add("<b>A:</b>&ensp;" + TextUtils.toHtml(qa.getRight()));
                                list.add("<br>");
                            }
                            list.add("<br>");
                            return list;
                        }
                )
                .flatMap(Collection::stream)
                .collect(Collectors.joining());
        var text = String.format(BASE_HTML, conversationAsString);
        history.setText(text);
        SwingUtilities.invokeLater(() -> {
            var vertical = scrollPanel.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

}
