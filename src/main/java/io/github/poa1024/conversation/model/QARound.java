package io.github.poa1024.conversation.model;

import lombok.Data;

import javax.annotation.Nullable;

@Data
public class QARound {

    private final Question question;
    @Nullable
    private Answer answer;

}
