package io.github.poa1024.chatgpt.mate.session.model;

import io.github.poa1024.chatgpt.mate.model.HumanReadableText;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GptRequest {

    private final HumanReadableText question;
    private final String body;

}
