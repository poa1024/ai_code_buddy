package io.github.poa1024.ai.code.buddy.html.model;

import org.intellij.lang.annotations.Language;

public class HtmlAnswerBlock extends HtmlBlockWithMarginAndPrefix {

    public HtmlAnswerBlock(@Language("html") String value) {
        super("Answer", value);
    }
}
