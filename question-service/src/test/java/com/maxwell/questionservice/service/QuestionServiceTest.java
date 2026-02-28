package com.maxwell.questionservice.service;

import com.maxwell.questionservice.dao.QuestionDao;
import com.maxwell.questionservice.model.Question;
import com.maxwell.questionservice.model.QuestionWrapper;
import com.maxwell.questionservice.model.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private QuestionService questionService;

    private Question question;

    @BeforeEach
    void setUp() {
        question = new Question();
        question.setId(1);
        question.setQuestionTitle("What is Java?");
        question.setOption1("A language");
        question.setOption2("A coffee");
        question.setOption3("A framework");
        question.setOption4("An OS");
        question.setRightAnswer("A language");
        question.setDifficultylevel("Easy");
        question.setCategory("Java");
    }

    @Test
    void getAllQuestions_returnsAllQuestions() {
        when(questionDao.findAll()).thenReturn(List.of(question));

        ResponseEntity<List<Question>> response = questionService.getAllQuestions();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getQuestionTitle()).isEqualTo("What is Java?");
    }

    @Test
    void getAllQuestions_onException_returnsInternalServerError() {
        when(questionDao.findAll()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<List<Question>> response = questionService.getAllQuestions();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getQuestionsByCategory_returnsMatchingQuestions() {
        when(questionDao.findByCategory("Java")).thenReturn(List.of(question));

        ResponseEntity<List<Question>> response = questionService.getQuestionsByCategory("Java");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getCategory()).isEqualTo("Java");
    }

    @Test
    void getQuestionsByCategory_onException_returnsInternalServerError() {
        when(questionDao.findByCategory(any())).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<List<Question>> response = questionService.getQuestionsByCategory("Java");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void addQuestion_savesAndReturnsCreated() {
        when(questionDao.save(question)).thenReturn(question);

        ResponseEntity<String> response = questionService.addQuestion(question);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Question added successfully");
        verify(questionDao).save(question);
    }

    @Test
    void addQuestion_onException_returnsInternalServerError() {
        when(questionDao.save(any())).thenThrow(new RuntimeException("Constraint violation"));

        ResponseEntity<String> response = questionService.addQuestion(question);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Failed to add question");
    }

    @Test
    void getQuestionsForQuiz_returnsRandomIds() {
        when(questionDao.findRandomQuestionsByCategory("Java", 2)).thenReturn(List.of(1, 2));

        ResponseEntity<List<Integer>> response = questionService.getQuestionsForQuiz("Java", 2);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void getQuestionsForQuiz_whenNoneFound_returnsNotFound() {
        when(questionDao.findRandomQuestionsByCategory("Unknown", 5)).thenReturn(List.of());

        ResponseEntity<List<Integer>> response = questionService.getQuestionsForQuiz("Unknown", 5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getQuestionsFromId_returnsWrappers() {
        when(questionDao.findById(1)).thenReturn(Optional.of(question));

        ResponseEntity<List<QuestionWrapper>> response = questionService.getQuestionsFromId(List.of(1));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        QuestionWrapper wrapper = response.getBody().get(0);
        assertThat(wrapper.getId()).isEqualTo(1);
        assertThat(wrapper.getQuestionTitle()).isEqualTo("What is Java?");
        assertThat(wrapper.getOption1()).isEqualTo("A language");
    }

    @Test
    void getQuestionsFromId_whenQuestionMissing_throwsException() {
        when(questionDao.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.getQuestionsFromId(List.of(99)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getScore_countsCorrectAnswers() {
        Response correctResponse = new Response();
        correctResponse.setId(1);
        correctResponse.setResponse("A language");

        Response wrongResponse = new Response();
        wrongResponse.setId(1);
        wrongResponse.setResponse("A coffee");

        when(questionDao.findById(1)).thenReturn(Optional.of(question));

        ResponseEntity<Integer> correctResult = questionService.getScore(List.of(correctResponse));
        assertThat(correctResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(correctResult.getBody()).isEqualTo(1);

        ResponseEntity<Integer> wrongResult = questionService.getScore(List.of(wrongResponse));
        assertThat(wrongResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(wrongResult.getBody()).isEqualTo(0);
    }

    @Test
    void getScore_withEmptyResponses_returnsZero() {
        ResponseEntity<Integer> response = questionService.getScore(List.of());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(0);
    }

    @Test
    void getScore_whenQuestionMissing_throwsException() {
        Response r = new Response();
        r.setId(99);
        r.setResponse("anything");

        when(questionDao.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.getScore(List.of(r)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }
}
