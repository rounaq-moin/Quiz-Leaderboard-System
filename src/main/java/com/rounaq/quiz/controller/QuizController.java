package com.rounaq.quiz.controller;

import com.rounaq.quiz.model.FinalResponse;
import com.rounaq.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService service;

    @GetMapping("/run")
    public ResponseEntity<FinalResponse> run() throws Exception {
        return ResponseEntity.ok(service.execute());
    }
}
