package io.github.poa1024.ai.code.buddy.session.model;

import io.github.poa1024.ai.code.buddy.model.HtmlBlockWithMargin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class AIResponse {
    private final String text;
    private boolean failed = false;

    public HtmlBlockWithMargin asHtmlBlock() {
        return new HtmlBlockWithMargin(text.lines()
                .collect(Collectors.joining("<br>"))
                .replaceAll("```(.*?)```", "<blockquote><pre>$1</pre></blockquote>"));
    }

}
