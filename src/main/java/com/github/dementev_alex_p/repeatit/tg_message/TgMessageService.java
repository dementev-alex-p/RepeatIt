package com.github.dementev_alex_p.repeatit.tg_message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TgMessageService {
    private final TgMessageRepository tgMessageRepository;

    public void save(final TgMessage tgMessage) {
        tgMessageRepository.save(tgMessage);
    }
    public TgMessage findLastByUserId(final long userId) {
        return tgMessageRepository.findLastByUserId(userId).orElseThrow();
    }
}
