package com.github.dementev_alex_p.repeatit.cards.collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardCollectionRepository extends JpaRepository<CardCollection, Long> {

    CardCollection findByAuthorId(Long authorId);

    @Query(
            """
                SELECT cc FROM CardCollection cc
                WHERE cc.isPublic = true
                AND cc.author.id != :userId
                AND cc.id NOT IN (SELECT cc1.parentCollectionId FROM CardCollection cc1 WHERE cc1.author.id = :userId)
            """
    )
    List<CardCollection> findAvailableForUser(long userId);
}
