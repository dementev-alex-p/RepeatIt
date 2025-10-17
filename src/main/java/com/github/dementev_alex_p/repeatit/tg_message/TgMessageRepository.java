package com.github.dementev_alex_p.repeatit.tg_message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TgMessageRepository extends JpaRepository<TgMessage, Long> {

}
