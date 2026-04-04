package com.github.dementev_alex_p.repeatit.cards.collection;

import com.github.dementev_alex_p.repeatit.cards.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    public CardCollection findById(long collectionId) {
        return cardCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Коллекция не найдена"));
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
        final CardCollection collection = findById(collectionId);
        cardService.softDeleteCardsByCollectionId(collectionId);
        collection.setDeletedAt(LocalDateTime.now());
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
