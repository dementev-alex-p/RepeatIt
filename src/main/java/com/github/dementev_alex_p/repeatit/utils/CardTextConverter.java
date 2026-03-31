package com.github.dementev_alex_p.repeatit.utils;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.cards.collection.CardCollection;

import java.util.Optional;

public class CardTextConverter {

    private static final String CARD_VIEW_TEXT = """
            📘 <strong>Обложка</strong>: %s
            📖 <strong>Содержание</strong> : %s
            """;

    private static final String CARD_VIEW_FULL_TEXT = """
            📘 <strong>Обложка</strong>: %s
            📖 <strong>Содержание</strong> : %s
            📚 <strong>Коллекция</strong> : %s
            """;

    private static final String CARD_VIEW_FOR_EDITION_TEXT = """
            📘 <strong>Обложка</strong>: <code>%s</code>
            📖 <strong>Содержание</strong> : <code>%s</code>
            📚 <strong>Коллекция</strong> : %s
            """;

    private static final String SUSPENSION_POINTS = "...";
    private static final String HIDE_BACK_SIDE = "❔❔❔";
    private static final String DASH_BACK_SIDE = "➖";

    private static final String CARD_VIEW_WITHOUT_BACK_SIDE_TEXT = """
            📘 <strong>Обложка</strong>: %s
            """;

    public static String convertForCreatingCard(final String frontSide) {
        return frontSide == null
                ? String.format(CARD_VIEW_TEXT, SUSPENSION_POINTS, SUSPENSION_POINTS)
                : String.format(CARD_VIEW_TEXT, escapeForHtml(frontSide), SUSPENSION_POINTS);
    }

    public static String convertCardToTextForView(final Card card) {
        return String.format(
                CARD_VIEW_FULL_TEXT,
                escapeForHtml(card.getFrontSide()),
                Optional.ofNullable(card.getBackSide()).orElse(DASH_BACK_SIDE),
                Optional.ofNullable(card.getCardCollection()).map(CardCollection::getName).orElse(DASH_BACK_SIDE)
        );
    }

    public static String convertCardToTextForEdition(final Card card) {
        return String.format(
                CARD_VIEW_FOR_EDITION_TEXT,
                escapeForHtml(card.getFrontSide()),
                Optional.ofNullable(card.getBackSide()).orElse(DASH_BACK_SIDE),
                Optional.ofNullable(card.getCardCollection()).map(CardCollection::getName).orElse(DASH_BACK_SIDE)
        );
    }


    public static String forTraining(final Card card, final boolean isShowBackSide) {
        return card.getBackSide() == null
                ? String.format(CARD_VIEW_WITHOUT_BACK_SIDE_TEXT, escapeForHtml(card.getFrontSide()))
                : String.format(CARD_VIEW_TEXT, escapeForHtml(card.getFrontSide()), isShowBackSide ? escapeForHtml(card.getBackSide()) : HIDE_BACK_SIDE);
    }

    public static String convertCardToShortText(final Card card) {
        return card.getBackSide() == null
                ? String.format(CARD_VIEW_WITHOUT_BACK_SIDE_TEXT, escapeForHtml(card.getFrontSide()))
                : String.format(CARD_VIEW_TEXT, escapeForHtml(card.getFrontSide()), escapeForHtml(card.getBackSide()));
    }

    public static String escapeForHtml(String text) {
        return text
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

}
