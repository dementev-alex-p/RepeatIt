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
    public Optional<TgMessage> findLastAvailableByUserId(final long userId) {
        final LocalDateTime tgRestrictionDateTime = LocalDateTime.now().minusHours(48);
        return tgMessageRepository.findLastByUserId(userId, tgRestrictionDateTime);
    }

    public List<TgMessage> findNotDeletedByUserIdAndCommand(final long userId, final CommandEnum command) {
        final LocalDateTime tgRestrictionDateTime = LocalDateTime.now().minusHours(48);
        return tgMessageRepository.findNotDeletedByUserIdAndCommandAndCreatedBefore(userId, command, tgRestrictionDateTime);
    }

    public List<Integer> findMessageIdsForDeletion(final long userId) {
        final LocalDateTime tgRestrictionDateTime = LocalDateTime.now().minusHours(48);
        return tgMessageRepository.findNotDeletedAndCreatedBeforeByUserId(userId, tgRestrictionDateTime)
                .stream()
                .map(TgMessage::getTgMessageId)
                .toList();
    }

    public void update(final TgMessage messageToEdit) {
        tgMessageRepository.save(messageToEdit);
    }

    public void softDeleteMessageByIdIfRequired(final int messageId) {
        final Optional<TgMessage> message = tgMessageRepository.findById((long) messageId);
        if (message.isEmpty()) {
            return;
        }
        final TgMessage messageToDelete = message.get();
        messageToDelete.setDeleted(true);
        tgMessageRepository.save(messageToDelete);
    }

    public void update(final int messageId, final String text, final CommandEnum command, final boolean answerExcepted) {
        final TgMessage tgMessage = tgMessageRepository.findById((long) messageId).orElseThrow();
        tgMessage.setCommand(command);
        tgMessage.setMessageText(text);
        tgMessage.setAnswerExcepted(answerExcepted);
        tgMessageRepository.save(tgMessage);
    }
}
