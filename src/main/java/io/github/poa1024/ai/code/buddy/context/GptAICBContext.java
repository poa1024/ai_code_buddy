package io.github.poa1024.ai.code.buddy.context;

import io.github.poa1024.ai.code.buddy.gpt.Gpt35TurboClient;
import io.github.poa1024.ai.code.buddy.session.SessionManager;
import lombok.Getter;

@Getter
public class GptAICBContext extends AbstractAICBContext {
    private final Gpt35TurboClient aiClient = new Gpt35TurboClient();
    private final SessionManager sessionManager = new SessionManager("GPT");
}
