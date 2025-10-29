package com.github.dementev_alex_p.repeatit.tg_message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface TgMessageRepository extends JpaRepository<TgMessage, Long> {
    @Query("SELECT m FROM TgMessage m WHERE m.userId = :userId ORDER BY m.createdAt DESC LIMIT 1")
    Optional<TgMessage> findLastByUserId(long userId);
}
