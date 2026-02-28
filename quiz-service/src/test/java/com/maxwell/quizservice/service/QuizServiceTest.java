package com.maxwell.quizservice.service;

import com.maxwell.quizservice.dao.QuizDao;
import com.maxwell.quizservice.feign.QuizInterface;
import com.maxwell.quizservice.model.QuestionWrapper;
import com.maxwell.quizservice.model.Quiz;
import com.maxwell.quizservice.model.Response;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizDao quizDao;

    @Mock
    private QuizInterface quizInterface;

    @InjectMocks
    private QuizService quizService;

    private Quiz quiz;
    private QuestionWrapper wrapper;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        quiz.setId(1);
        quiz.setTitle("Java Basics");
        quiz.setQuestionIds(List.of(1, 2, 3));

        wrapper = new QuestionWrapper(1, "What is Java?", "A language", "A coffee", "A framework", "An OS");
    }

    @Test
    void createQuiz_savesQuizAndReturnsCreated() {
        when(quizInterface.getQuestionsForQuiz("Java", 3))
                .thenReturn(new ResponseEntity<>(List.of(1, 2, 3), HttpStatus.OK));
        when(quizDao.save(any(Quiz.class))).thenReturn(quiz);

        ResponseEntity<String> response = quizService.createQuiz("Java", 3, "Java Basics");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Quiz created successfully");
        verify(quizDao).save(any(Quiz.class));
    }

    @Test
    void createQuiz_whenNoQuestionsAvailable_returnsNotFound() {
        when(quizInterface.getQuestionsForQuiz("Unknown", 5))
                .thenReturn(new ResponseEntity<>(List.of(), HttpStatus.OK));

        ResponseEntity<String> response = quizService.createQuiz("Unknown", 5, "Empty Quiz");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Unknown");
        verify(quizDao, never()).save(any());
    }

    @Test
    void createQuiz_whenFeignThrows_returnsInternalServerError() {
        when(quizInterface.getQuestionsForQuiz(any(), anyInt()))
                .thenThrow(new RuntimeException("Service unavailable"));

        ResponseEntity<String> response = quizService.createQuiz("Java", 3, "Java Basics");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(quizDao, never()).save(any());
    }

    @Test
    void getQuizQuestions_returnsQuestionsForQuiz() {
        when(quizDao.findById(1)).thenReturn(Optional.of(quiz));
        when(quizInterface.getQuestionsFromId(List.of(1, 2, 3)))
                .thenReturn(new ResponseEntity<>(List.of(wrapper), HttpStatus.OK));

        ResponseEntity<List<QuestionWrapper>> response = quizService.getQuizQuestions(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getQuestionTitle()).isEqualTo("What is Java?");
    }

    @Test
    void getQuizQuestions_whenQuizNotFound_throwsException() {
        when(quizDao.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizService.getQuizQuestions(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void calculateResult_delegatesToQuizInterface() {
        Response r = new Response();
        r.setId(1);
        r.setResponse("A language");

        when(quizInterface.getScore(anyList()))
                .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

        ResponseEntity<Integer> response = quizService.calculateResult(1, List.of(r));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(1);
    }
}
