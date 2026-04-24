package com.rounaq.quiz.service;

import com.rounaq.quiz.client.ApiClient;
import com.rounaq.quiz.config.QuizProperties;
import com.rounaq.quiz.model.ApiResponse;
import com.rounaq.quiz.model.ApiSubmitResponse;
import com.rounaq.quiz.model.Event;
import com.rounaq.quiz.model.FinalResponse;
import com.rounaq.quiz.model.LeaderboardEntry;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final ApiClient apiClient;
    private final QuizProperties props;
    private final Gson gson;

    private String getWithRetry(int poll) throws Exception {
        for (int i = 0; i < 3; i++) {
            try {
                return apiClient.getMessages(props.getRegNo(), poll);
            } catch (Exception e) {
                log.warn("Poll {} failed attempt {}", poll, i + 1);
                if (i == 2) {
                    throw e;
                }
                Thread.sleep(1000);
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    private <T> T parseResponse(String rawJson, Class<T> type, String operation) {
        if (rawJson == null || rawJson.isBlank()) {
            throw new IllegalStateException(operation + " returned an empty response");
        }

        try {
            JsonElement element = gson.fromJson(rawJson, JsonElement.class);

            while (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String nested = element.getAsString();
                if (nested.isBlank()) {
                    throw new IllegalStateException(operation + " returned a blank string response");
                }
                element = gson.fromJson(nested, JsonElement.class);
            }

            if (element == null || !element.isJsonObject()) {
                throw new IllegalStateException(
                        operation + " returned an unexpected payload: " + rawJson
                );
            }

            return gson.fromJson(element, type);
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    operation + " response could not be parsed. Raw body: " + rawJson,
                    e
            );
        }
    }

    public FinalResponse execute() throws Exception {
        Map<String, Integer> scoreMap = new HashMap<>();
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < props.getPollCount(); i++) {
            ApiResponse res = parseResponse(getWithRetry(i), ApiResponse.class, "Poll " + i);

            if (res.getEvents() != null) {
                for (Event event : res.getEvents()) {
                    String key = event.getRoundId() + "|" + event.getParticipant();

                    if (seen.add(key)) {
                        scoreMap.put(
                                event.getParticipant(),
                                scoreMap.getOrDefault(event.getParticipant(), 0) + event.getScore()
                        );
                    }
                }
            }

            log.info("Poll {}/{} complete", i + 1, props.getPollCount());

            if (i < props.getPollCount() - 1) {
                Thread.sleep(props.getDelayMillis());
            }
        }

        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
            leaderboard.add(new LeaderboardEntry(entry.getKey(), entry.getValue()));
        }

        leaderboard.sort(Comparator.comparingInt(LeaderboardEntry::getTotalScore).reversed());

        log.info("Processed {} unique events, {} participants", seen.size(), leaderboard.size());

        int total = leaderboard.stream()
                .mapToInt(LeaderboardEntry::getTotalScore)
                .sum();

        Map<String, Object> payload = new HashMap<>();
        payload.put("regNo", props.getRegNo());
        payload.put("leaderboard", leaderboard);

        ApiSubmitResponse result = parseResponse(
                apiClient.submit(gson.toJson(payload)),
                ApiSubmitResponse.class,
                "Submit"
        );

        if (Boolean.FALSE.equals(result.getIsCorrect())) {
            throw new RuntimeException(
                    "Submission failed: " + (result.getMessage() == null ? "validator marked it incorrect" : result.getMessage())
            );
        }

        if (result.getSubmittedTotal() == null || result.getSubmittedTotal() != total) {
            throw new RuntimeException(
                    "Submission failed: validator accepted a different total. Local total="
                            + total
                            + ", validator total="
                            + result.getSubmittedTotal()
            );
        }

        if (result.getExpectedTotal() != null && result.getExpectedTotal() != total) {
            throw new RuntimeException(
                    "Submission failed: expected "
                            + result.getExpectedTotal()
                            + " got "
                            + result.getSubmittedTotal()
            );
        }

        String message = result.getMessage();
        if (message == null || message.isBlank()) {
            message = "Submission accepted";
        }

        return new FinalResponse(leaderboard, total, message);
    }
}
