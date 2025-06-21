package com.java_final_project.vocab_typing.controller;

import com.java_final_project.vocab_typing.entity.User;
import com.java_final_project.vocab_typing.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 建立使用者
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userRepository.save(user));
    }

    // 查詢全部使用者
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 根據 name 查詢使用者
    @GetMapping("/name/{name}")
    public ResponseEntity<User> getUserByName(@PathVariable String name) {
        Optional<User> user = userRepository.findByName(name);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 刪除使用者
    @DeleteMapping("/name/{name}")
    public ResponseEntity<String> deleteUserByName(@PathVariable String name) {
        Optional<User> userOpt = userRepository.findByName(name);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
            return ResponseEntity.ok("已刪除使用者：" + name);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
