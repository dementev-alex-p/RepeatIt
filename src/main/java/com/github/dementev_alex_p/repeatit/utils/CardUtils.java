package com.github.dementev_alex_p.repeatit.utils;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.CardStatus;

import java.util.Optional;

public class CardUtils {

    private static final String CARD_VIEW_TEXT = """
            Лицевая сторона: %s
            Обратная сторона: %s
            """;

    public static String convertCardToShortText(final Card card) {
        return card.getBackSide() != null
                ? String.format("%s --> %s", card.getFrontSide(), card.getBackSide())
                : card.getFrontSide();
    }

    public static String convertCardToTextForView(final Card card) {
        return String.format(CARD_VIEW_TEXT,
                card.getFrontSide(),
                Optional.ofNullable(card.getBackSide()).orElse(card.getStatus() == CardStatus.DRAFT ? "..." : "-")
        );
    }
}
