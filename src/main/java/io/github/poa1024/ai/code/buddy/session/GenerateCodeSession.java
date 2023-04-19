package io.github.poa1024.ai.code.buddy.session;

import freemarker.template.Template;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.conf.Configuration;
import io.github.poa1024.ai.code.buddy.model.HumanReadableText;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import io.github.poa1024.ai.code.buddy.util.TextUtils;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GenerateCodeSession extends Session {

    private final Template reqTemplate;

    private final Consumer<String> generatedCodeHandler;

    @SneakyThrows
    public GenerateCodeSession(
            Consumer<String> generatedCodeHandler,
            AIClient aiClient,
            Executor executor,
            String context,
            int offset
    ) {
        super(aiClient, executor, createContext(context, offset));
        this.generatedCodeHandler = generatedCodeHandler;
        this.reqTemplate = Configuration.getInstance()
                .getFreemarkerConf()
                .getTemplate("ai/code_generation_req.ftl");
    }

    @Override
    @SneakyThrows
    protected AIRequest createRequest(String userInput) {
        var templateModel = new HashMap<>();

        templateModel.put("userInput", userInput);
        templateModel.put("context", initialContext);

        val codeVersions = history.stream()
                .map(AIInteraction::requireResponse)
                .map(AIResponse::getText)
                .collect(Collectors.toList());

        templateModel.put("codeVersions", codeVersions);

        var stringWriter = new StringWriter();
        reqTemplate.process(templateModel, stringWriter);
        return AIRequest.builder()
                .question(new HumanReadableText(userInput))
                .body(stringWriter.toString())
                .build();
    }

    @Override
    protected void handleResponse(AIResponse response) {
        response.setText(TextUtils.cleanCode(response.getText()));
        var code = response.getText();
        generatedCodeHandler.accept(code);
    }


    @Override
    protected List<Pair<String, String>> getPrintableHtmlHistory() {
        return history.stream()
                .map(qa -> Pair.of(
                                qa.getRequest().getQuestion().text(),
                                qa.getResponse() != null ? "<i>code was generated successfully</i>" : null
                        )
                )
                .collect(Collectors.toList());
    }

    private static String createContext(String context, int offset) {
        return context.substring(0, offset) + "/*your code will be here*/" + context.substring(offset);
    }


}
