package io.github.poa1024.ai.code.buddy.mapper.html;

import io.github.poa1024.ai.code.buddy.model.html.HtmlBlock;
import io.github.poa1024.ai.code.buddy.session.Session;

import java.util.List;
import java.util.stream.Collectors;

public interface SessionHistoryHtmlMapper<T extends Session> {

    List<HtmlBlock> map(T t);

    static String rawTextToHtml(String text) {
        return text.lines()
                .collect(Collectors.joining("<br>"))
                .replaceAll("```(.*?)```", "<blockquote><pre>$1</pre></blockquote>");
    }

}
