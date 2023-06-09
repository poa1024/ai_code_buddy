package io.github.poa1024.ai.code.buddy.session;

import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import lombok.SneakyThrows;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GenerateCodeSession extends AbstractCodeManipulationSession {


    @SneakyThrows
    public GenerateCodeSession(
            Consumer<String> generatedCodeHandler,
            AIClient aiClient,
            Executor executor,
            String fileText,
            int offset
    ) {
        super(
                generatedCodeHandler,
                aiClient,
                executor,
                createContext(fileText, offset),
                "ai/generate_code_req.ftl"
        );
    }


    @Override
    protected List<String> getCodeVersions() {
        return getHistory().stream()
                .map(AIInteraction::requireResponse)
                .map(AIResponse::getText)
                .collect(Collectors.toList());
    }

    private static String createContext(String context, int offset) {
        return context.substring(0, offset) + "/*your code will be here*/" + context.substring(offset);
    }

}
