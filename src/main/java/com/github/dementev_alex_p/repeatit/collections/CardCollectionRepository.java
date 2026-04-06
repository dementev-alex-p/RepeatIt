package com.github.dementev_alex_p.repeatit.collections;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardCollectionRepository extends JpaRepository<CardCollection, Long> {

    @Query("""
    SELECT c FROM CardCollection c WHERE c.authorId = :authorId ORDER BY c.updatedAt LIMIT :limit OFFSET :offset
    """)
    List<CardCollection> findByAuthorId(final long authorId, final int limit, final int offset);

    @Query("""
    SELECT COUNT(c) FROM CardCollection c WHERE c.authorId = :authorId
    """)
    int findCountByAuthorId(long authorId);

    @Query(
            """
            SELECT cc FROM CardCollection cc
            WHERE cc.isPublic = true
            AND cc.authorId != :userId
            AND cc.id NOT IN (SELECT cc1.parentCollectionId FROM CardCollection cc1 WHERE cc1.authorId = :userId)
            ORDER BY cc.createdAt ASC
            LIMIT :limit
            OFFSET :offset
            """
    )
    List<CardCollection> findPublicAvailableForUser(final long userId, final int limit, final int offset);

    @Query(
            """
            SELECT COUNT(cc) FROM CardCollection cc
            WHERE cc.isPublic = true
            AND cc.authorId != :userId
            AND cc.id NOT IN (SELECT cc1.parentCollectionId FROM CardCollection cc1 WHERE cc1.authorId = :userId)
            """
    )
    int findCountPublicAvailableForUser(final long userId);

    @Query(""" 
            SELECT c FROM CardCollection c WHERE c.authorId = :userId AND
            LOWER(c.name) LIKE LOWER(:searchQuery)
            ORDER BY c.updatedAt DESC
            LIMIT :limit
            """)
    List<CardCollection> searchCollections(final long userId, final String searchQuery, final int limit);
}
