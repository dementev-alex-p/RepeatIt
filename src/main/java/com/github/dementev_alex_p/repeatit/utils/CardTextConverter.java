package com.github.dementev_alex_p.repeatit.utils;

import com.github.dementev_alex_p.repeatit.cards.Card;

public class CardTextConverter {

    private static final String CARD_VIEW_TEXT = """
            📘 Обложка: %s
            📖 Содержание : %s
            """;

    private static final String SUSPENSION_POINTS = "...";

    private static final String CARD_VIEW_WITHOUT_BACK_SIDE_TEXT = """
            📘 Обложка: %s
            """;

    public static String convertForCreatingCard(final String frontSide) {
        return frontSide == null
                ? String.format(CARD_VIEW_TEXT, SUSPENSION_POINTS, SUSPENSION_POINTS)
                : String.format(CARD_VIEW_TEXT, frontSide, SUSPENSION_POINTS);
    }

    public static String convertCardToTextForView(final Card card) {
        return card.getBackSide() == null
                ? String.format(CARD_VIEW_WITHOUT_BACK_SIDE_TEXT, card.getFrontSide())
                : String.format(CARD_VIEW_TEXT, card.getFrontSide(), card.getBackSide());
    }


    public static String forTraining(final Card card, final boolean isShowBackSide) {
        return isShowBackSide && card.getBackSide() != null
                ? String.format(CARD_VIEW_TEXT, card.getFrontSide(), card.getBackSide())
                : String.format(CARD_VIEW_TEXT, card.getFrontSide(), "?");
    }

}
