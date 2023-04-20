package io.github.poa1024.ai.code.buddy.session;

import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.AIRequestBuilder;
import io.github.poa1024.ai.code.buddy.Executor;
import io.github.poa1024.ai.code.buddy.conf.Configuration;
import io.github.poa1024.ai.code.buddy.model.HtmlBlockWithMargin;
import io.github.poa1024.ai.code.buddy.model.HumanReadableText;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ExplainCodeSession extends Session {

    private final AIRequestBuilder aiRequestBuilder = Configuration.getInstance().getAiRequestBuilder();
    private final String code;

    public ExplainCodeSession(AIClient aiClient, Executor executor, String context, String code) {
        super(aiClient, executor, removeCodeFromTheContext(context, code));
        this.code = code;
    }

    @Override
    protected AIRequest createRequest(String userInput) {
        String request;
        if (history.isEmpty()) {
            userInput = "Explain the code";
            request = aiRequestBuilder.askForShortExplanation();
            request = aiRequestBuilder.appendCode(request, code);

        } else {
            request = aiRequestBuilder.continueDialog(userInput);
            request = aiRequestBuilder.appendCode(request, code);
            request = aiRequestBuilder.appendHistory(request, history);
        }
        request = aiRequestBuilder.appendContext(request, initialContext);
        return AIRequest.builder()
                .question(new HumanReadableText(userInput))
                .body(request)
                .build();
    }

    @Override
    protected List<Pair<HtmlBlockWithMargin, HtmlBlockWithMargin>> getPrintableHtmlHistory() {

        var res = new ArrayList<Pair<HtmlBlockWithMargin, HtmlBlockWithMargin>>();

        boolean first = true;

        for (AIInteraction qa : history) {

            HtmlBlockWithMargin question;
            HtmlBlockWithMargin answer = qa.getResponse() != null ? qa.getResponse().asHtmlBlock() : null;


            if (first) {
                //language=html
                question = new HtmlBlockWithMargin("""
                                <i>asked to explain the code</i> <br> <br>
                                <blockquote>
                                    <pre>%s</pre>
                                </blockquote>
                        """.formatted(code)
                );
                first = false;
            } else {
                //language=html
                question = new HtmlBlockWithMargin(qa.getRequest().getQuestion().text());
            }
            res.add(Pair.of(question, answer));
        }
        return res;
    }

    public static String removeCodeFromTheContext(String context, String code) {
        return context.replaceAll(Pattern.quote(code), "/*discussed code is here*/");
    }

}
