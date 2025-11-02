package com.github.dementev_alex_p.repeatit.tg_message;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public List<TgMessage> findNotDeletedByUserIdAndCommand(final long userId, final CommandEnum command) {
        return tgMessageRepository.findNotDeletedByUserIdAndCommand(userId, command);
    }

    public List<Integer> findMessageIdsToDelete(final long userId) {
        final LocalDateTime tgRestrictionDateTime = LocalDateTime.now().minusHours(48);
        return tgMessageRepository.findNotDeletedAndCreatedBeforeByUserId(userId, tgRestrictionDateTime)
                .stream()
                .map(TgMessage::getTgMessageId)
                .toList();
    }

    public void update(final TgMessage messageToEdit) {
        tgMessageRepository.save(messageToEdit);
    }

    public void softDeleteMessageById(final int messageId) {
        final Optional<TgMessage> message = tgMessageRepository.findById((long) messageId);
        if (message.isEmpty()) {
            throw new RuntimeException("Не удалось найти карточку по id: " + messageId);
        }
        final TgMessage messageToDelete = message.get();
        messageToDelete.setDeleted(true);
        tgMessageRepository.save(messageToDelete);
    }
}
