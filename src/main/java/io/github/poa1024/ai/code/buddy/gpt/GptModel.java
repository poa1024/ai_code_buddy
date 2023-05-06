package io.github.poa1024.ai.code.buddy.gpt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GptModel {

    GPT_35_TURBO("gpt-3.5-turbo"), GPT_4("gpt-4");

    private final String name;

    public static GptModel create(String name) {
        for (GptModel gptModel : values()) {
            if (gptModel.name.equals(name)) {
                return gptModel;
            }
        }
        throw new IllegalStateException("Unknown gpt model " + name);
    }
}
