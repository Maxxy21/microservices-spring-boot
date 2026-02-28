package com.maxwell.quizservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxwell.quizservice.model.QuestionWrapper;
import com.maxwell.quizservice.model.QuizDto;
import com.maxwell.quizservice.model.Response;
import com.maxwell.quizservice.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuizControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuizService quizService;

    @InjectMocks
    private QuizController quizController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private QuestionWrapper wrapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quizController).build();
        wrapper = new QuestionWrapper(1, "What is Java?", "A language", "A coffee", "A framework", "An OS");
    }

    @Test
    void createQuiz_returns201OnSuccess() throws Exception {
        when(quizService.createQuiz(eq("Java"), eq(5), eq("Java Quiz")))
                .thenReturn(new ResponseEntity<>("Quiz created successfully", HttpStatus.CREATED));

        QuizDto dto = new QuizDto();
        dto.setTitle("Java Quiz");
        dto.setCategoryName("Java");
        dto.setNumberOfQuestions(5);

        mockMvc.perform(post("/quiz/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Quiz created successfully"));
    }

    @Test
    void getQuizQuestions_returns200WithQuestions() throws Exception {
        when(quizService.getQuizQuestions(1))
                .thenReturn(new ResponseEntity<>(List.of(wrapper), HttpStatus.OK));

        mockMvc.perform(get("/quiz/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].questionTitle").value("What is Java?"));
    }

    @Test
    void submitQuiz_returnsScore() throws Exception {
        when(quizService.calculateResult(eq(1), anyList()))
                .thenReturn(new ResponseEntity<>(3, HttpStatus.OK));

        Response r = new Response();
        r.setId(1);
        r.setResponse("A language");

        mockMvc.perform(post("/quiz/submit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(r))))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}
