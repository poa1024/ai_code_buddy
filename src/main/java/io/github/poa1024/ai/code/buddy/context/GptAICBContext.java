package io.github.poa1024.ai.code.buddy.context;

import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.exception.AICBException;
import io.github.poa1024.ai.code.buddy.gpt.Gpt35TurboClient;
import io.github.poa1024.ai.code.buddy.gpt.Gpt4Client;
import io.github.poa1024.ai.code.buddy.gpt.GptModel;
import io.github.poa1024.ai.code.buddy.session.SessionManager;
import lombok.Getter;

@Getter
public class GptAICBContext extends AbstractAICBContext {

    private final SessionManager sessionManager = new SessionManager("GPT");
    private final Gpt35TurboClient gpt35TurboClient = new Gpt35TurboClient();
    private final Gpt4Client gpt4Client = new Gpt4Client();

    @Override
    public AIClient getAiClient() {
        if (getAICBSettings().getGptModel() == GptModel.GPT_35_TURBO) {
            return gpt35TurboClient;
        } else if (getAICBSettings().getGptModel() == GptModel.GPT_4) {
            return gpt4Client;
        } else {
            throw new AICBException("GPT model is not defined");
        }
    }
}
