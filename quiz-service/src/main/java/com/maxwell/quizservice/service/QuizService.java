package com.maxwell.quizservice.service;


import com.maxwell.quizservice.dao.QuizDao;
import com.maxwell.quizservice.feign.QuizInterface;
import com.maxwell.quizservice.model.QuestionWrapper;
import com.maxwell.quizservice.model.Quiz;
import com.maxwell.quizservice.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuizInterface quizInterface;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        try {
            List<Integer> questions = quizInterface.getQuestionsForQuiz(category, numQ).getBody();
            if (questions == null || questions.isEmpty()) {
                log.warn("No questions found for category: {}", category);
                return new ResponseEntity<>("No questions available for category: " + category, HttpStatus.NOT_FOUND);
            }
            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setQuestionIds(questions);
            quizDao.save(quiz);
            return new ResponseEntity<>("Quiz created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Failed to create quiz for category: {}", category, e);
            return new ResponseEntity<>("Failed to create quiz", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Quiz quiz = quizDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        List<Integer> questionIds = quiz.getQuestionIds();
        return quizInterface.getQuestionsFromId(questionIds);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        return quizInterface.getScore(responses);
    }
}
