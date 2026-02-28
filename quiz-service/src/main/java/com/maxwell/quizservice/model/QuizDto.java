package com.maxwell.quizservice.model;

import lombok.Data;

@Data
public class QuizDto {
    private String title;
    private Integer numberOfQuestions;
    private String categoryName;
}
