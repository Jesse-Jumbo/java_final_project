package com.java_final_project.vocab_typing.controller;

import com.java_final_project.vocab_typing.repository.WordRepository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.java_final_project.vocab_typing.dto.WordImportRequest;
import com.java_final_project.vocab_typing.entity.User;
import com.java_final_project.vocab_typing.entity.Word;
import com.java_final_project.vocab_typing.entity.WordSet;
import com.java_final_project.vocab_typing.entity.WordSetWord;
import com.java_final_project.vocab_typing.repository.UserRepository;
import com.java_final_project.vocab_typing.repository.WordSetRepository;
import com.java_final_project.vocab_typing.repository.WordSetWordRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/words")
public class WordController {

	private final WordRepository wordRepository;
	private final UserRepository userRepository;
	private final WordSetRepository wordSetRepository;
	private final WordSetWordRepository wordSetWordRepository;
	
    public WordController(WordRepository wordRepository,
            UserRepository userRepository,
            WordSetRepository wordSetRepository,
            WordSetWordRepository wordSetWordRepository) {
	this.wordRepository = wordRepository;
	this.userRepository = userRepository;
	this.wordSetRepository = wordSetRepository;
	this.wordSetWordRepository = wordSetWordRepository;
	}

    @PostMapping
    public ResponseEntity<Word> createWord(@RequestBody Word word) {
        return ResponseEntity.ok(wordRepository.save(word));
    }

    @GetMapping
    public ResponseEntity<List<Word>> getAllWords() {
        return ResponseEntity.ok(wordRepository.findAll());
    }
    
    // ✅ 匯入 JSON 陣列
    @PostMapping("/import")
    public ResponseEntity<String> importWords(@RequestBody List<Word> words) {
        wordRepository.saveAll(words);
        return ResponseEntity.ok("成功匯入 " + words.size() + " 筆單字！");
    }
    
    @PostMapping("/import/with-set")
    public ResponseEntity<String> importWordsAndCreateSet(@RequestBody WordImportRequest request) {
        String userId = request.getUserId();
        String setName = request.getSetName();
        List<Word> words = request.getWords();

        // 防呆:查找使用者（放最前面）
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 使用者不存在: " + userId + "，無法匯入單字。");
        }
        User user = userOptional.get();
        
        // 1. 儲存單字
        List<Word> savedWords = wordRepository.saveAll(words);


        // 2. 建立 WordSet
        WordSet wordSet = new WordSet();
        wordSet.setUser(user);
        wordSet.setSetName(setName);
        WordSet savedSet = wordSetRepository.save(wordSet);

        // 3. 建立 WordSetWord 關聯
        // 這裡的 link 是一個變數名稱，它代表的是一筆 WordSetWord 實體物件，也就是資料庫中 word_sets_words 這個「橋接表」中的一行資料
        for (Word word : savedWords) {
            WordSetWord link = new WordSetWord();
            link.setWord(word);
            link.setWordSet(savedSet);
            link.setAddedAt(new Timestamp(System.currentTimeMillis()));
            wordSetWordRepository.save(link);
        }

        return ResponseEntity.ok("成功匯入 " + savedWords.size() + " 筆單字，並建立單字集「" + setName + "」");
    }

}