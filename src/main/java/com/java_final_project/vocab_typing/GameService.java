package com.java_final_project.vocab_typing;

import com.java_final_project.vocab_typing.repository.PersonalConfigRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.java_final_project.vocab_typing.entity.PersonalConfig;


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
    private final PersonalConfigRepository configRepository;


    public void setGroup(String group) {
        this.selectedGroup = group;
        resetGame();
    }

    public GameService(WordRepository_jumbo repo, PersonalConfigRepository configRepository) {
        this.wordRepository = repo;
        this.configRepository = configRepository;
        this.wordQueue = new LinkedList<>(repo.getTodayWords(selectedGroup, maxPerDay, repeatPerWord));
        this.reviewedToday = new ArrayList<>();
    }


    public WordGameState nextWord() {
        if (wordQueue.isEmpty()) {
            boolean allFailed = reviewedToday.stream().noneMatch(w -> w.defeated);
            if (reviewedToday.isEmpty() || allFailed) {
                return new WordGameState("", "", 0, lives, true, false, false); // 全部失敗 → Game Over
            }

            int successCount = (int) reviewedToday.stream()
                    .filter(w -> w.review_count >= repeatPerWord)
                    .count();

            int dailyLimit = configRepository.findByUserId("user0001")
                    .map(PersonalConfig::getDailyWordLimit)
                    .orElse(10);

            if (!completedOnce && successCount >= dailyLimit) {
                completedOnce = true;
                wordRepository.commitReviewedWords(reviewedToday, true);
                return new WordGameState("", "", 0, lives, false, true, false); // ✅ 完成複習
            }

            return new WordGameState("", "", 0, lives, lives <= 0, false, false); // ⚠️ 還沒達成複習門檻
        }

        WordRecord word = wordQueue.poll();
        if (word == null) {
            return new WordGameState("", "", 0, lives, false, false, true); // noWords=true
        }

        reviewedToday.add(word);
        callCount++;

        int delay = 5000 - Math.min(callCount * 200, 2000); // 動態決定速度：5s→3s
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

    //給瀏覽字卡用
    public List<WordRecord> getWordsByGroup(String group) {
        return wordRepository.previewWordset(group);
    }

    public void setGroupFromReviewApi(String userName, String group) {
        // call /api/review/today?userName=xxx&setName=xxx
        String urlToday = String.format("http://localhost:8080/api/review/today?userName=%s&setName=%s", userName, group);
        ResponseEntity<List> response = new RestTemplate().getForEntity(urlToday, List.class);
        List<Map<String, Object>> todayWords = response.getBody();

        if (todayWords.size() < 10) {
            String urlUnplayedBySet = String.format("http://localhost:8080/api/review/unplayed-by-set?userName=%s&setName=%s", userName, group);
            ResponseEntity<List> resUnplayed = new RestTemplate().getForEntity(urlUnplayedBySet, List.class);
            List<Map<String, Object>> unplayed = resUnplayed.getBody();
            Collections.shuffle(unplayed);

            for (Map<String, Object> word : unplayed) {
                if (todayWords.size() >= 10) break;
                todayWords.add(word);
            }
        }

        // 初始化遊戲資料
        callCount = 0;
        lives = MAX_LIVES;
        reviewedToday.clear();
        wordQueue.clear();

        List<WordRecord> repeatedWords = new ArrayList<>();
        for (Map<String, Object> wordData : todayWords) {
            for (int i = 0; i < repeatPerWord; i++) {
                WordRecord record = new WordRecord();
                record.word = (String) wordData.get("word");
                record.definition = (String) wordData.get("definition");
                record.userName = userName;
                record.group = (String) wordData.get("setName");
                record.review_count = 0;
                repeatedWords.add(record);
            }
        }

        // ✅ 打散順序
        Collections.shuffle(repeatedWords);

        // 加入 wordQueue
        wordQueue.addAll(repeatedWords);
        completedOnce = false;
    }
}
