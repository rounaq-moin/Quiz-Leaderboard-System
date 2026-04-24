package com.example.quizfinal.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FinalResponse {
    private List<LeaderboardEntry> leaderboard;
    private int totalScore;
    private String message;
}
