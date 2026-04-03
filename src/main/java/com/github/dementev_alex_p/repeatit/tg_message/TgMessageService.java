package com.github.dementev_alex_p.repeatit.tg_message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TgMessageService {
    private final TgMessageRepository tgMessageRepository;

    public void create(final TgMessage tgMessage) {
        tgMessageRepository.save(tgMessage);
    }

    public Optional<TgMessage> findLastByUserId(final long userId) {
        return tgMessageRepository.findLastAndNotDeletedByUserId(userId);
    }

    public Optional<TgMessage> findLastEditableByUserId(final long userId) {
        final LocalDateTime tgRestrictionDateTime = LocalDateTime.now().minusHours(48);
        return tgMessageRepository.findLastByUserIdAndCreatedAtLessThanAndNotDeleted(userId, tgRestrictionDateTime);
    }

    public List<TgMessage> findNotDeletedMessagesByUserId(final long userId) {
        final LocalDateTime tgRestrictionDateTime = LocalDateTime.now().minusHours(48);
        return tgMessageRepository.findNotDeletedByUserIdAndCreatedBefore(userId, tgRestrictionDateTime);
    }

    public Optional<TgMessage> findSecondToLastEditableByUserId(final long userId) {
        final LocalDateTime tgRestrictionDateTime = LocalDateTime.now().minusHours(48);
        return tgMessageRepository.findSecondToLastByUserIdAndCreatedAtLessThanAndNotDeleted(userId, tgRestrictionDateTime);
    }

    public List<Integer> findTgMessageIdsForDeletion(final long chatId) {
        final LocalDateTime tgRestrictionDateTime = LocalDateTime.now().minusHours(48);
        return tgMessageRepository.findNotDeletedAndCreatedBeforeByChatId(chatId, tgRestrictionDateTime)
                .stream()
                .map(TgMessage::getTgMessageId)
                .toList();
    }

    public void update(final TgMessage tgMessageToEdit) {
        tgMessageRepository.save(tgMessageToEdit);
    }

    @Transactional
    public void processChangeMessage(final TgMessage oldTgMessage, final TgMessage newTgMessage) {
        softDeleteByTgMessageIds(List.of(oldTgMessage.getTgMessageId()));
        tgMessageRepository.save(newTgMessage);
    }

    @Transactional
    public void softDeleteByTgMessageIds(final List<Integer> messageIds) {
        if (messageIds.isEmpty()) {
            return;
        }
        final List<TgMessage> tgMessages = tgMessageRepository.findByTgMessageIdInAndDeletedAtIsNull(messageIds);
        tgMessages.forEach(message -> message.setDeletedAt(LocalDateTime.now()));
        tgMessageRepository.saveAll(tgMessages);
    }
}
