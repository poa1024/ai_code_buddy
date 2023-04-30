package io.github.poa1024.ai.code.buddy.intellij.conf;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import lombok.Getter;

public class AICBSettings {

    @Getter
    private String apikey;

    public AICBSettings() {
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes());
        if (credentials != null) {
            this.apikey = credentials.getPasswordAsString();
        }
    }

    public void setApikey(String apiKey) {
        this.apikey = apiKey;
        var credentials = new Credentials("aicb-gpt-api-key", apiKey);
        PasswordSafe.getInstance().set(credentialAttributes(), credentials);
    }

    private static CredentialAttributes credentialAttributes() {
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName("AICB", "gpt-api-key"));
    }

}
