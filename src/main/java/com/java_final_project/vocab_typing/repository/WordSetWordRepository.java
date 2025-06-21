package com.java_final_project.vocab_typing.repository;

import com.java_final_project.vocab_typing.entity.WordSetWord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface WordSetWordRepository extends JpaRepository<WordSetWord, Long> {
    List<WordSetWord> findByWordSetId(Long wordSetId);
    List<WordSetWord> findByWordId(Long wordId);
    List<WordSetWord> findByWordSetSetName(String setName);

    // 使用 JPQL 查詢符合使用者和 set 名稱的單字 ID

    @Query("SELECT w.word.id FROM WordSetWord w " +
    	       "WHERE w.wordSet.setName = :setName AND (w.wordSet.user.id = :userId OR w.wordSet.user.id = 'user0001')")
    	List<Long> findWordIdsBySystemOrUser(@Param("userId") String userId, @Param("setName") String setName);

}
