package io.github.poa1024.ai.code.buddy.conf;

import freemarker.template.Version;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.AIRequestBuilder;
import io.github.poa1024.ai.code.buddy.gpt.Gpt35TurboClient;
import io.github.poa1024.ai.code.buddy.session.SessionManager;
import lombok.Getter;

public interface Configuration {

    AIClient getAiClient();

    AIRequestBuilder getAiRequestBuilder();

    SessionManager getSessionManager();

    freemarker.template.Configuration getFreemarkerConf();

    GptConfiguration GPT_CONFIGURATION = new GptConfiguration();

    //can be switched to different implementation
    static Configuration getInstance() {
        return GPT_CONFIGURATION;
    }

    @Getter
    class GptConfiguration implements Configuration {

        private final Gpt35TurboClient aiClient = new Gpt35TurboClient();
        private final AIRequestBuilder aiRequestBuilder = new AIRequestBuilder();
        private final SessionManager sessionManager = new SessionManager("GPT");
        private final freemarker.template.Configuration freemarkerConf = new freemarker.template.Configuration(new Version("2.3.31"));

        {
            freemarkerConf.setClassForTemplateLoading(this.getClass(), "/templates");
        }

    }


}
