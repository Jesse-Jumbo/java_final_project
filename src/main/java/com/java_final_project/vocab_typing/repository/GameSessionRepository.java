package com.java_final_project.vocab_typing.repository;

import com.java_final_project.vocab_typing.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Integer> {
    // 可依需求擴充查詢方法
}