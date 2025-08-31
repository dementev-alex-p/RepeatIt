package com.github.dementev_alex_p.repeatit.cards;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Card finaCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow();
    }

    public Card createCard(long userId) {
        final Card card = new Card(userId);
        return cardRepository.save(card);
    }

    public Card updateCard(Card card) {
        //todo нужно порифачить, добавить проверку на существование
        return cardRepository.save(card);
    }

    public List<Card> findByUserId(long userId) {
        return cardRepository.findByUserId(userId);
    }


}
