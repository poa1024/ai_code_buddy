package io.github.poa1024.ai.code.buddy.session.model;

import io.github.poa1024.ai.code.buddy.model.HumanReadableText;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AIRequest {

    private final HumanReadableText question;
    private final String body;

}
