package com.java_final_project.vocab_typing;


public class WordGameState {
    public String word;
    public String definition;
    public int durationMs;
    public int lives;
    public boolean gameOver;
    public boolean completed;

    public WordGameState(String word, String definition, int durationMs, int lives, boolean gameOver, boolean completed) {
        this.word = word;
        this.definition = definition;
        this.durationMs = durationMs;
        this.lives = lives;
        this.gameOver = gameOver;
        this.completed = completed;
    }
}
