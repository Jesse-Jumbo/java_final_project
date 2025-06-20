package com.java_final_project.vocab_typing.repository;

import com.java_final_project.vocab_typing.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
}