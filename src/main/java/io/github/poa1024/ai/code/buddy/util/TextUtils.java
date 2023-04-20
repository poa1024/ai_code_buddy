package io.github.poa1024.ai.code.buddy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    public static String cleanCode(String possiblyDirtyCode) {
        String regex = "```[^\\s]*(.*?)```";
        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(possiblyDirtyCode);
        if (matcher.find()) {
            String code = matcher.group(1);
            if (matcher.find()) {
                throw new IllegalArgumentException("More than one code block is not allowed");
            }
            return code;
        }
        return possiblyDirtyCode;
    }

}
