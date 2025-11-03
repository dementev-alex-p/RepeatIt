package com.github.dementev_alex_p.repeatit.cards;

import com.github.dementev_alex_p.repeatit.training.trainig_cards.RecallScoreEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Card findCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow();
    }

    public Card createCard(long userId, String message) {
        return cardRepository.save(new Card(userId, message, CardStatus.DRAFT));
    }

    public Card complitCreationCard(final Card card, final String backSide) {
        card.setBackSide(backSide);
        card.setStatus(CardStatus.READY);
        return cardRepository.save(card);
    }

    public Card updateContent(final Card card, final String frontSide, final String backSide) {
        card.setFrontSide(frontSide);
        card.setBackSide(backSide);
        card.setStatus(CardStatus.READY);
        return cardRepository.save(card);
    }

    public void updateStatus(final Card card, final CardStatus status) {
        card.setStatus(status);
        cardRepository.save(card);
    }

    public void forkCards(final List<Card> cards, final long userId, final long collectionId) {
        final List<Card> cardsForSave = cards
                .stream()
                .map(card -> new Card(
                        card.getFrontSide(),
                        card.getBackSide(),
                        userId,
                        collectionId,
                        CardStatus.READY
                )).toList();
        cardRepository.saveAll(cardsForSave);
    }

    public List<Card> findCardsForExtraTraining(long userId) {
        return cardRepository.findCardsForExtraTraining(userId, 30);
    }

    public void recalcParameters(final long cardId, final RecallScoreEnum newRecallScore) {
        final Card card = cardRepository.findById(cardId).orElseThrow();

        final float easinessFactor = calcNewEasinessFactor(card.getEasinessFactor(), newRecallScore.getVeight());
        final int streak = calcStreak(card.getStreak(), newRecallScore.getVeight());
        final int interval = calcInterval(card.getIntervalDays(), easinessFactor, streak);
        final LocalDate nextRepeatDate = LocalDate.now().plusDays(interval);

        card.setEasinessFactor(easinessFactor);
        card.setStreak(streak);
        card.setIntervalDays(interval);
        card.setNextRepeatDate(nextRepeatDate);
        cardRepository.save(card);
    }

    private int calcInterval(final int intervalDays, final float easinessFactor, final int streak) {
        return switch (streak) {
            case 0 -> 1;
            case 1 -> 3;
            case 2 -> 6;
            default -> Math.round(intervalDays * easinessFactor);
        };
    }

    private int calcStreak(final int streak, final int score) {
        return score >= 3 ? streak + 1 : 0;
    }

    private float calcNewEasinessFactor(float ef, int score) {
        final float easinessFactor = ef + (0.1f - (5 - score) * (0.08f + (5 - score) * 0.02f));
        return easinessFactor < 1.3 ? 1.3f : easinessFactor;
    }

    public Optional<Card> findDraftCardByUserId(final long userId) {
        final List<Card> cards = cardRepository.findByUserIdAndStatusIn(
                userId, Collections.singletonList(CardStatus.DRAFT)
        );
        if (cards.size() > 1) {
            throw new RuntimeException("Черновиков не может быть несколько");
        }
        return cards.isEmpty() ? Optional.empty() : Optional.of(cards.get(0));
    }

    public Optional<Card> findEditingCardByUserId(final long userId) {
        final List<Card> cards = cardRepository.findByUserIdAndStatusIn(
                userId, Arrays.asList(CardStatus.EDITING_FRONT_SIDE, CardStatus.EDITING_BACK_SIDE)
        );
        if (cards.size() > 1) {
            throw new RuntimeException("Редактируемых карточек не может быть несколько");
        }
        return Optional.ofNullable(cards.get(0));
    }

    public int findCardCountForUserId(final long userId) {
        return cardRepository.countCardByUserId(userId);
    }

    public int findCountForDailyTrainingByUserId(final long userId) {
        return cardRepository.findCountForDailyTrainingByUserId(userId);
    }

    public List<Card> searchCard(final long userId, final String query) {
        return cardRepository.searchCards(
                userId,
                "%" + query.trim().toLowerCase() + "%",
                10
        );
    }

    public void softDeleteCardById(final long cardId) {
        final Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карточка уже удалена ранее"));
        card.setStatus(CardStatus.DELETED);
        cardRepository.save(card);
    }

    public List<Card> findCardsForDailyTraining(final long userId) {
        return cardRepository.findCardsForDailyTraining(userId);
    }
}
