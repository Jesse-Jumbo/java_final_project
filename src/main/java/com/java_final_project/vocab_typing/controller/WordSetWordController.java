package com.java_final_project.vocab_typing.controller;

import com.java_final_project.vocab_typing.entity.WordSetWord;
import com.java_final_project.vocab_typing.repository.WordSetWordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/wordset-words")
public class WordSetWordController {

    private final WordSetWordRepository wordSetWordRepository;

    public WordSetWordController(WordSetWordRepository wordSetWordRepository) {
        this.wordSetWordRepository = wordSetWordRepository;
    }

    // 🔹 把單字加入到某個單字集
    @PostMapping
    public ResponseEntity<WordSetWord> addWordToSet(@RequestBody WordSetWord link) {
        link.setAddedAt(new Timestamp(System.currentTimeMillis())); // 加上加入時間
        return ResponseEntity.ok(wordSetWordRepository.save(link));
    }

    // 🔹 查詢某單字集中的所有單字
    @GetMapping("/by-set/{wordSetId}")
    public ResponseEntity<List<WordSetWord>> getWordsInSet(@PathVariable Long wordSetId) {
        return ResponseEntity.ok(wordSetWordRepository.findByWordSetId(wordSetId));
    }

    // 🔹 查詢某單字出現在那些單字集中
    @GetMapping("/by-word/{wordId}")
    public ResponseEntity<List<WordSetWord>> getSetsContainingWord(@PathVariable Long wordId) {
        return ResponseEntity.ok(wordSetWordRepository.findByWordId(wordId));
    }
}
