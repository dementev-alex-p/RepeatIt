package com.github.dementev_alex_p.repeatit.cards;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
