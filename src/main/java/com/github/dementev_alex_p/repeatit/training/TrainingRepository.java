package com.github.dementev_alex_p.repeatit.training;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {
    Optional<Training> findByUserIdAndFinishedAtIsNull(long userId);

    @Modifying
    @Query("UPDATE Training SET finishedAt = :finishedAt WHERE id = :id")
    void initFinishedAtByTrainingId(long id, LocalDateTime finishedAt);
}
