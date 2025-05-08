package com.java_final_project.vocab_typing;

public record WordGameState(
        String word,
        String definition,
        int durationMs,
        int lives,
        boolean gameOver
) {}
