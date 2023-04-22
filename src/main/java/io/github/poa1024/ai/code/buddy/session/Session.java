package io.github.poa1024.ai.code.buddy.session;

import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Session {

    protected final AIClient aiClient;
    protected final Executor executor;
    protected final String initialContext;

    private final List<AIInteraction> history = new ArrayList<>();

    protected Session(AIClient aiClient, Executor executor, String initialContext) {
        this.aiClient = aiClient;
        this.executor = executor;
        this.initialContext = initialContext;
    }


    public void proceed(String userInput, Runnable onAnyChangesCallback) {
        var req = createRequest(userInput);
        history.add(new AIInteraction(req));
        onAnyChangesCallback.run();
        executor.execute("Asking...", () -> {
            synchronized (Session.class) {
                var firstChoice = aiClient
                        .ask(req.getBody());
                var res = new AIResponse(firstChoice);
                res = processResponse(res);
                getLastInteraction().setResponse(res);
                onAnyChangesCallback.run();
            }
        });
    }

    private AIInteraction getLastInteraction() {
        return history.get(history.size() - 1);
    }

    protected abstract AIRequest createRequest(String userInput);

    protected abstract AIResponse processResponse(AIResponse response);

}
