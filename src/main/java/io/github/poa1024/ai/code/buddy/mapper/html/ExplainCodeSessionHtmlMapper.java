package io.github.poa1024.ai.code.buddy.mapper.html;

import io.github.poa1024.ai.code.buddy.html.model.HtmlAnswerBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlQuestionBlock;
import io.github.poa1024.ai.code.buddy.session.ExplainCodeSession;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

import static io.github.poa1024.ai.code.buddy.mapper.html.SessionHistoryHtmlMapper.getEscapedText;
import static io.github.poa1024.ai.code.buddy.mapper.html.SessionHistoryHtmlMapper.getEscapedUserInput;

public class ExplainCodeSessionHtmlMapper implements SessionHistoryHtmlMapper<ExplainCodeSession> {

    @Override
    public List<HtmlBlock> mapHistory(ExplainCodeSession session) {
        var res = new ArrayList<HtmlBlock>();

        boolean first = true;

        for (AIInteraction qa : session.getHistory()) {

            HtmlBlock question;

            if (first) {
                //language=html
                question = new HtmlQuestionBlock("""
                                <i>asked to explain the code</i> <br> <br>
                                <blockquote>
                                    <pre>%s</pre>
                                </blockquote>
                        """.formatted(session.getCode())
                );
                first = false;
            } else {
                //language=html
                question = new HtmlQuestionBlock(getEscapedUserInput(qa.getRequest()));
            }
            res.add(question);

            if (qa.getResponse() != null) {
                res.add(new HtmlAnswerBlock(
                        TextUtils.rawTextToHtml(getEscapedText(qa.getResponse()))
                ));
            }

        }
        return res;
    }
}
