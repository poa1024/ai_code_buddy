package io.github.poa1024.chatgpt.mate.session.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GptInteraction {

    private final GptRequest gptRequest;
    @Nullable
    private GptResponse gptResponse;

    public GptResponse requireGptResponse() {
        if (gptResponse == null) {
            throw new IllegalStateException();
        }
        return gptResponse;
    }

}
