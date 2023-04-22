package io.github.poa1024.ai.code.buddy.html.model;

import org.intellij.lang.annotations.Language;

public class HtmlQuestionBlock extends HtmlBlock {

    public HtmlQuestionBlock(@Language("html") String value) {
        super("<b>Question:</b>" + new HtmlBlockWithMargin(value).getValue());
    }
}
