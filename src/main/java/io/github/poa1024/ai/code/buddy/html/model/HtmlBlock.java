package io.github.poa1024.ai.code.buddy.html.model;

import lombok.Data;
import org.intellij.lang.annotations.Language;

@Data
public class HtmlBlock {
    @Language("html")
    private final String value;
}
