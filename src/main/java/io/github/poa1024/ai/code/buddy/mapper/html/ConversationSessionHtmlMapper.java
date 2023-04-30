package io.github.poa1024.ai.code.buddy.mapper.html;

import io.github.poa1024.ai.code.buddy.html.model.HtmlAnswerBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlQuestionBlock;
import io.github.poa1024.ai.code.buddy.session.ConversationSession;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ConversationSessionHtmlMapper implements SessionHistoryHtmlMapper<ConversationSession> {

    @Override
    public List<HtmlBlock> mapHistory(ConversationSession session) {
        var res = new ArrayList<HtmlBlock>();

        for (AIInteraction qa : session.getHistory()) {

            res.add(new HtmlQuestionBlock(qa.getRequest().getUserInput()));

            if (qa.getResponse() != null) {
                res.add(new HtmlAnswerBlock(
                        TextUtils.rawTextToHtml(qa.getResponse().getText())
                ));
            }

        }
        return res;
    }
}
