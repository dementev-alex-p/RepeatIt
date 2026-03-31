package com.github.dementev_alex_p.repeatit.training.trainig_cards;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class TrainingCardService {

    private final TrainingCardRepository trainingCardRepository;
    private final CardService cardService;

    public void scoreRecall(final TrainingCard trainingCard, final RecallScoreEnum recallScore) {
        trainingCard.setRecallScore(recallScore);
        trainingCard.setReviewedAt(LocalDateTime.now());
        trainingCardRepository.save(trainingCard);
        cardService.recalcParameters(trainingCard.getCardId(), recallScore);
    }

    public List<TrainingCard> createCardsForTraining(final List<Card> cards, final long trainingId) {
        final AtomicInteger counter = new AtomicInteger();
        final List<TrainingCard> trainingCards = cards
                .stream()
                .map(card -> new TrainingCard(
                        card.getId(),
                        trainingId,
                        counter.incrementAndGet()
                )).toList();
        trainingCardRepository.saveAll(trainingCards);
        return trainingCards;
    }

    public void delete(final TrainingCard trainingCard) {
        trainingCardRepository.delete(trainingCard);
    }

    public void incrementOrderIndexForCards(final List<TrainingCard> cards) {
        cards.forEach(card -> card.setOrderIndex(card.getOrderIndex() - 1));
        trainingCardRepository.saveAll(cards);

    }
}
