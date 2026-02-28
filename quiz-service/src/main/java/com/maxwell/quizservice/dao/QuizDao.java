package com.maxwell.quizservice.dao;


import com.maxwell.quizservice.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizDao extends JpaRepository<Quiz, Integer> {

}
