package com.java_final_project.vocab_typing.controller;

import com.java_final_project.vocab_typing.entity.GameSession;
import com.java_final_project.vocab_typing.entity.User;
import com.java_final_project.vocab_typing.repository.GameSessionRepository;
import com.java_final_project.vocab_typing.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/api/game")
public class GameSessionController {

    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;

    public GameSessionController(GameSessionRepository gameSessionRepository, UserRepository userRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
    }

    //儲存一場遊戲紀錄
    @PostMapping("/record")
    public ResponseEntity<String> saveGameSession(@RequestBody GameSessionRequest request) {

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 使用者不存在：" + request.getUserId());
        }

        GameSession session = new GameSession();
        session.setUser(userOpt.get());
        session.setGameMode(request.getGameMode());
        session.setScore(request.getScore());
        session.setDurationSec(request.getDurationSec());
        session.setCorrectCount(request.getCorrectCount());
        session.setMissCount(request.getMissCount());
        session.setPlayedAt(new Timestamp(System.currentTimeMillis()));

        gameSessionRepository.save(session);

        return ResponseEntity.ok("成功儲存遊戲紀錄！");
    }

    //接收用的 Request DTO
    public static class GameSessionRequest {
        private String userId;
        private GameSession.GameMode gameMode;
        private Integer score;
        private Integer durationSec;
        private Integer correctCount;
        private Integer missCount;

        // Getters and Setters
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public GameSession.GameMode getGameMode() {
            return gameMode;
        }

        public void setGameMode(GameSession.GameMode gameMode) {
            this.gameMode = gameMode;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public Integer getDurationSec() {
            return durationSec;
        }

        public void setDurationSec(Integer durationSec) {
            this.durationSec = durationSec;
        }

        public Integer getCorrectCount() {
            return correctCount;
        }

        public void setCorrectCount(Integer correctCount) {
            this.correctCount = correctCount;
        }

        public Integer getMissCount() {
            return missCount;
        }

        public void setMissCount(Integer missCount) {
            this.missCount = missCount;
        }
    }
}
