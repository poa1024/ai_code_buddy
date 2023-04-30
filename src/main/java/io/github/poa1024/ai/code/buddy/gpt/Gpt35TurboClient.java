package io.github.poa1024.ai.code.buddy.gpt;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.poa1024.ai.code.buddy.AIClient;
import io.github.poa1024.ai.code.buddy.context.AICBContextHolder;
import io.github.poa1024.ai.code.buddy.exception.AICBException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;


public class Gpt35TurboClient implements AIClient {

    private static final String GPT_VERSION = "gpt-3.5-turbo";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();


    @SneakyThrows
    @Override
    public String ask(String text) {
        var req = new Request(Collections.singletonList(new Message("user", text)));

        var url = "https://api.openai.com/v1/chat/completions";
        var request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getApikey())
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(req)))
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        var json = gson.fromJson(response.body(), Response.class);

        if (response.statusCode() != 200) {
            if (json.error != null) {
                if ("invalid_api_key".equals(json.error.code)) {
                    throw new AICBException("Invalid OpenAI API key");
                }
            }
            throw new IllegalStateException(response.body());
        }

        return json.getFirstChoice();
    }

    private static String getApikey() {
        var apikey = AICBContextHolder.getContext().getAICBSettings().getApikey();
        if (apikey == null) {
            throw new AICBException("GPT api key is not set");
        }
        return apikey;
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
        private final Error error;

        public String getFirstChoice() {
            return choices.get(0).message.content;
        }
    }

    @RequiredArgsConstructor
    private class Choice {
        private final Message message;
    }

    @RequiredArgsConstructor
    private class Error {
        private final String code;
    }

}
