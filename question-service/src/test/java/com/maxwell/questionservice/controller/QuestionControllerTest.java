package com.maxwell.questionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxwell.questionservice.model.Question;
import com.maxwell.questionservice.model.QuestionWrapper;
import com.maxwell.questionservice.model.Response;
import com.maxwell.questionservice.service.QuestionService;
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
class QuestionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Question question;
    private QuestionWrapper wrapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();

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

        wrapper = new QuestionWrapper(1, "What is Java?", "A language", "A coffee", "A framework", "An OS");
    }

    @Test
    void getAllQuestions_returns200WithQuestions() throws Exception {
        when(questionService.getAllQuestions())
                .thenReturn(new ResponseEntity<>(List.of(question), HttpStatus.OK));

        mockMvc.perform(get("/question/allQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionTitle").value("What is Java?"))
                .andExpect(jsonPath("$[0].category").value("Java"));
    }

    @Test
    void getQuestionsByCategory_returns200WithFilteredQuestions() throws Exception {
        when(questionService.getQuestionsByCategory("Java"))
                .thenReturn(new ResponseEntity<>(List.of(question), HttpStatus.OK));

        mockMvc.perform(get("/question/category/Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Java"));
    }

    @Test
    void addQuestion_returns201OnSuccess() throws Exception {
        when(questionService.addQuestion(any(Question.class)))
                .thenReturn(new ResponseEntity<>("Question added successfully", HttpStatus.CREATED));

        mockMvc.perform(post("/question/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(question)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Question added successfully"));
    }

    @Test
    void generateQuiz_returnsListOfIds() throws Exception {
        when(questionService.getQuestionsForQuiz("Java", 2))
                .thenReturn(new ResponseEntity<>(List.of(1, 2), HttpStatus.OK));

        mockMvc.perform(get("/question/generate")
                        .param("categoryName", "Java")
                        .param("numberOfQuestions", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2));
    }

    @Test
    void getQuestionsFromId_returnsWrappers() throws Exception {
        when(questionService.getQuestionsFromId(List.of(1)))
                .thenReturn(new ResponseEntity<>(List.of(wrapper), HttpStatus.OK));

        mockMvc.perform(post("/question/getQuestions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].questionTitle").value("What is Java?"));
    }

    @Test
    void getScore_returnsCorrectCount() throws Exception {
        when(questionService.getScore(anyList()))
                .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

        Response response = new Response();
        response.setId(1);
        response.setResponse("A language");

        mockMvc.perform(post("/question/getScore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(response))))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}
