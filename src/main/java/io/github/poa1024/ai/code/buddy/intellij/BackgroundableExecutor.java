package io.github.poa1024.ai.code.buddy.intellij;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import io.github.poa1024.ai.code.buddy.Executor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class BackgroundableExecutor implements Executor {

    private final ProgressManager progressManager = ProgressManager.getInstance();
    private final Project project;

    @Override
    public void execute(String executionName, Runnable command) {
        progressManager.run(new Task.Backgroundable(project, executionName) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                command.run();
            }
        });
    }
}
