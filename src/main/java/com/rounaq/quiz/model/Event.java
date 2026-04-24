package com.rounaq.quiz.model;

import lombok.Data;

@Data
public class Event {
    private String roundId;
    private String participant;
    private int score;
}
