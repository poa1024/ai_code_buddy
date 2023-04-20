package io.github.poa1024.ai.code.buddy.model;

import org.intellij.lang.annotations.Language;

public class HtmlBlockWithMargin extends HtmlBlock {

    public HtmlBlockWithMargin(@Language("html") String value) {
        super("""
                    <div class="block-with-margin">%s</div>
                """.formatted(value)
        );
    }
}
