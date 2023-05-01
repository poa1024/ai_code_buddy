package io.github.poa1024.ai.code.buddy.session;

import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.poa1024.ai.code.buddy.util.TextUtils.removeCodeFromTheContext;

public class RefactorCodeSession extends AbstractCodeManipulationSession implements Focusable {

    @Getter
    private final String code;

    @SneakyThrows
    public RefactorCodeSession(
            Consumer<String> generatedCodeHandler,
            AIClient aiClient,
            Executor executor,
            String fileText,
            String code
    ) {
        super(
                generatedCodeHandler,
                aiClient,
                executor,
                removeCodeFromTheContext(fileText, code),
                "ai/refactor_code_req.ftl"
        );
        this.code = code;
    }

    @Override
    protected List<String> getCodeVersions() {
        return Stream
                .concat(
                        Stream.of(code),
                        getHistory().stream()
                                .map(AIInteraction::requireResponse)
                                .map(AIResponse::getText))
                .collect(Collectors.toList());
    }

}
