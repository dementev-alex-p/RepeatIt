package com.github.dementev_alex_p.repeatit.cards;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CardService {
    private final Map<Long, Card> cards = new HashMap<>();
    private final AtomicLong id = new AtomicLong();

    public Card finaCardById(Long id) {
        return cards.get(id);
    }

    public Card createCard(long userId) {
        final Card card = new Card(id.incrementAndGet(), userId);
        cards.put(card.getId(), card);
        return card;
    }

    public Card updateCard(Card card) {
        cards.put(card.getId(), card);
        return card;
    }

    public List<Card> findByUserId(long userId) {
        return cards.values().stream().filter(card -> card.getUserId() == userId).collect(Collectors.toList());
    }

    @PostConstruct
    public void init() {
        Map<String, String> topCountries = Map.ofEntries(
                Map.entry("Франция", "Париж"),
                Map.entry("Испания", "Мадрид"),
                Map.entry("США", "Вашингтон"),
                Map.entry("Китай", "Пекин"),
                Map.entry("Италия", "Рим"),
                Map.entry("Турция", "Анкара"),
                Map.entry("Мексика", "Мехико"),
                Map.entry("Германия", "Берлин"),
                Map.entry("Таиланд", "Бангкок"),
                Map.entry("Великобритания", "Лондон"),
                Map.entry("Греция", "Афины"),
                Map.entry("Япония", "Токио"),
                Map.entry("Португалия", "Лиссабон"),
                Map.entry("Вьетнам", "Ханой"),
                Map.entry("ОАЭ", "Абу-Даби"),
                Map.entry("Индия", "Нью-Дели"),
                Map.entry("Хорватия", "Загреб"),
                Map.entry("Индонезия", "Джакарта"),
                Map.entry("Марокко", "Рабат"),
                Map.entry("Бразилия", "Бразилиа")
        );
        cards.putAll(topCountries
                .entrySet()
                .stream()
                .map(e ->  new Card(id.incrementAndGet(), 450807973, e.getKey(), e.getValue()))
                .collect(Collectors.toMap(Card::getId, Function.identity()))
        );
    }
}
