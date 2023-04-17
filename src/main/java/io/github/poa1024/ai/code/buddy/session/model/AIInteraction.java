package io.github.poa1024.ai.code.buddy.session.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AIInteraction {

    private final AIRequest request;
    @Nullable
    private AIResponse response;

    public AIResponse requireResponse() {
        if (response == null) {
            throw new IllegalStateException();
        }
        return response;
    }

}
