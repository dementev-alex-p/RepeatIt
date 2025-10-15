package com.github.dementev_alex_p.repeatit.cards;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByUserId(Long userId);

    @Query("SELECT c FROM Card c WHERE c.userId = :userId ORDER BY c.nextRepeatDate LIMIT 30")
    List<Card> findCardsForTraining(long userId);
}
