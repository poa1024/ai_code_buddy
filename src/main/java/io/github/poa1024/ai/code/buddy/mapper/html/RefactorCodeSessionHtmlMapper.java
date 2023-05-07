package io.github.poa1024.ai.code.buddy.mapper.html;

import io.github.poa1024.ai.code.buddy.html.model.HtmlAnswerBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlockWithMarginAndPrefix;
import io.github.poa1024.ai.code.buddy.html.model.HtmlCodeBlock;
import io.github.poa1024.ai.code.buddy.session.RefactorCodeSession;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

import static io.github.poa1024.ai.code.buddy.mapper.html.SessionHistoryHtmlMapper.getEscapedText;
import static io.github.poa1024.ai.code.buddy.mapper.html.SessionHistoryHtmlMapper.getEscapedUserInput;

public class RefactorCodeSessionHtmlMapper implements SessionHistoryHtmlMapper<RefactorCodeSession> {

    @Override
    public List<HtmlBlock> mapHistory(RefactorCodeSession session) {
        var res = new ArrayList<HtmlBlock>();
        res.add(new HtmlCodeBlock(StringEscapeUtils.escapeHtml(session.getCode())));

        for (AIInteraction qa : session.getHistory()) {

            var question = new HtmlBlockWithMarginAndPrefix("Request", getEscapedUserInput(qa.getRequest()));
            res.add(question);

            if (qa.getResponse() != null) {
                res.add(mapResToHtml(qa.getResponse()));
            }

        }
        return res;
    }

    private static HtmlBlock mapResToHtml(AIResponse res) {

        if (res.isFailed()) {
            //language=html
            return new HtmlAnswerBlock("""
                                <i>failed parsing result. Raw response:</i> <br> <br>
                                <pre>%s</pre>
                    """.formatted(getEscapedText(res))
            );
        }

        //language=html
        return new HtmlAnswerBlock("""
                            <i>changed code</i> <br> <br>
                            <blockquote>
                                <pre>%s</pre>
                            </blockquote>
                """.formatted(getEscapedText(res))
        );
    }


}
