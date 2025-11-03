package com.github.dementev_alex_p.repeatit.tg_message;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
interface TgMessageRepository extends JpaRepository<TgMessage, Long> {
    @Query("SELECT m FROM TgMessage m WHERE m.userId = :userId ORDER BY m.createdAt DESC LIMIT 1")
    Optional<TgMessage> findLastByUserId(final long userId);

    @Query("SELECT m FROM TgMessage m WHERE m.userId = :userId AND m.isDeleted = false AND m.createdAt > :time ORDER BY m.createdAt ASC")
    List<TgMessage> findNotDeletedAndCreatedBeforeByUserId(final long userId, final LocalDateTime time);

    @Query("SELECT m FROM TgMessage m WHERE m.userId = :userId AND m.command = :command AND m.isDeleted = false AND m.createdAt > :time")
    List<TgMessage> findNotDeletedByUserIdAndCommandAndCreatedBefore(final long userId, final CommandEnum command, final LocalDateTime time);
}
