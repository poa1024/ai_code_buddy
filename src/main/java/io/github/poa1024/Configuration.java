package io.github.poa1024;

import io.github.poa1024.sesssion.GptCodeGenerationSessionManager;
import io.github.poa1024.gpt.GptClient;
import io.github.poa1024.gpt.GptQuestionBuilder;
import lombok.Getter;

@Getter
public class Configuration {

    public final static GptClient GPT_CLIENT = new GptClient();
    public final static GptQuestionBuilder GPT_QUESTION_BUILDER = new GptQuestionBuilder();
    public final static GptCodeGenerationSessionManager GPT_CODE_GENERATION_SESSION_MANAGER = new GptCodeGenerationSessionManager();

}
