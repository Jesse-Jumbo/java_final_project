package com.java_final_project.vocab_typing.controller;

import com.java_final_project.vocab_typing.entity.PersonalConfig;
import com.java_final_project.vocab_typing.repository.PersonalConfigRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/api/config")
public class PersonalConfigController {

    private final PersonalConfigRepository configRepository;

    public PersonalConfigController(PersonalConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    // ✅ 取得使用者偏好設定
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserConfig(@PathVariable String userId) {
        Optional<PersonalConfig> config = configRepository.findByUserId(userId);
        return config.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ 建立或更新使用者設定
    @PostMapping
    public ResponseEntity<PersonalConfig> saveOrUpdate(@RequestBody PersonalConfig config) {
        config.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.ok(configRepository.save(config));
    }
}
