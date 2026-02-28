package com.maxwell.questionservice.service;

import com.maxwell.questionservice.dao.QuestionDao;
import com.maxwell.questionservice.model.Question;
import com.maxwell.questionservice.model.QuestionWrapper;
import com.maxwell.questionservice.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to fetch all questions", e);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try {
            return new ResponseEntity<>(questionDao.findByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to fetch questions for category: {}", category, e);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        try {
            questionDao.save(question);
            return new ResponseEntity<>("Question added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Failed to add question", e);
            return new ResponseEntity<>("Failed to add question", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, int numberOfQuestions) {
        try {
            List<Integer> questions = questionDao.findRandomQuestionsByCategory(categoryName, numberOfQuestions);
            if (questions.isEmpty()) {
                log.warn("No questions found for category: {}", categoryName);
                return new ResponseEntity<>(questions, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to fetch questions for quiz, category: {}", categoryName, e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
        List<QuestionWrapper> wrappers = new ArrayList<>();
        for (Integer id : questionIds) {
            Question question = questionDao.findById(id)
                    .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
            wrappers.add(new QuestionWrapper(
                    question.getId(),
                    question.getQuestionTitle(),
                    question.getOption1(),
                    question.getOption2(),
                    question.getOption3(),
                    question.getOption4()));
        }
        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {
        int right = 0;
        for (Response response : responses) {
            Question question = questionDao.findById(response.getId())
                    .orElseThrow(() -> new RuntimeException("Question not found with id: " + response.getId()));
            if (response.getResponse().equals(question.getRightAnswer())) {
                right++;
            }
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
