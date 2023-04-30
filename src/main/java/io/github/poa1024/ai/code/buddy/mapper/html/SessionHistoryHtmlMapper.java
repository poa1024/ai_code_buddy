package io.github.poa1024.ai.code.buddy.mapper.html;

import io.github.poa1024.ai.code.buddy.html.model.HtmlBlock;
import io.github.poa1024.ai.code.buddy.session.Session;

import java.util.List;

public interface SessionHistoryHtmlMapper<T extends Session> {

    List<HtmlBlock> mapHistory(T t);

}
