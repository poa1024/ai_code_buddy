package io.github.poa1024.ai.code.buddy.session.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AIRequest {

    private final String userInput;
    private final String body;

}
