package com.java_final_project.vocab_typing.repository;

import com.java_final_project.vocab_typing.entity.PersonalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalConfigRepository extends JpaRepository<PersonalConfig, Long> {
    Optional<PersonalConfig> findByUserId(String userId);

}
