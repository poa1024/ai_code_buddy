package io.github.poa1024.chatgpt.mate;

import io.github.poa1024.chatgpt.mate.gptclient.GptClient;
import io.github.poa1024.chatgpt.mate.gptclient.GptClientImpl;
import io.github.poa1024.chatgpt.mate.session.GptSessionManager;
import lombok.Getter;

@Getter
public class Configuration {

    public final static GptClient GPT_CLIENT = new GptClientImpl();
    public final static GptRequestBuilder GPT_REQUEST_BUILDER = new GptRequestBuilder();
    public final static GptSessionManager GPT_SESSION_MANAGER = new GptSessionManager();

}
