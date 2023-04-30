package io.github.poa1024.ai.code.buddy.session;

import freemarker.template.Template;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import io.github.poa1024.ai.code.buddy.util.TextUtils;
import lombok.SneakyThrows;
import lombok.val;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GenerateCodeSession extends Session {

    private final Template reqTemplate;

    private final Consumer<String> generatedCodeHandler;

    private final String codeContext;

    @SneakyThrows
    public GenerateCodeSession(
            Consumer<String> generatedCodeHandler,
            AIClient aiClient,
            Executor executor,
            String fileText,
            int offset
    ) {
        super(aiClient, executor);
        this.codeContext = createContext(fileText, offset);
        this.generatedCodeHandler = generatedCodeHandler;
        this.reqTemplate = AICBContextHolder.getContext()
                .getFreemarkerConf()
                .getTemplate("ai/generate_code_req.ftl");
    }

    @Override
    @SneakyThrows
    protected AIRequest createRequest(String userInput) {
        var templateModel = new HashMap<>();

        templateModel.put("userInput", userInput);
        templateModel.put("context", codeContext);

        val codeVersions = getHistory().stream()
                .map(AIInteraction::requireResponse)
                .map(AIResponse::getText)
                .collect(Collectors.toList());

        templateModel.put("codeVersions", codeVersions);

        var stringWriter = new StringWriter();
        reqTemplate.process(templateModel, stringWriter);
        return AIRequest.builder()
                .userInput(userInput)
                .body(stringWriter.toString())
                .build();
    }

    @Override
    protected AIResponse processResponse(AIResponse response) {
        try {
            var code = TextUtils.cleanCode(response.getText());
            generatedCodeHandler.accept(code);
            return new AIResponse(code);
        } catch (IllegalArgumentException e) {
            return new AIResponse(response.getText(), true);
        }
    }


    private static String createContext(String context, int offset) {
        return context.substring(0, offset) + "/*your code will be here*/" + context.substring(offset);
    }


}
