package com.example.quizfinal.client;

import com.example.quizfinal.config.QuizProperties;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiClient {

    private final QuizProperties props;
    private final HttpClient client;

    public String getMessages(String regNo, int poll) throws Exception {
        String url = props.getBaseUrl() + "/quiz/messages?regNo=" + regNo + "&poll=" + poll;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        log.info("Calling poll {}", poll);

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "Poll " + poll + " failed with HTTP " + response.statusCode() + ": " + response.body()
            );
        }

        return response.body();
    }

    public String submit(String body) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(props.getBaseUrl() + "/quiz/submit"))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        log.info("Submitting leaderboard");

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "Submit failed with HTTP " + response.statusCode() + ": " + response.body()
            );
        }

        return response.body();
    }
}
