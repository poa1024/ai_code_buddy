package io.github.poa1024.chatgpt.mate.session;

import io.github.poa1024.chatgpt.mate.Configuration;
import io.github.poa1024.chatgpt.mate.Executor;
import io.github.poa1024.chatgpt.mate.GptClient;
import io.github.poa1024.chatgpt.mate.session.model.GptInteraction;
import io.github.poa1024.chatgpt.mate.session.model.GptRequest;
import io.github.poa1024.chatgpt.mate.session.model.GptResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class GptSession {

    private final GptClient gptClient = Configuration.GPT_CLIENT;

    protected final Executor executor;
    protected final String initialContext;

    protected final List<GptInteraction> history = new ArrayList<>();

    protected GptSession(Executor executor, String initialContext) {
        this.executor = executor;
        this.initialContext = initialContext;
    }

    public void proceed(String userInput, Runnable onAnyChangesCallback) {
        var req = createRequest(userInput);
        history.add(new GptInteraction(req));
        onAnyChangesCallback.run();
        executor.execute("GPT request...", () -> {
            synchronized (GptSession.class) {
                var firstChoice = gptClient
                        .ask(req.getBody())
                        .getFirstChoice();
                var res = new GptResponse(firstChoice);
                handleResponse(res);
                getLastInteraction().setGptResponse(res);
                onAnyChangesCallback.run();
            }
        });
    }

    private GptInteraction getLastInteraction() {
        return history.get(history.size() - 1);
    }

    protected abstract GptRequest createRequest(String userInput);

    protected abstract void handleResponse(GptResponse gptResponse);

    protected abstract List<Pair<String, String>> getPrintableHtmlHistory();


}
