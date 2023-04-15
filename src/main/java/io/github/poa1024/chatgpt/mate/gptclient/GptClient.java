package io.github.poa1024.chatgpt.mate.gptclient;

import lombok.RequiredArgsConstructor;

import java.util.List;


public interface GptClient {

    GptResponse ask(String text);

    @RequiredArgsConstructor
    class GptMessage {
        private final String role;
        private final String content;
    }

    @RequiredArgsConstructor
    class GptResponse {

        private final List<GptChoice> choices;

        public String getFirstChoice() {
            return choices.get(0).message.content;
        }
    }

    @RequiredArgsConstructor
    class GptChoice {
        private final GptMessage message;
    }

}
