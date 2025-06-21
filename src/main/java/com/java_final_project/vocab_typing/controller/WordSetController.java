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

    // 🔹 根據 setName 查詢單字集
    @GetMapping("/set/{setName}")
    public ResponseEntity<WordSet> getWordSetBySetName(@PathVariable String setName) {
        Optional<WordSet> wordSet = wordSetRepository.findBySetName(setName);
        return wordSet.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 查詢特定使用者的所有單字集
    @GetMapping("/user/{name}")
    public ResponseEntity<List<WordSet>> getWordSetsByUserName(@PathVariable String name) {
        return ResponseEntity.ok(wordSetRepository.findByUserName(name));
    }

    // 🔹 根據 setName 刪除單字集
    @DeleteMapping("/set/{setName}")
    public ResponseEntity<String> deleteWordSetBySetName(@PathVariable String setName) {
        Optional<WordSet> wordSetOpt = wordSetRepository.findBySetName(setName);
        if (wordSetOpt.isPresent()) {
            wordSetRepository.delete(wordSetOpt.get());
            return ResponseEntity.ok("已刪除單字集：" + setName);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
