package io.github.poa1024.ai.code.buddy.session.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class AIResponse {
    private final String text;
    private boolean failed = false;
}
