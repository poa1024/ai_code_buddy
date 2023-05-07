package io.github.poa1024.ai.code.buddy.html.model;

public class HtmlCodeBlock extends HtmlBlockWithMargin {

    public HtmlCodeBlock(String code) {
        super("<br><blockquote><pre>" + code + "</pre></blockquote>");
    }
}
