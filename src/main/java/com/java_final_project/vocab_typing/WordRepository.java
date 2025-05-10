package com.java_final_project.vocab_typing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WordRepository {
    private static final String FILE_PATH = "src/main/resources/vocab_review.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<WordRecord> allWords;

    public WordRepository() {
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

    public List<WordRecord> getTodayWords(int limit, int requiredTimesPerWord) {
        String today = LocalDate.now().toString();
        List<WordRecord> repeated = allWords.stream()
                .filter(w -> w.status.equals("in-progress") && w.nextDates.contains(today))
                .flatMap(w -> java.util.stream.IntStream.range(0, requiredTimesPerWord).mapToObj(i -> {
                    WordRecord clone = new WordRecord();
                    clone.word = w.word;
                    clone.definition = w.definition;
                    clone.history = w.history;
                    clone.nextDates = w.nextDates;
                    clone.status = w.status;
                    return clone;
                }))
                .collect(Collectors.toList());

        // 打亂題目順序
        Collections.shuffle(repeated);

        // 限制總數
        return repeated.stream().limit(limit * requiredTimesPerWord).collect(Collectors.toList());
    }


    public void markReviewedToday(WordRecord word) {
        String today = LocalDate.now().toString();
        if (!word.history.contains(today)) {
            word.history.add(today);
        }

        int reviewCount = word.history.size();
        int[] intervals = {2, 4, 8, 15, 30};
        if (reviewCount <= intervals.length) {
            LocalDate nextDay = LocalDate.now().plusDays(intervals[reviewCount - 1]);
            word.nextDates.add(nextDay.toString());
        } else {
            word.status = "mastered";
        }
    }

    public void commitReviewedWords(List<WordRecord> reviewed) {
        reviewed.stream()
                .map(r -> r.word) // 過濾重複單字
                .distinct()
                .forEach(word -> {
                    allWords.stream()
                            .filter(w -> w.word.equalsIgnoreCase(word))
                            .findFirst()
                            .ifPresent(this::markReviewedToday);
                });
        save();
    }
}

