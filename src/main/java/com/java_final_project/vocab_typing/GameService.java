package com.java_final_project.vocab_typing;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private final WordRepository wordRepository;
    private final Queue<WordRecord> wordQueue;
    private final List<WordRecord> reviewedToday;
    private int callCount = 0;
    private final int maxPerDay = 10;
    private final int repeatPerWord = 3; // 每個單字出現 3 次
    private static final int MAX_LIVES = 10;
    private int lives = MAX_LIVES;
    private String selectedGroup = "daily"; // 預設 daily
    private boolean completedOnce = false;


    public void setGroup(String group) {
        this.selectedGroup = group;
        resetGame();
    }

    public GameService(WordRepository repo) {
        this.wordRepository = repo;
        this.wordQueue = new LinkedList<>(repo.getTodayWords(selectedGroup, maxPerDay, repeatPerWord));
        this.reviewedToday = new ArrayList<>();
    }

    public WordGameState nextWord() {
        if (wordQueue.isEmpty()) {
            if (reviewedToday.isEmpty()) {
                // 沒有可複習的單字，直接回傳 noWords=true
                return new WordGameState("", "", 0, lives, false, false, true);
            }

            if (!completedOnce) {
                completedOnce = true;
                wordRepository.commitReviewedWords(reviewedToday, true);
                return new WordGameState("", "", 0, lives, false, true, false); // 勝利畫面
            } else {
                boolean gameOver = lives <= 0;
                return new WordGameState("", "", 0, lives, gameOver, false, false);
            }
        }

        WordRecord word = wordQueue.poll();
        if (word == null) {
            // 防止 NPE，這應該理論上不會發生，但為保險加上
            return new WordGameState("", "", 0, lives, false, false, true);
        }

        reviewedToday.add(word);
        callCount++;
        int delay = 5000 - Math.min(callCount * 200, 2000); // 5s to 3s

        return new WordGameState(word.word, word.definition, delay, lives, false, false, false);
    }


    public void resetGame() {
        callCount = 0;
        lives = MAX_LIVES;
        wordQueue.clear();
        reviewedToday.clear();
        wordQueue.addAll(wordRepository.getTodayWords(selectedGroup, maxPerDay, repeatPerWord));
    }

    public WordGameState loseLife() {
        if (!reviewedToday.isEmpty()) {
            WordRecord last = reviewedToday.get(reviewedToday.size() - 1);
            if (last.defeated) {
                return new WordGameState("", "", 0, lives, false, false, false); // ✅ 不扣命
            }
        }
        if (lives <= 0) {
            return new WordGameState("", "", 0, lives, true, false, false); // 避免再扣命
        }
        lives--;
        boolean gameOver = lives <= 0;
        return new WordGameState("", "", 0, lives, gameOver, false, false);
    }

    public void markDefeated(String word) {
        reviewedToday.stream()
                .filter(w -> w.word.equalsIgnoreCase(word))
                .forEach(w -> w.defeated = true);
    }
}

