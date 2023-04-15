package io.github.poa1024.session;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import io.github.poa1024.Configuration;
import io.github.poa1024.gpt.GptClient;
import io.github.poa1024.session.model.GptInteraction;
import io.github.poa1024.session.model.GptRequest;
import io.github.poa1024.session.model.GptResponse;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class GptSession {

    private final GptClient gptClient = Configuration.GPT_CLIENT;
    private final ProgressManager progressManager = ProgressManager.getInstance();

    protected final PsiFile psiFile;
    protected final String initialContext;

    protected final List<GptInteraction> history = new ArrayList<>();

    protected GptSession(PsiFile psiFile, String initialContext) {
        this.psiFile = psiFile;
        this.initialContext = initialContext;
    }

    public void proceed(String userInput, Runnable onAnyChangesCallback) {
        var req = createRequest(userInput);
        history.add(new GptInteraction(req));
        onAnyChangesCallback.run();
        progressManager.run(new Task.Backgroundable(psiFile.getProject(), "GPT request...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                synchronized (GptSession.class) {
                    var firstChoice = gptClient
                            .ask(req.getBody())
                            .getFirstChoice();
                    var res = new GptResponse(firstChoice);
                    handleResponse(res);
                    getLastInteraction().setGptResponse(res);
                    onAnyChangesCallback.run();
                }
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
