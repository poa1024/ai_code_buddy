package io.github.poa1024.ai.code.buddy.util;

import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {


    private static final String CODE_BLOCK_REGEX = "```\\S*(.*?)```";

    public static String rawTextToHtml(String text) {
        return text.lines()
                .collect(Collectors.joining("<br>"))
                .replaceAll(CODE_BLOCK_REGEX, "<blockquote><pre>$1</pre></blockquote>");
    }

    public static String cleanCode(String possiblyDirtyCode) {
        Matcher matcher = Pattern.compile(CODE_BLOCK_REGEX, Pattern.DOTALL).matcher(possiblyDirtyCode);
        if (matcher.find()) {
            String code = matcher.group(1);
            if (matcher.find()) {
                throw new IllegalArgumentException("More than one code block is not allowed");
            }
            return code;
        }
        return possiblyDirtyCode;
    }


    public static List<Entry<String, String>> prepareConversationHistory(List<AIInteraction> history, String userInput) {
        var historyForReq = new ArrayList<Entry<String, String>>();

        for (AIInteraction qa : history) {
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

    public static AIResponse removeAnswerPrefixFromResponse(AIResponse response) {
        if (response.getText().trim().startsWith("A:")) {
            var trimmedRes = response.getText().replaceFirst("^[\\s\\t]*A:[\\s\\t]*", "");
            return new AIResponse(trimmedRes);
        }
        return response;
    }


    public static String removeCodeFromTheContext(String context, String code) {
        return context.replaceAll(Pattern.quote(code), "/*discussed code is here*/");
    }

}
