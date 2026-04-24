package com.rounaq.quiz.model;

import java.util.List;
import lombok.Data;

@Data
public class ApiResponse {
    private String regNo;
    private String setId;
    private int pollIndex;
    private List<Event> events;
}
