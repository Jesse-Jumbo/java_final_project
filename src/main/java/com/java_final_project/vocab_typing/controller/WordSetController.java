package com.java_final_project.vocab_typing.controller;

import com.java_final_project.vocab_typing.entity.WordSet;
import com.java_final_project.vocab_typing.repository.WordSetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/wordsets")
public class WordSetController {

    private final WordSetRepository wordSetRepository;

    public WordSetController(WordSetRepository wordSetRepository) {
        this.wordSetRepository = wordSetRepository;
    }

    // 🔹 建立新的單字集
    @PostMapping
    public ResponseEntity<WordSet> createWordSet(@RequestBody WordSet wordSet) {
        return ResponseEntity.ok(wordSetRepository.save(wordSet));
    }

    // 🔹 查詢全部單字集
    @GetMapping
    public ResponseEntity<List<WordSet>> getAllWordSets() {
        return ResponseEntity.ok(wordSetRepository.findAll());
    }

    // 🔹 根據 ID 查詢單字集
    @GetMapping("/{id}")
    public ResponseEntity<WordSet> getWordSetById(@PathVariable Long id) {
        Optional<WordSet> wordSet = wordSetRepository.findById(id);
        return wordSet.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 查詢特定使用者的所有單字集
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WordSet>> getWordSetsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(wordSetRepository.findByUserId(userId));
    }

    // 🔹 刪除單字集
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWordSet(@PathVariable Long id) {
        if (wordSetRepository.existsById(id)) {
            wordSetRepository.deleteById(id);
            return ResponseEntity.ok("已刪除單字集：" + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
