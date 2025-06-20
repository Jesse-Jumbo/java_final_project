package com.java_final_project.vocab_typing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class WordRepository_jumbo {
    private static final String FILE_PATH = "src/main/resources/vocab_review.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<WordRecord> allWords;

    public WordRepository_jumbo() {
        load();
    }

    public void load() {
        try {
            allWords = objectMapper.readValue(new File(FILE_PATH), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("讀取 vocab_review.json 失敗", e);
        }
    }

    public void save() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), allWords);
        } catch (Exception e) {
            throw new RuntimeException("儲存 vocab_review.json 失敗", e);
        }
    }

    public List<WordRecord> getTodayWords(String group, int limit, int repeat, boolean includeReviewedToday) {
        String today = LocalDate.now().toString();
        LocalDate now = LocalDate.now();

        List<WordRecord> todayWords = allWords.stream()
                .filter(w -> w.group.equalsIgnoreCase(group))
                .filter(w -> w.status.equals("in-progress"))
                .filter(w -> {
                    if (w.nextDate == null) return false;
                    LocalDate nextDate = LocalDate.parse(w.nextDate);
                    // ✅ 決定是否包含今天以前已複習過的單字
                    if (includeReviewedToday) {
                        return !nextDate.isAfter(now); // 今天或以前
                    } else {
                        boolean notReviewedToday = w.history == null || !w.history.contains(today);
                        return !nextDate.isAfter(now) && notReviewedToday;
                    }
                })
                .limit(limit)
                .collect(Collectors.toList());

        List<WordRecord> repeated = new ArrayList<>();
        for (WordRecord word : todayWords) {
            for (int i = 0; i < repeat; i++) {
                WordRecord clone = new WordRecord();
                clone.word = word.word;
                clone.definition = word.definition;
                clone.group = word.group;
                clone.history = new ArrayList<>(word.history);
                clone.nextDate = word.nextDate;
                clone.status = word.status;
                repeated.add(clone);
            }
        }

        Collections.shuffle(repeated);
        return repeated;
    }


    public void markReviewedToday(WordRecord word) {
        String today = LocalDate.now().toString();
        if (!word.history.contains(today)) {
            word.history.add(today);
        }

        int reviewCount = word.history.size();
        int[] intervals = {2, 4, 8, 15, 30, 30};

        if (reviewCount <= intervals.length) {
            LocalDate next = LocalDate.now().plusDays(intervals[reviewCount - 1]);
            word.nextDate = next.toString();
        } else {
            word.status = "mastered"; // 畢業
            word.nextDate = null;
        }
    }

    public void commitReviewedWords(List<WordRecord> reviewed, boolean writeToFile) {
        String today = LocalDate.now().toString();

        reviewed.stream()
                .map(r -> r.word)
                .distinct()
                .forEach(word -> {
                    allWords.stream()
                            .filter(w -> w.word.equalsIgnoreCase(word))
                            .findFirst()
                            .ifPresent(w -> {
                                if (writeToFile) {
                                    // 正常紀錄：加入 history，推進 nextDate 或變成 mastered
                                    markReviewedToday(w);
                                } else {
                                    // 清除紀錄：將今天移除，並設回今日複習
                                    System.out.println("✅ [復原] " + w.word + " 已還原為今日複習項目");

                                    if (!w.history.isEmpty() && today.equals(w.history.get(w.history.size() - 1))) {
                                        w.history.remove(w.history.size() - 1);
                                        w.nextDate = today;
                                        w.status = "in-progress";
                                        System.out.println("✅ [復原] " + w.word + " 已還原為今日複習項目");
                                    }
                                }
                            });
                });

        save();
    }

    public List<String> getAllGroups() {
        return allWords.stream()
                .map(w -> w.group)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

}

