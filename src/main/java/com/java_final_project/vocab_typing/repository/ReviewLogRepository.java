package com.java_final_project.vocab_typing.repository;

import com.java_final_project.vocab_typing.entity.ReviewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // ✅ 這一行你漏掉了
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLogRepository extends JpaRepository<ReviewLog, Integer> {

    // ✅ 查詢某使用者某個單字集的所有記錄
    List<ReviewLog> findByUserIdAndSetName(String userId, String setName);

    // ✅ 查詢某使用者 + 單字集 + 單字是否已有記錄
    Optional<ReviewLog> findByUserIdAndSetNameAndWordId(String userId, String setName, Long wordId);

    @Query("SELECT DISTINCT r.setName FROM ReviewLog r WHERE r.user.id = :userId")
    List<String> findAllSetNamesByUserId(@Param("userId") String userId);

    // ✅ 查詢「今天到期」且尚未完成複習的單字
    @Query("SELECT r FROM ReviewLog r WHERE r.user.id = :userId AND r.setName = :setName AND r.nextReviewAt <= :now AND r.reviewCount < r.reviewThreshold")
    List<ReviewLog> findDueReviews(@Param("userId") String userId,
                                   @Param("setName") String setName,
                                   @Param("now") LocalDateTime now);

    // ✅ 查詢某使用者 + 單字集的所有已記錄 wordId
    @Query("SELECT r.word.id FROM ReviewLog r WHERE r.user.id = :userId AND r.setName = :setName")
    List<Long> findExistingWordIds(@Param("userId") String userId,
                                   @Param("setName") String setName);

    // ✅ 快速檢查某筆 log 是否存在
    boolean existsByUserIdAndWordId(String userId, Long wordId);
}
