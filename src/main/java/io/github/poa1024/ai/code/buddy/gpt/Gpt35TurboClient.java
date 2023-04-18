package io.github.poa1024.ai.code.buddy.gpt;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.util.PropertiesUtil;
import io.github.poa1024.ai.code.buddy.AIClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;


public class Gpt35TurboClient implements AIClient {

    private static final String GPT_VERSION = "gpt-3.5-turbo";

    private final String apiKey;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @SneakyThrows
    public Gpt35TurboClient() {
        //todo refactor this insanity
        try (var reader = new FileReader(System.getProperty("user.home") + "/chatgpt_intellij_plugin.properties")) {
            var properties = PropertiesUtil.loadProperties(reader);
            apiKey = properties.get("openai_api_key");
        }
    }

    @SneakyThrows
    @Override
    public String ask(String text) {
        var req = new Request(Collections.singletonList(new Message("user", text)));

        var url = "https://api.openai.com/v1/chat/completions";
        var request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(req)))
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException(response.body());
        }

        return gson.fromJson(response.body(), Response.class).getFirstChoice();
    }

    @RequiredArgsConstructor
    private static class Request {
        private final String model = GPT_VERSION;
        private final List<Message> messages;
    }

    @RequiredArgsConstructor
    private class Message {
        private final String role;
        private final String content;
    }

    @RequiredArgsConstructor
    private class Response {

        private final List<Choice> choices;

        public String getFirstChoice() {
            return choices.get(0).message.content;
        }
    }

    @RequiredArgsConstructor
    private class Choice {
        private final Message message;
    }

}