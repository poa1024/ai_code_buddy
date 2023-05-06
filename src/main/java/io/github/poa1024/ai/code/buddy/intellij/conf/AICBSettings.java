package io.github.poa1024.ai.code.buddy.intellij.conf;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import io.github.poa1024.ai.code.buddy.gpt.GptModel;
import lombok.*;

@State(name = "AICBSettings", storages = @Storage("aicb.xml"))
public class AICBSettings implements PersistentStateComponent<AICBSettings.State> {

    @Getter
    private String apikey;
    @Getter
    @Setter
    private GptModel gptModel;

    public AICBSettings() {
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes());
        if (credentials != null) {
            this.apikey = credentials.getPasswordAsString();
        }
        this.gptModel = GptModel.GPT_35_TURBO;
    }

    public void setApikey(String apiKey) {
        this.apikey = apiKey;
        var credentials = new Credentials("aicb-gpt-api-key", apiKey);
        PasswordSafe.getInstance().set(credentialAttributes(), credentials);
    }

    private static CredentialAttributes credentialAttributes() {
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName("AICB", "gpt-api-key"));
    }

    @Override
    public AICBSettings.State getState() {
        return new State(gptModel);
    }

    @Override
    public void loadState(State state) {
        this.gptModel = state.getGptModel();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class State {
        private GptModel gptModel;
    }
}
