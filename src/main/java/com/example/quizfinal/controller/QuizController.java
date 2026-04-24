package com.example.quizfinal.controller;

import com.example.quizfinal.model.FinalResponse;
import com.example.quizfinal.service.QuizService;
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
