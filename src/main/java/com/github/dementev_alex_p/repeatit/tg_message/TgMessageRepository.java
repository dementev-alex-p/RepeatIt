package com.github.dementev_alex_p.repeatit.tg_message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
interface TgMessageRepository extends JpaRepository<TgMessage, Long> {
    @Query("SELECT m FROM TgMessage m WHERE m.userId = :userId AND m.deletedAt is NULL ORDER BY m.createdAt DESC LIMIT 1")
    Optional<TgMessage> findLastAndNotDeletedByUserId(final long userId);

    @Query("SELECT m FROM TgMessage m WHERE m.userId = :userId AND m.createdAt > :time AND m.deletedAt IS NULL ORDER BY m.createdAt DESC LIMIT 1")
    Optional<TgMessage> findLastByUserIdAndCreatedAtLessThanAndNotDeleted(final long userId, final LocalDateTime time);

    @Query("SELECT m FROM TgMessage m WHERE m.userId = :userId AND m.createdAt > :time AND m.deletedAt IS NULL ORDER BY m.createdAt DESC LIMIT 1 OFFSET 1")
    Optional<TgMessage> findSecondToLastByUserIdAndCreatedAtLessThanAndNotDeleted(final long userId, final LocalDateTime time);

    @Query("SELECT m FROM TgMessage m WHERE m.chatId = :chatId AND m.deletedAt is NULL AND m.createdAt > :time ORDER BY m.createdAt ASC")
    List<TgMessage> findNotDeletedAndCreatedBeforeByChatId(final long chatId, final LocalDateTime time);

    @Query("SELECT m FROM TgMessage m WHERE m.userId = :userId AND m.deletedAt is NULL AND m.createdAt > :time ORDER BY m.createdAt ASC")
    List<TgMessage> findNotDeletedByUserIdAndCreatedBefore(final long userId, final LocalDateTime time);

    List<TgMessage> findByTgMessageIdInAndDeletedAtIsNull(final List<Integer> messageIds);
}
