package com.github.dementev_alex_p.repeatit.cards.collection;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardService;
import com.github.dementev_alex_p.repeatit.users.User;
import com.github.dementev_alex_p.repeatit.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardCollectionService {
    private final CardCollectionRepository cardCollectionRepository;
    private final UserService userService;
    private final CardService cardService;

    public List<CardCollection> findAvailableForUser(long userId) {
        return cardCollectionRepository.findAvailableForUser(userId);
    }
    
    public CardCollection findByAuthorId(final Long authorId) {
        return cardCollectionRepository.findByAuthorId(authorId);
    }

    public Optional<CardCollection> findById(long chosenCollectionId) {
        return cardCollectionRepository.findById(chosenCollectionId);
    }


    public void forkCardCollection(CardCollection parentCollection, long userId) {
        final User author = userService.getReferenceById(userId);

        final CardCollection newCardCollection = cardCollectionRepository.save(
                new CardCollection(author,parentCollection.getName(), parentCollection.getId(), false)
        );
        final List<Card> cards = parentCollection
                .getCards()
                .stream()
                .map(c -> new Card(
                        c.getName(),
                        c.getDescription(),
                        userId,
                        newCardCollection.getId()
                ))
                .toList();

        cardService.createCards(cards);
        return newCardCollection;
    }
}
