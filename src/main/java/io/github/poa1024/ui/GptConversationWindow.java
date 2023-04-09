package io.github.poa1024.ui;

import com.intellij.openapi.project.DumbAware;

import javax.swing.*;

public class GptConversationWindow implements DumbAware {

    private JPanel content;
    private JTextArea history;
    private JTextField terminal;

    public JPanel getContent() {
        return content;
    }
}
