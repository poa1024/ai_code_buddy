package io.github.poa1024.gpt;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;


public class GptClient {

    private static final String GPT_VERSION = "gpt-3.5-turbo";

    private final String apiKey = System.getenv("OPEN_AI_API_KEY");
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @SneakyThrows
    public GptResponse ask(String text) {
        var req = new GptRequest(Collections.singletonList(new GptMessage("user", text)));

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

        return gson.fromJson(response.body(), GptResponse.class);
    }

    @RequiredArgsConstructor
    private static class GptRequest {
        private final String model = GPT_VERSION;
        private final List<GptMessage> messages;
    }

    @RequiredArgsConstructor
    private static class GptMessage {
        private final String role;
        private final String content;
    }

    @RequiredArgsConstructor
    public static class GptResponse {

        private final List<GptChoice> choices;

        public String getFirstChoice() {
            return choices.get(0).message.content;
        }
    }

    @RequiredArgsConstructor
    public static class GptChoice {
        private final GptMessage message;
    }

}
