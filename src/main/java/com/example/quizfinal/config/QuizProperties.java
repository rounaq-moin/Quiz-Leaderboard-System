package com.example.quizfinal.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Configuration
@ConfigurationProperties(prefix = "quiz")
@Validated
public class QuizProperties {

    @NotBlank
    private String regNo;

    @NotBlank
    private String baseUrl;

    @Min(1)
    private int pollCount = 10;

    @Min(1000)
    private int delayMillis = 5000;
}
