package com.java_final_project.vocab_typing.controller;

import com.java_final_project.vocab_typing.entity.*;
import com.java_final_project.vocab_typing.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/review")
public class ReviewLogController {

    private final ReviewLogRepository reviewLogRepository;
    private final WordRepository wordRepository;
    private final UserRepository userRepository;
    private final PersonalConfigRepository personalConfigRepository;
    private final WordSetWordRepository wordSetWordRepository; // 同下

    public ReviewLogController(
        ReviewLogRepository reviewLogRepository,
        WordRepository wordRepository,
        UserRepository userRepository,
        PersonalConfigRepository personalConfigRepository,
        WordSetWordRepository wordSetWordRepository
        
    ) {
        this.reviewLogRepository = reviewLogRepository;
        this.wordRepository = wordRepository;
        this.userRepository = userRepository;
        this.personalConfigRepository = personalConfigRepository;
        this.wordSetWordRepository = wordSetWordRepository;
    }

    // 查詢今天要玩的題目：從 review_logs 中挑出 dailyWordLimit 數量
    @GetMapping("/today")
    public ResponseEntity<List<Word>> getTodayWordsToReview(
            @RequestParam String userId,
            @RequestParam String setName) {

        // 1. 查找使用者
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 2. 查找使用者的 daily limit 設定
        Optional<PersonalConfig> configOpt = personalConfigRepository.findByUserId(userId);
        if (configOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        int dailyWordLimit = configOpt.get().getDailyWordLimit();

        // 3. 撈出今天應該複習的資料
        List<ReviewLog> dueList = reviewLogRepository.findDueReviews(userId, setName, LocalDateTime.now());

        // 4. 挑出要回傳的單字（依時間排序 + 限制筆數）
        List<Word> words = dueList.stream()
                .sorted(Comparator.comparing(ReviewLog::getNextReviewAt))
                .limit(dailyWordLimit)
                .map(ReviewLog::getWord)
                .collect(Collectors.toList());

        return ResponseEntity.ok(words);
    }


    // ✅ 初始化 review_logs：加入新單字進入複習（避免重複）
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeReviewLog(
            @RequestParam String userId,
            @RequestParam String setName) {

        // 1. 查找使用者
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 使用者不存在: " + userId);
        }
        User user = userOpt.get();

        // 2. 查找使用者的個人偏好設定
        Optional<PersonalConfig> configOpt = personalConfigRepository.findByUserId(userId);
        if (configOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 找不到個人偏好設定，請先建立 PERSONAL_CONFIG");
        }
        PersonalConfig config = configOpt.get();
        int dailyWordLimit = config.getDailyWordLimit();
        int reviewThreshold = config.getReviewThreshold();

        // 3. 撈出這個 setName 所有單字的 ID
        List<Long> allWordIds = wordSetWordRepository.findWordIdsBySystemOrUser(userId, setName);

        // 4. 找出已有 review log 的 wordId（避免重複建立）
        List<Long> existing = reviewLogRepository.findExistingWordIds(userId, setName);

        // 5. 篩選出還沒加入過 review log 的單字
        List<Long> toInsert = allWordIds.stream()
                .filter(id -> !existing.contains(id))
                .limit(dailyWordLimit)
                .collect(Collectors.toList());

        for (Long wordId : toInsert) {
            Optional<Word> wordOpt = wordRepository.findById(wordId);
            if (wordOpt.isEmpty()) continue;

            ReviewLog log = new ReviewLog();
            log.setUser(user);
            log.setWord(wordOpt.get());
            log.setSetName(setName);
            log.setReviewThreshold(reviewThreshold);
            log.setReviewCount(0);
            log.setNextReviewAt(LocalDateTime.now());
            log.setLastReviewAt(null);
            log.setStatus("NEW");

            reviewLogRepository.save(log);
        }

        return ResponseEntity.ok("已建立 " + toInsert.size() + " 筆複習單字（set: " + setName + "）");
    }
    
    
}
