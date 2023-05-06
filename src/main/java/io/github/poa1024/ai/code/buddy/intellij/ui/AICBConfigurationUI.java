package io.github.poa1024.ai.code.buddy.intellij.ui;

import com.intellij.openapi.options.ConfigurableUi;
import io.github.poa1024.ai.code.buddy.gpt.GptModel;
import io.github.poa1024.ai.code.buddy.intellij.conf.AICBSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AICBConfigurationUI implements ConfigurableUi<AICBSettings> {

    private JPanel mainPanel;
    private JLabel openAiApiKeyLabel;
    private JPasswordField openAiApiKeyValue;
    private JLabel gptModelLabel;
    private JComboBox gptModelValue;


    @Override
    public void reset(@NotNull AICBSettings settings) {
        openAiApiKeyValue.setText(settings.getApikey());
        gptModelValue.getModel().setSelectedItem(settings.getGptModel().getName());
    }

    @Override
    public boolean isModified(@NotNull AICBSettings settings) {
        var apikey = new String(openAiApiKeyValue.getPassword());
        boolean keyIsModified = !apikey.equals(settings.getApikey()) && !(settings.getApikey() == null && apikey.isEmpty());
        boolean modelIsModified = !getSelectedGptModel().equals(settings.getGptModel());
        return keyIsModified || modelIsModified;
    }

    @Override
    public void apply(@NotNull AICBSettings settings) {
        var apikey = new String(openAiApiKeyValue.getPassword());
        settings.setApikey(apikey);
        settings.setGptModel(getSelectedGptModel());
    }

    private GptModel getSelectedGptModel() {
        return GptModel.create(gptModelValue.getModel().getSelectedItem().toString());
    }

    @Override
    public @NotNull JComponent getComponent() {
        return mainPanel;
    }
}
