package com.rounaq.quiz.model;

import lombok.Data;

@Data
public class ApiSubmitResponse {
    private String regNo;
    private Integer totalPollsMade;
    private Integer submittedTotal;
    private Integer expectedTotal;
    private Integer attemptCount;
    private Boolean isCorrect;
    private Boolean isIdempotent;
    private String message;
}
