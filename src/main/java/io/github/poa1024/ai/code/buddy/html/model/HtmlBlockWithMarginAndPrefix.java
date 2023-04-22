package io.github.poa1024.ai.code.buddy.html.model;

import org.intellij.lang.annotations.Language;

public class HtmlBlockWithMarginAndPrefix extends HtmlBlock {

    public HtmlBlockWithMarginAndPrefix(String prefix, @Language("html") String value) {
        super("<b>" + prefix + ":</b>" + new HtmlBlockWithMargin(value).getValue());
    }
}
