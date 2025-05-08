package com.java_final_project.vocab_typing;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameService {

    // 假資料詞庫（未來用資料庫或檔案替換）
    private final List<Word> mockWords = List.of(
            new Word("apple", "蘋果"),
            new Word("run", "跑步"),
            new Word("banana", "香蕉"),
            new Word("jump", "跳躍"),
            new Word("computer", "電腦"),
            new Word("school", "學校"),
            new Word("book", "書"),
            new Word("table", "桌子"),
            new Word("chair", "椅子"),
            new Word("window", "窗戶")
    );

    private int currentIndex = 0;
    private int callCount = 0;
    private int lives = 10;

    // 取得下一個單字（會循環）
    public WordGameState nextWord() {
        callCount++;

        Word word = mockWords.get(currentIndex);
        currentIndex = (currentIndex + 1) % mockWords.size();

        // 初始 30 秒，最多加快至 10 秒
        int maxDelay = 30000;
        int minDelay = 10000;
        int duration = maxDelay - Math.min(callCount * 1000, maxDelay - minDelay);

        boolean gameOver = lives <= 0;
        if (gameOver) {
            return new WordGameState("", "", 0, lives, true);
        }
        return new WordGameState(
                word.word(),
                word.definition(),
                duration,
                lives,
                gameOver
        );
    }

    public void loseLife() {
        lives--;
    }

    public void resetGame() {
        callCount = 0;
        currentIndex = 0;
        lives = 10;
    }

    public int getCurrentDelay() {
        // 從第 1 次呼叫開始，依次縮短間隔秒數（最多快到 3 秒）
        int delay = 5000 - Math.min(callCount * 100, 2000); // 最少 3000ms
        return delay;
    }

    // 檢查玩家輸入是否正確（大小寫忽略）
    public boolean checkAnswer(String userInput, String correctWord) {
        return userInput.trim().equalsIgnoreCase(correctWord);
    }
}
