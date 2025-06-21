package com.java_final_project.vocab_typing;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private final WordRepository_jumbo wordRepository;
    private final Queue<WordRecord> wordQueue;
    private final List<WordRecord> reviewedToday;
    private int callCount = 0;
    private final int maxPerDay = 10;
    private final int repeatPerWord = 3;
    private static final int MAX_LIVES = 10;
    private int lives = MAX_LIVES;
    private String selectedGroup = "daily";
    private boolean completedOnce = false;

    public GameService(WordRepository_jumbo repo) {
        this.wordRepository = repo;
        this.wordQueue = new LinkedList<>(repo.getTodayWords(selectedGroup, maxPerDay, repeatPerWord, false));
        this.reviewedToday = new ArrayList<>();
    }

    public void setGroup(String group) {
        this.selectedGroup = group;
        resetGame();
    }

    public WordGameState nextWord() {
        if (wordQueue.isEmpty()) {
            if (!completedOnce && !reviewedToday.isEmpty()) {
                completedOnce = true;
                wordRepository.commitReviewedWords(reviewedToday, true);
            }
            return new WordGameState("", "", 0, lives, true);
        }

        WordRecord word = wordQueue.poll();
        reviewedToday.add(word);

        callCount++;
        int delay = 5000 - Math.min(callCount * 200, 2000); // 5s to 3s
        return new WordGameState(word.word, word.definition, delay, lives, false);
    }

    public void resetGame() {
        resetGame(false);
    }

    public void resetGame(boolean clearHistory) {
        System.out.println("🔥 resetGame called with clearHistory = " + clearHistory);

        if (!clearHistory) {
            wordRepository.commitReviewedWords(reviewedToday, true);
        } else {
            wordRepository.commitReviewedWords(reviewedToday, false);
            wordRepository.load(); // 重讀 JSON
        }

        callCount = 0;
        lives = MAX_LIVES;
        completedOnce = false;
        wordQueue.clear();
        reviewedToday.clear();

        wordQueue.addAll(
                wordRepository.getTodayWords(selectedGroup, maxPerDay, repeatPerWord, clearHistory)
        );

        System.out.println("✅ Reset 成功，載入單字：" + wordQueue.size());
    }



    public WordGameState loseLife() {
        if (!reviewedToday.isEmpty()) {
            WordRecord last = reviewedToday.get(reviewedToday.size() - 1);
            if (last.defeated) {
                return new WordGameState("", "", 0, lives, false); // 不扣命
            }
        }
        lives--;
        boolean gameOver = lives <= 0;
        return new WordGameState("", "", 0, lives, gameOver);
    }

    public void markDefeated(String word) {
        reviewedToday.stream()
                .filter(w -> w.word.equalsIgnoreCase(word))
                .forEach(w -> w.defeated = true);
    }
    //給瀏覽字卡用
    public List<WordRecord> getWordsByGroup(String group) {
        return wordRepository.previewWordset(group);
    }

}
