package io.github.poa1024.ai.code.buddy.session;

import freemarker.template.Template;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import io.github.poa1024.ai.code.buddy.util.TextUtils;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractCodeManipulationSession extends Session {

    private final Template reqTemplate;
    private final Consumer<String> generatedCodeHandler;
    private final String codeContext;

    @SneakyThrows
    public AbstractCodeManipulationSession(
            Consumer<String> generatedCodeHandler,
            AIClient aiClient,
            Executor executor,
            String codeContext,
            String templateName
    ) {
        super(aiClient, executor);
        this.codeContext = codeContext;
        this.generatedCodeHandler = generatedCodeHandler;
        this.reqTemplate = AICBContextHolder.getContext()
                .getFreemarkerConf()
                .getTemplate(templateName);
    }

    @Override
    @SneakyThrows
    protected AIRequest createRequest(String userInput) {
        var templateModel = new HashMap<>();

        templateModel.put("userInput", userInput);
        templateModel.put("context", codeContext);

        List<String> codeVersions = getCodeVersions();

        templateModel.put("codeVersions", codeVersions);

        var stringWriter = new StringWriter();
        reqTemplate.process(templateModel, stringWriter);
        return AIRequest.builder()
                .userInput(userInput)
                .body(stringWriter.toString())
                .build();
    }

    protected abstract List<String> getCodeVersions();

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

}
