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
import java.util.stream.IntStream;

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

    public List<WordRecord> getTodayWords(String group, int limit, int repeat) {
        String today = LocalDate.now().toString();
        List<WordRecord> repeated = allWords.stream()
                .filter(w -> w.group.equalsIgnoreCase(group))
                .filter(w -> w.status.equals("in-progress") && today.equals(w.nextDate))
                .flatMap(w -> IntStream.range(0, repeat).mapToObj(i -> {
                    WordRecord clone = new WordRecord();
                    clone.word = w.word;
                    clone.definition = w.definition;
                    clone.group = w.group;
                    clone.history = new ArrayList<>(w.history);
                    clone.nextDate = w.nextDate;
                    clone.status = w.status;
                    return clone;
                }))
                .collect(Collectors.toList());

        Collections.shuffle(repeated);
        return repeated.stream().limit(limit * repeat).collect(Collectors.toList());
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

    public void commitReviewedWords(List<WordRecord> reviewed) {
        reviewed.stream()
                .map(r -> r.word)
                .distinct()
                .forEach(word -> {
                    allWords.stream()
                            .filter(w -> w.word.equalsIgnoreCase(word))
                            .findFirst()
                            .ifPresent(this::markReviewedToday);
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

