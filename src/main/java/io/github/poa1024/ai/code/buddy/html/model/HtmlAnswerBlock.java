package io.github.poa1024.ai.code.buddy.html.model;

import org.intellij.lang.annotations.Language;

public class HtmlAnswerBlock extends HtmlBlock {

    public HtmlAnswerBlock(@Language("html") String value) {
        super("<b>Answer:</b>" + new HtmlBlockWithMargin(value).getValue());
    }
}
