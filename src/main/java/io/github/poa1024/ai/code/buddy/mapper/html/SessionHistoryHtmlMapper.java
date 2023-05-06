package io.github.poa1024.ai.code.buddy.mapper.html;

import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import io.github.poa1024.ai.code.buddy.session.Session;
import io.github.poa1024.ai.code.buddy.session.model.AIRequest;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;

public interface SessionHistoryHtmlMapper<T extends Session> {

    List<HtmlBlock> mapHistory(T t);

    static String getEscapedUserInput(AIRequest req) {
        return StringEscapeUtils.escapeHtml(req.getUserInput());
    }

    static String getEscapedText(AIResponse res) {
        return StringEscapeUtils.escapeHtml(res.getText());
    }

}
