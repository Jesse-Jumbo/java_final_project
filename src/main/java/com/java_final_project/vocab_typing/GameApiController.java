package com.java_final_project.vocab_typing;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GameApiController {

    private final GameService gameService;

    public GameApiController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/next-word")
    public WordGameState nextWord() {
        return gameService.nextWord();
    }

    @PostMapping("/life/lost")
    public WordGameState loseLife() {
        gameService.loseLife();
        return gameService.nextWord();
    }

    @PostMapping("/reset")
    public void resetGame() {
        gameService.resetGame();
    }
}
