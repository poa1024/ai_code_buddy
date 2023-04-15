package io.github.poa1024.util;

import io.github.poa1024.exception.GptRetryableException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {

    public static String toHtml(String rawText) {
        return rawText.lines()
                .collect(Collectors.joining("<br>"))
                .replaceAll("```(.*?)```", "<blockquote><pre>$1</pre></blockquote>");
    }

    public static String cleanCode(String possiblyDirtyCode) {
        String regex = "```[^\\s]*(.*?)```";
        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(possiblyDirtyCode);
        if (matcher.find()) {
            String code = matcher.group(1);
            if (matcher.find()) {
                throw new GptRetryableException("More than one code block is not allowed");
            }
            return code;
        }
        return possiblyDirtyCode;
    }

    public static String removeCodeFromTheContext(String context, String code) {
        return context.replaceAll(code, "/*discussed code is here*/");
    }

}
