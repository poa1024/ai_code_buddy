package io.github.poa1024.ai.code.buddy.mapper.html;

import io.github.poa1024.ai.code.buddy.html.model.HtmlAnswerBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import io.github.poa1024.ai.code.buddy.html.model.HtmlQuestionBlock;
import io.github.poa1024.ai.code.buddy.session.GenerateCodeSession;
import io.github.poa1024.ai.code.buddy.session.model.AIResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateCodeSessionHtmlMapper implements SessionHistoryHtmlMapper<GenerateCodeSession> {

    @Override
    public List<HtmlBlock> mapHistory(GenerateCodeSession session) {
        return session.getHistory()
                .stream()
                .flatMap(qa -> Stream.of(
                                new HtmlQuestionBlock(qa.getRequest().getQuestion().text()),
                                mapResToHtml(qa.getResponse())
                        ).filter(Objects::nonNull)
                )
                .collect(Collectors.toList());
    }

    private static HtmlAnswerBlock mapResToHtml(AIResponse res) {

        if (res == null) {
            return null;
        }

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
                            <i>generated code</i> <br> <br>
                            <blockquote>
                                <pre>%s</pre>
                            </blockquote>
                """.formatted(res.getText())
        );
    }

}
