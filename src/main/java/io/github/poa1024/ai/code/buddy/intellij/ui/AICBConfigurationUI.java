package io.github.poa1024.ai.code.buddy.intellij.ui;

import com.intellij.openapi.options.ConfigurableUi;
import io.github.poa1024.ai.code.buddy.intellij.conf.AICBSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AICBConfigurationUI implements ConfigurableUi<AICBSettings> {

    private JPanel mainPanel;
    private JPasswordField openAiApiKeyValue;
    private JLabel openAiApiKeyLabel;


    @Override
    public void reset(@NotNull AICBSettings settings) {
        openAiApiKeyValue.setText(settings.getApikey());
    }

    @Override
    public boolean isModified(@NotNull AICBSettings settings) {
        var apikey = new String(openAiApiKeyValue.getPassword());
        return !apikey.equals(settings.getApikey()) && !(settings.getApikey() == null && apikey.isEmpty());
    }

    @Override
    public void apply(@NotNull AICBSettings settings) {
        var apikey = new String(openAiApiKeyValue.getPassword());
        settings.setApikey(apikey);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return mainPanel;
    }
}
