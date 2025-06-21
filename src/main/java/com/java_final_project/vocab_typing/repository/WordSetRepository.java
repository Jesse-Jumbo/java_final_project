package com.java_final_project.vocab_typing.repository;

import com.java_final_project.vocab_typing.entity.WordSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface WordSetRepository extends JpaRepository<WordSet, Long> {
    List<WordSet> findByUserId(String userId);  // 可根據使用者查詢其所有單字集
    List<WordSet> findByUserName(String name);
    Optional<WordSet> findBySetName(String setName);

}
