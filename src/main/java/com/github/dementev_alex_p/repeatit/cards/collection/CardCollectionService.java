package com.github.dementev_alex_p.repeatit.cards.collection;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardCollectionService {
    private final CardCollectionRepository cardCollectionRepository;
    private final CardService cardService;

    public List<CardCollection> findPublicAvailableForUser(final long userId, final int limit, final int offset) {
        return cardCollectionRepository.findPublicAvailableForUser(userId, limit, offset);
    }

    public int findCountPublicAvailableForUser(final long userId) {
        return cardCollectionRepository.findCountPublicAvailableForUser(userId);
    }

    public List<CardCollection> findByAuthorId(final Long authorId, final int limit, final int offset) {
        return cardCollectionRepository.findByAuthorId(authorId, limit, offset);
    }

    public int findCountByAuthorId(final Long authorId) {
        return cardCollectionRepository.findCountByAuthorId(authorId);
    }

    public Optional<CardCollection> findById(long chosenCollectionId) {
        return cardCollectionRepository.findById(chosenCollectionId);
    }

    @Transactional
    public CardCollection forkCardCollection(CardCollection parentCollection, long userId) {
        final CardCollection newCardCollection = cardCollectionRepository.save(
                new CardCollection(
                        userId,
                        parentCollection.getName(),
                        parentCollection.getId(),
                        false
                )
        );

        cardService.forkCards(parentCollection.getCards(), userId, newCardCollection);
        return newCardCollection;
    }

    @Transactional
    public void softDeleteById(final long collectionId) {
        final CardCollection collection = cardCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));
        cardService.softDeleteCardsByCollectionId(collectionId);
        collection.setDeleted(true);
        cardCollectionRepository.save(collection);

    }

    @Transactional
    public void updateTitleByCollectionId(final long collectionId, final String title) {
        final CardCollection collection = cardCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));
        collection.setName(title);
        cardCollectionRepository.save(collection);
    }

    public List<CardCollection> searchCollection(final long userId, final String query) {

        return cardCollectionRepository.searchCollections(
                userId,
                "%" + query.trim().toLowerCase() + "%",
                10
        );

    }
}
