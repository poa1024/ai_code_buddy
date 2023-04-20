package io.github.poa1024.ai.code.buddy.intellij.ui;

import freemarker.template.Template;
import io.github.poa1024.ai.code.buddy.conf.Configuration;
import io.github.poa1024.ai.code.buddy.model.html.HtmlBlock;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.swing.*;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class SessionWindow {

    //language=HTML
    private final Template historyTemplate;

    private JPanel content;
    private JTextPane history;
    private JTextField terminal;
    private JScrollPane scrollPanel;

    @SneakyThrows
    public SessionWindow(Consumer<String> onEnter) {
        this.historyTemplate = Configuration.getInstance()
                .getFreemarkerConf()
                .getTemplate("ui/history.html");
        terminal.addActionListener(e -> {
            var text = terminal.getText();
            terminal.setText(null);
            onEnter.accept(text);
        });
    }

    @SneakyThrows
    public void printConversation(List<HtmlBlock> conversation) {
        var conversationAsString = conversation.stream()
                .map(HtmlBlock::getValue)
                .collect(Collectors.joining());

        var templateModel = new HashMap<String, Object>();
        templateModel.put("body", conversationAsString);

        var stringWriter = new StringWriter();
        historyTemplate.process(templateModel, stringWriter);

        history.setText(stringWriter.toString());
        SwingUtilities.invokeLater(() -> {
            var vertical = scrollPanel.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

}
