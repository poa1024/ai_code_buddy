package io.github.poa1024.ai.code.buddy.session;

import freemarker.template.Template;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.conf.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ExplainCodeSession extends Session {

    private final Template reqTemplate;
    @Getter
    private final String code;

    @SneakyThrows
    public ExplainCodeSession(AIClient aiClient, Executor executor, String context, String code) {
        super(aiClient, executor, removeCodeFromTheContext(context, code));
        this.code = code;
        this.reqTemplate = AICBContextHolder.getContext()
                .getFreemarkerConf()
                .getTemplate("ai/explain_code_req.ftl");
    }

    @Override
    @SneakyThrows
    protected AIRequest createRequest(String userInput) {

        if (getHistory().isEmpty()) {
            userInput = "Give me a short explanation of what's happening in the code. " +
                        "Try to be concise." +
                        "Your answer should not contain the code itself. Just the explanation.";
        }

        var templateModel = new HashMap<>();
        templateModel.put("context", initialContext);
        templateModel.put("code", code);
        templateModel.put("history", prepareConversationHistory(userInput));

        var stringWriter = new StringWriter();
        reqTemplate.process(templateModel, stringWriter);

        return AIRequest.builder()
                .userInput(userInput)
                .body(stringWriter.toString())
                .build();
    }

    @Override
    protected AIResponse processResponse(AIResponse response) {
        if (response.getText().trim().startsWith("A:")) {
            var trimmedRes = response.getText().replaceFirst("^[\\s\\t]*A:[\\s\\t]*", "");
            return new AIResponse(trimmedRes);
        }
        return response;
    }

    private List<Map.Entry<String, String>> prepareConversationHistory(String userInput) {
        var historyForReq = new ArrayList<Map.Entry<String, String>>();

        for (AIInteraction qa : getHistory()) {
            historyForReq.add(
                    Pair.of(
                            qa.getRequest().getUserInput(),
                            qa.getResponse().getText()
                    )
            );
        }

        historyForReq.add(Pair.of(userInput, ""));
        return historyForReq;
    }

    public static String removeCodeFromTheContext(String context, String code) {
        return context.replaceAll(Pattern.quote(code), "/*discussed code is here*/");
    }

}
