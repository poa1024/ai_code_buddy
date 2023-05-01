package io.github.poa1024.ai.code.buddy.session;

import freemarker.template.Template;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.util.HashMap;

import static io.github.poa1024.ai.code.buddy.util.TextUtils.prepareConversationHistory;
import static io.github.poa1024.ai.code.buddy.util.TextUtils.removeAnswerPrefixFromResponse;

public class ConversationSession extends Session implements Focusable {

    private final Template reqTemplate;

    @SneakyThrows
    public ConversationSession(AIClient aiClient, Executor executor) {
        super(aiClient, executor);
        this.reqTemplate = AICBContextHolder.getContext()
                .getFreemarkerConf()
                .getTemplate("ai/conversation_req.ftl");
    }

    @Override
    @SneakyThrows
    protected AIRequest createRequest(String userInput) {

        var templateModel = new HashMap<>();
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
