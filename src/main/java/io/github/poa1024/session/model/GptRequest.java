package io.github.poa1024.session.model;

import io.github.poa1024.util.HumanReadableText;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GptRequest {

    private final HumanReadableText question;
    private final String body;

}
