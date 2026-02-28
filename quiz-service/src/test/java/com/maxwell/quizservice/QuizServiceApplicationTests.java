package com.maxwell.quizservice;

import com.maxwell.quizservice.feign.QuizInterface;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class QuizServiceApplicationTests {

    @MockitoBean
    QuizInterface quizInterface;

    @Test
    void contextLoads() {
    }
}
