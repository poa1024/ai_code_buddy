package io.github.poa1024.ai.code.buddy.mapper.html;

import io.github.poa1024.ai.code.buddy.html.model.HtmlAnswerBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlockWithMarginAndPrefix;
import io.github.poa1024.ai.code.buddy.session.RefactorCodeSession;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;

import java.util.ArrayList;
import java.util.List;

public class RefactorCodeSessionHtmlMapper implements SessionHistoryHtmlMapper<RefactorCodeSession> {

    @Override
    public List<HtmlBlock> mapHistory(RefactorCodeSession session) {
        var res = new ArrayList<HtmlBlock>();

        boolean first = true;

        for (AIInteraction qa : session.getHistory()) {

            HtmlBlock question;

            if (first) {
                //language=html
                question = new HtmlBlockWithMarginAndPrefix("Request", """
                                <i>asked to refactor the code</i> <br> <br>
                                <blockquote>
                                    <pre>%s</pre>
                                </blockquote>
                        """.formatted(session.getCode())
                );
                first = false;
            } else {
                //language=html
                question = new HtmlBlockWithMarginAndPrefix("Request", qa.getRequest().getUserInput());
            }
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
                    """.formatted(res.getText())
            );
        }

        //language=html
        return new HtmlAnswerBlock("""
                            <i>changed code</i> <br> <br>
                            <blockquote>
                                <pre>%s</pre>
                            </blockquote>
                """.formatted(res.getText())
        );
    }


}
