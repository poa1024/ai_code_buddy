package io.github.poa1024.ai.code.buddy.session;

import freemarker.template.Template;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.util.HashMap;

import static io.github.poa1024.ai.code.buddy.util.TextUtils.*;

public class ExplainCodeSession extends Session implements Focusable {

    private final Template reqTemplate;
    @Getter
    private final String code;
    private final String codeContext;

    @SneakyThrows
    public ExplainCodeSession(AIClient aiClient, Executor executor, String fileText, String code) {
        super(aiClient, executor);
        this.code = code;
        this.codeContext = removeCodeFromTheContext(fileText, code);
        this.reqTemplate = AICBContextHolder.getContext()
                .getFreemarkerConf()
                .getTemplate("ai/explain_code_req.ftl");
    }

    @Override
    @SneakyThrows
    protected AIRequest createRequest(String userInput) {
        var templateModel = new HashMap<>();
        templateModel.put("context", codeContext);
        templateModel.put("code", code);
        templateModel.put("history", prepareConversationHistory(getHistory(), userInput));

        var stringWriter = new StringWriter();
        reqTemplate.process(templateModel, stringWriter);

        return AIRequest.builder()
                .userInput(userInput)
                .body(stringWriter.toString())
                .build();
    }

    @Override
    protected AIResponse processResponse(AIResponse response) {
        return removeAnswerPrefixFromResponse(response);
    }

}
