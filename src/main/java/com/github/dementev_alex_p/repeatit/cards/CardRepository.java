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
        WHERE c.userId = :userId AND c.deletedAt IS NULL
        ORDER BY c.nextRepeatDate LIMIT :limit
        """)
    List<Card> findCardsForExtraTraining(long userId, final int limit);

    @Query("""
        SELECT count(c) FROM Card c
            WHERE c.userId = :userId
            AND c.nextRepeatDate <= CURRENT_DATE
            AND c.deletedAt IS NULL
        """)
    int findCountForDailyTrainingByUserId(final long userId);

    @Query("""
        SELECT c FROM Card c
            WHERE c.userId = :userId
            AND c.nextRepeatDate <= CURRENT_DATE
            AND c.deletedAt IS NULL
        """)
    List<Card> findCardsForDailyTraining(long userId);

    @Query("""
        SELECT COUNT(c) FROM Card c WHERE c.userId = :userId AND c.deletedAt is null
        """)
    int countNotDeletedCardsByUserId(long userId);

    @Query(""" 
            SELECT c FROM Card c WHERE c.userId = :userId AND
            (LOWER(c.frontSide) LIKE LOWER(:searchQuery) OR
            LOWER(c.backSide) LIKE LOWER(:searchQuery))
            AND c.deletedAt IS NULL
            ORDER BY c.updatedAt DESC
            LIMIT :limit
            """)
    List<Card> searchCards(long userId, String searchQuery, final int limit);

    @Query("""
           SELECT c FROM Card c WHERE c.cardCollection.id = :collectionId AND c.deletedAt IS NULL ORDER BY c.createdAt LIMIT :limit OFFSET :offset
           """)
    List<Card> findByCardCollectionId(final long collectionId, final int limit, final int offset);

    @Query("""
            SELECT count(c) FROM Card c WHERE c.cardCollection.id = :collectionId AND c.deletedAt IS NULL
            """)
    Integer findCountByCardCollectionId(final long collectionId);

    @Modifying
    @Query("""
            UPDATE Card c SET c.deletedAt = CURRENT TIMESTAMP  , c.updatedAt = CURRENT_TIMESTAMP WHERE c.cardCollection.id = :collectionId
            """)
    void softDeleteCardsByCollectionId(final long collectionId);
}
