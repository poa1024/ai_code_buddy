package io.github.poa1024.chatgpt.mate.gptclient;

import lombok.RequiredArgsConstructor;

import java.util.List;


public interface GptClient {

    Response ask(String text);

    @RequiredArgsConstructor
    class Message {
        private final String role;
        private final String content;
    }

    @RequiredArgsConstructor
    class Response {

        private final List<Choice> choices;

        public String getFirstChoice() {
            return choices.get(0).message.content;
        }
    }

    @RequiredArgsConstructor
    class Choice {
        private final Message message;
    }

}
