package io.github.poa1024.ai.code.buddy.mapper.html;

import io.github.poa1024.ai.code.buddy.html.model.HtmlAnswerBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlCodeBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlQuestionBlock;
import io.github.poa1024.ai.code.buddy.session.ExplainCodeSession;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.util.TextUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

import static io.github.poa1024.ai.code.buddy.mapper.html.SessionHistoryHtmlMapper.getEscapedText;
import static io.github.poa1024.ai.code.buddy.mapper.html.SessionHistoryHtmlMapper.getEscapedUserInput;

public class ExplainCodeSessionHtmlMapper implements SessionHistoryHtmlMapper<ExplainCodeSession> {

    @Override
    public List<HtmlBlock> mapHistory(ExplainCodeSession session) {
        var res = new ArrayList<HtmlBlock>();
        res.add(new HtmlCodeBlock(StringEscapeUtils.escapeHtml(session.getCode())));

        if (session.getHistory().isEmpty()) {
            res.add(new HtmlBlock("<h3><i>&nbsp;Ask something...</i></h3>"));
        } else {

            for (AIInteraction qa : session.getHistory()) {

                res.add(new HtmlQuestionBlock(getEscapedUserInput(qa.getRequest())));

                if (qa.getResponse() != null) {
                    res.add(new HtmlAnswerBlock(
                            TextUtils.rawTextToHtml(getEscapedText(qa.getResponse()))
                    ));
                }

            }
        }
        return res;
    }
}
