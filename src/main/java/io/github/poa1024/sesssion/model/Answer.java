package io.github.poa1024.sesssion.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Nullable;

@Data
@AllArgsConstructor
public class Answer {
    private final String text;
    @Nullable
    private String desc;
}
