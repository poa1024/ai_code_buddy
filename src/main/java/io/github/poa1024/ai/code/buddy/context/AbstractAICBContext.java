package io.github.poa1024.ai.code.buddy.context;

import com.intellij.openapi.application.ApplicationManager;
import freemarker.template.Configuration;
import freemarker.template.Version;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.html.HtmlHistoryPrinter;
import io.github.poa1024.ai.code.buddy.intellij.conf.AICBSettings;
import io.github.poa1024.ai.code.buddy.mapper.html.ConversationSessionHtmlMapper;
import io.github.poa1024.ai.code.buddy.mapper.html.ExplainCodeSessionHtmlMapper;
import io.github.poa1024.ai.code.buddy.mapper.html.GenerateCodeSessionHtmlMapper;
import io.github.poa1024.ai.code.buddy.mapper.html.RefactorCodeSessionHtmlMapper;
import io.github.poa1024.ai.code.buddy.session.SessionManager;
import lombok.Getter;

public abstract class AbstractAICBContext {

    //mappers
    @Getter
    private final GenerateCodeSessionHtmlMapper generateCodeSessionHtmlMapper = new GenerateCodeSessionHtmlMapper();
    @Getter
    private final RefactorCodeSessionHtmlMapper refactorCodeSessionHtmlMapper = new RefactorCodeSessionHtmlMapper();
    @Getter
    private final ExplainCodeSessionHtmlMapper explainCodeSessionHtmlMapper = new ExplainCodeSessionHtmlMapper();
    @Getter
    private final ConversationSessionHtmlMapper conversationSessionHtmlMapper = new ConversationSessionHtmlMapper();

    @Getter
    private final Configuration freemarkerConf = new Configuration(new Version("2.3.31"));

    {
        freemarkerConf.setClassForTemplateLoading(this.getClass(), "/templates");
    }

    @Getter
    private final HtmlHistoryPrinter htmlHistoryPrinter = new HtmlHistoryPrinter(freemarkerConf);

    public AICBSettings getAICBSettings() {
        return ApplicationManager.getApplication().getService(AICBSettings.class);
    }

    public abstract AIClient getAiClient();

    public abstract SessionManager getSessionManager();

}
