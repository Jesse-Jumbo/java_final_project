package com.java_final_project.vocab_typing.repository;

import com.java_final_project.vocab_typing.entity.ReviewLog;
import com.java_final_project.vocab_typing.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLogRepository extends JpaRepository<ReviewLog, Integer> {

    // 查詢使用者 + set_name 所有 review log
    List<ReviewLog> findByUserIdAndSetName(String userId, String setName);

    // 檢查某使用者是否已經有某單字的 log（避免重複插入）
    boolean existsByUserIdAndWordId(String userId, Long wordId);

    // 查詢今天該出現的題目（next_review_at 到了）
    @Query("SELECT r FROM ReviewLog r WHERE r.user.id = :userId AND r.setName = :setName AND r.nextReviewAt <= :now AND r.reviewCount < r.reviewThreshold")
    List<ReviewLog> findDueReviews(String userId, String setName, LocalDateTime now);

    // 查詢使用者已經有紀錄的單字 wordId（用於過濾）
    @Query("SELECT r.word.id FROM ReviewLog r WHERE r.user.id = :userId AND r.setName = :setName")
    List<Long> findExistingWordIds(String userId, String setName);
    
   
}
