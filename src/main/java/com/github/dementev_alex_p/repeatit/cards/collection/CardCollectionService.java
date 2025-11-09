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

    public List<CardCollection> findByAuthorId(final Long authorId, final int limit, final int offset) {
        return cardCollectionRepository.findByAuthorId(authorId, limit, offset);
    }

    public int findCountByAuthorId(final Long authorId) {
        return cardCollectionRepository.findCountByAuthorId(authorId);
    }

    public Optional<CardCollection> findById(long chosenCollectionId) {
        return cardCollectionRepository.findById(chosenCollectionId);
    }


    public void forkCardCollection(CardCollection parentCollection, long userId) {
        final User author = userService.getReferenceById(userId);

        final CardCollection newCardCollection = cardCollectionRepository.save(
                new CardCollection(author.getId(), parentCollection.getName(), parentCollection.getId(), false)
        );

        cardService.forkCards(parentCollection.getCards(), userId, newCardCollection.getId());
    }
}
