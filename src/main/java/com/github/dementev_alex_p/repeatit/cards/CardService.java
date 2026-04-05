package com.github.dementev_alex_p.repeatit.cards;

import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;
import com.github.dementev_alex_p.repeatit.training.trainig_cards.RecallScoreEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Card findCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow();
    }

    public Card createCard(final long userId, final String message, final Optional<CardCollection> collection) {
        return cardRepository.save(new Card(userId, message, collection.orElse(null)));
    }


    @Transactional
    public void updateFrontSideByCardId(final long cardId, final String frontSide) {
        final Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setFrontSide(frontSide);
        cardRepository.save(card);
    }

    @Transactional
    public void updateBackSideByCardId(final long cardId, final String backSide) {
        final Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setBackSide(backSide);
        cardRepository.save(card);
    }


    public void forkCards(final List<Card> cards, final long userId, final CardCollection cardCollection) {
        final List<Card> cardsForSave = cards
                .stream()
                .map(card -> new Card(
                        card.getFrontSide(),
                        card.getBackSide(),
                        userId,
                        cardCollection
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


    public int findCardCountForUserId(final long userId) {
        return cardRepository.countNotDeletedCardsByUserId(userId);
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
    public List<Card> searchCardInCollection(final long userId, final String query, final long collectionId) {
        return cardRepository.searchCardsInCollection(
                userId,
                "%" + query.trim().toLowerCase() + "%",
                collectionId,
                10
        );
    }

    public void softDeleteCardById(final long cardId) {
        final Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Карточка уже удалена ранее"));
        card.setDeletedAt(LocalDateTime.now());
        cardRepository.save(card);
    }

    public List<Card> findCardsForDailyTraining(final long userId) {
        return cardRepository.findCardsForDailyTraining(userId);
    }

    public List<Card> findCardsByCollectionId(final long collectionId, final int limit, final int offset) {
        return cardRepository.findByCardCollectionId(collectionId, limit, offset);
    }

    public Integer findCardCountByCollectionId(final long collectionId) {
        return cardRepository.findCountByCardCollectionId(collectionId);
    }

    public void softDeleteCardsByCollectionId(final long collectionId) {
        cardRepository.softDeleteCardsByCollectionId(collectionId);
    }

    @Transactional
    public void updateCardCollection(final long cardId, final CardCollection collection) {
        final Card card = cardRepository.findById(cardId).orElseThrow();
        card.setCardCollection(collection);
        cardRepository.save(card);
    }
}
