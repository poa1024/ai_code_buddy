package io.github.poa1024.ai.code.buddy.intellij.conf;

import com.intellij.openapi.options.ConfigurableBase;
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.intellij.ui.AICBConfigurationUI;
import org.jetbrains.annotations.NotNull;

public class AICBConfiguration extends ConfigurableBase<AICBConfigurationUI, AICBSettings> {


    protected AICBConfiguration() {
        super("poa1024.aicb", "AI Code Buddy", null);
    }

    @Override
    protected @NotNull AICBSettings getSettings() {
        return AICBContextHolder.getContext().getAICBSettings();
    }

    @Override
    protected AICBConfigurationUI createUi() {
        return new AICBConfigurationUI();
    }


}
