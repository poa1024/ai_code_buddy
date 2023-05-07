package io.github.poa1024.ai.code.buddy.mapper.html;

import io.github.poa1024.ai.code.buddy.html.model.HtmlAnswerBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlQuestionBlock;
import io.github.poa1024.ai.code.buddy.session.ConversationSession;
import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;
import io.github.poa1024.ai.code.buddy.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

import static io.github.poa1024.ai.code.buddy.mapper.html.SessionHistoryHtmlMapper.getEscapedText;
import static io.github.poa1024.ai.code.buddy.mapper.html.SessionHistoryHtmlMapper.getEscapedUserInput;

public class ConversationSessionHtmlMapper implements SessionHistoryHtmlMapper<ConversationSession> {

    @Override
    public List<HtmlBlock> mapHistory(ConversationSession session) {
        var res = new ArrayList<HtmlBlock>();

        if (session.getHistory().isEmpty()) {
            return List.of(new HtmlBlock("<h3><i>&nbsp;Ask something...</i></h3>"));
        }

        for (AIInteraction qa : session.getHistory()) {

            res.add(new HtmlQuestionBlock(getEscapedUserInput(qa.getRequest())));

            if (qa.getResponse() != null) {
                res.add(new HtmlAnswerBlock(
                        TextUtils.rawTextToHtml(getEscapedText(qa.getResponse()))
                ));
            }

        }
        return res;
    }
}
