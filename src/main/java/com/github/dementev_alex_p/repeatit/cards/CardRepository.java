package com.github.dementev_alex_p.repeatit.cards;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("""
        SELECT c FROM Card c
        WHERE c.userId = :userId
        ORDER BY c.updatedAt LIMIT :limit OFFSET :offset
    """)
    List<Card> findByUserId(final long userId, final int limit, final int offset);

    @Query("""
        SELECT c FROM Card c
        WHERE c.userId = :userId AND c.status = 'READY'
        ORDER BY c.nextRepeatDate LIMIT :limit
        """)
    List<Card> findCardsForExtraTraining(long userId, final int limit);

    @Query("""
        SELECT count(c) FROM Card c
            WHERE c.userId = :userId
            AND c.nextRepeatDate <= CURRENT_DATE
            AND c.status = 'READY'
        """)
    int findCountForDailyTrainingByUserId(final long userId);

    @Query("""
        SELECT c FROM Card c
            WHERE c.userId = :userId
            AND c.nextRepeatDate <= CURRENT_DATE
            AND c.status = 'READY'
        """)
    List<Card> findCardsForDailyTraining(long userId);

    List<Card> findByUserIdAndStatusIn(final long userId, final List<CardStatus> statuses);

    int countCardByUserId(long userId);

    @Query(""" 
            SELECT c FROM Card c WHERE c.userId = :userId AND
            (LOWER(c.frontSide) LIKE LOWER(:searchQuery) OR
            LOWER(c.backSide) LIKE LOWER(:searchQuery))
            AND c.status = 'READY'
            ORDER BY c.updatedAt DESC
            LIMIT :limit
            """)
    List<Card> searchCards(long userId, String searchQuery, final int limit);

    @Query("""
           SELECT c FROM Card c WHERE c.cardCollectionId = :collectionId ORDER BY c.createdAt LIMIT :limit OFFSET :offset
           """)
    List<Card> findByCardCollectionId(final long collectionId, final int limit, final int offset);

    @Query("""
            SELECT count(c) FROM Card c WHERE c.cardCollectionId = :collectionId
            """)
    Integer findCountByCardCollectionId(final long collectionId);

    @Modifying
    @Query("""
            UPDATE Card c SET c.status = 'DELETED', c.updatedAt = CURRENT_TIMESTAMP\s WHERE c.cardCollectionId = :collectionId AND c.status != 'DELETED'
            """)
    void softDeleteCardsByCollectionId(final long collectionId);
}
