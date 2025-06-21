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

    @GetMapping("/today")
    public ResponseEntity<List<Map<String, Object>>> getTodayWordsToReview(
            @RequestParam String userName,
            @RequestParam String setName) {

        // 1. 根據 userName 查找使用者
        Optional<User> userOpt = userRepository.findByName(userName);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        User user = userOpt.get();
        String userId = user.getId();

        // 2. 查找使用者的 daily limit 設定
        Optional<PersonalConfig> configOpt = personalConfigRepository.findByUserId(userId);
        if (configOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        int dailyWordLimit = configOpt.get().getDailyWordLimit();

        // 3. 今天日期（不含時間）
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        // 4. 撈出今天要複習的資料（nextReviewAt < 今天）
        List<ReviewLog> dueList = reviewLogRepository.findDueReviews(userId, setName, today);

        // 5. 組成回傳格式（加上 reviewCount 與 reviewThreshold）
        List<Map<String, Object>> response = dueList.stream()
                .sorted(Comparator.comparing(ReviewLog::getNextReviewAt))
                .limit(dailyWordLimit)
                .map(log -> {
                    Map<String, Object> wordInfo = new HashMap<>();
                    wordInfo.put("id", log.getWord().getId());
                    wordInfo.put("word", log.getWord().getWord());
                    wordInfo.put("definition", log.getWord().getDefinition());
                    wordInfo.put("reviewCount", log.getReviewCount());
                    wordInfo.put("reviewThreshold", log.getReviewThreshold());
                    return wordInfo;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateOrInsertReviewLogs(
            @RequestParam String name,  // 使用者名稱
            @RequestBody List<Map<String, Object>> reviewDataList) {

        // 找出 user（使用者名稱轉 userId）
        Optional<User> userOpt = userRepository.findByName(name);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 找不到使用者：" + name);
        }
        User user = userOpt.get();
        String userId = user.getId();

        // 找使用者的 config（若沒有 fallback 到 user0001）
        Optional<PersonalConfig> configOpt = personalConfigRepository.findByUserId(userId);
        if (configOpt.isEmpty()) {
            configOpt = personalConfigRepository.findByUserId("user0001");
            if (configOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ 找不到使用者設定，也沒有預設 user0001 設定");
            }
        }
        PersonalConfig config = configOpt.get();
        int reviewThreshold = config.getReviewThreshold();

        int successCount = 0;

        for (Map<String, Object> item : reviewDataList) {
            try {
                String setName = (String) item.get("setName");
                String wordText = (String) item.get("word");
                String definition = (String) item.get("definition");
                String nextReviewAtStr = (String) item.get("nextReviewAt");
                String lastReviewAtStr = (String) item.get("lastReviewAt");

                // 轉換時間
                LocalDateTime nextReviewAt = LocalDateTime.parse(nextReviewAtStr);
                LocalDateTime lastReviewAt = LocalDateTime.parse(lastReviewAtStr);

                // 找 word
                Optional<Word> wordOpt = wordRepository.findByWordAndDefinition(wordText, definition);
                if (wordOpt.isEmpty()) continue;
                Word word = wordOpt.get();

                // 查是否已有 review log
                Optional<ReviewLog> logOpt = reviewLogRepository.findByUserIdAndSetNameAndWordId(userId, setName, word.getId());

                if (logOpt.isPresent()) {
                    // 更新 log
                    ReviewLog log = logOpt.get();
                    log.setReviewCount(log.getReviewCount() + 1);
                    log.setLastReviewAt(lastReviewAt);
                    log.setNextReviewAt(nextReviewAt);
                    if (log.getReviewCount() >= log.getReviewThreshold()) {
                        log.setStatus("FINISHED");
                    }
                    reviewLogRepository.save(log);
                } else {
                    // 新增 log
                    ReviewLog newLog = new ReviewLog();
                    newLog.setUser(user);
                    newLog.setWord(word);
                    newLog.setSetName(setName);
                    newLog.setReviewCount(1);
                    newLog.setReviewThreshold(reviewThreshold);
                    newLog.setLastReviewAt(lastReviewAt);
                    newLog.setNextReviewAt(nextReviewAt);
                    newLog.setStatus("IN_PROGRESS");
                    reviewLogRepository.save(newLog);
                }

                successCount++;

            } catch (Exception e) {
                // 可以加上 log.error(...) 若你需要
                continue;
            }
        }

        return ResponseEntity.ok("✅ 成功處理 " + successCount + " 筆複習紀錄");
    }

    @GetMapping("/unplayed")
    public ResponseEntity<?> getUnplayedWords(@RequestParam String userName) {
        Optional<User> userOpt = userRepository.findByName(userName);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("找不到使用者: " + userName);
        }
        String userId = userOpt.get().getId();

        // 1. 找出該使用者的所有 setName（從 review_log 取得）
        List<String> reviewedSets = reviewLogRepository.findAllSetNamesByUserId(userId);

        List<Map<String, String>> result = new ArrayList<>();

        for (String setName : reviewedSets) {
            // 2. 找出該單字集所有單字 ID
            List<Long> allWordIds = wordSetWordRepository.findWordIdsBySystemOrUser(userId, setName);

            // 3. 找出該使用者已複習過的 wordId
            List<Long> reviewedWordIds = reviewLogRepository.findExistingWordIds(userId, setName);

            // 4. 過濾尚未進入 review log 的 wordId
            List<Long> unplayedIds = allWordIds.stream()
                    .filter(id -> !reviewedWordIds.contains(id))
                    .collect(Collectors.toList());

            // 5. 查出 word 資訊並組合結果
            for (Long id : unplayedIds) {
                Optional<Word> wordOpt = wordRepository.findById(id);
                wordOpt.ifPresent(word -> {
                    Map<String, String> entry = new HashMap<>();
                    entry.put("setName", setName);
                    entry.put("word", word.getWord());
                    entry.put("definition", word.getDefinition());
                    result.add(entry);
                });
            }
        }

        return ResponseEntity.ok(result);
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
