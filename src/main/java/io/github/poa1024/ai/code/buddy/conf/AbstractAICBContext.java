package io.github.poa1024.ai.code.buddy.conf;

import freemarker.template.Configuration;
import freemarker.template.Version;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.html.HtmlHistoryPrinter;
import io.github.poa1024.ai.code.buddy.mapper.html.ExplainCodeSessionHtmlMapper;
import io.github.poa1024.ai.code.buddy.mapper.html.GenerateCodeSessionHtmlMapper;
import io.github.poa1024.ai.code.buddy.session.SessionManager;
import lombok.Getter;

@Getter
public abstract class AbstractAICBContext {

    //mappers
    private final GenerateCodeSessionHtmlMapper generateCodeSessionHtmlMapper = new GenerateCodeSessionHtmlMapper();
    private final ExplainCodeSessionHtmlMapper explainCodeSessionHtmlMapper = new ExplainCodeSessionHtmlMapper();


    private final Configuration freemarkerConf = new Configuration(new Version("2.3.31"));

    {
        freemarkerConf.setClassForTemplateLoading(this.getClass(), "/templates");
    }

    private final HtmlHistoryPrinter htmlHistoryPrinter = new HtmlHistoryPrinter(freemarkerConf);

    public abstract AIClient getAiClient();

    public abstract SessionManager getSessionManager();

}
