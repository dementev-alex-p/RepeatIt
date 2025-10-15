package com.github.dementev_alex_p.repeatit.cards;

import com.github.dementev_alex_p.repeatit.training.trainig_cards.RecallScoreEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Card findCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow();
    }

    public Card createCard(long userId) {
        return cardRepository.save(new Card(userId));
    }

    public void updateFrontSideCard(Card card, String frontSide) {
        card.setFrontSide(frontSide);
        cardRepository.save(card);
    }

    public void updateBackSideCard(Card card, String backSide) {
        card.setBackSide(backSide);
        cardRepository.save(card);
    }

    public List<Card> findByUserId(long userId) {
        return cardRepository.findByUserId(userId);
    }

    public void forkCards(final List<Card> cards, final long userId, final long collectionId) {
        final List<Card> cardsForSave = cards
                .stream()
                .map(card -> new Card(
                        card.getFrontSide(),
                        card.getBackSide(),
                        userId,
                        collectionId
                )).toList();
        cardRepository.saveAll(cardsForSave);
    }

    public List<Card> findCardsForTraining(long userId) {
        return cardRepository.findCardsForTraining(userId);
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
        final float easinessFactor =  ef + (0.1f - (5 - score) * (0.08f + (5 - score) * 0.02f));
        return easinessFactor < 1.3 ? 1.3f : easinessFactor;
    }
}
