package io.github.poa1024.ai.code.buddy.html;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class HtmlHistoryPrinter {

    private final Template historyTemplate;

    @SneakyThrows
    public HtmlHistoryPrinter(Configuration configuration) {
        this.historyTemplate = configuration.getTemplate("ui/history.html");
    }

    @SneakyThrows
    public String printAsString(List<HtmlBlock> conversation) {
        var conversationAsString = conversation.stream()
                .map(HtmlBlock::getValue)
                .collect(Collectors.joining());

        var templateModel = new HashMap<String, Object>();
        templateModel.put("body", conversationAsString);

        var stringWriter = new StringWriter();
        historyTemplate.process(templateModel, stringWriter);
        return stringWriter.toString();
    }

}
