package com.github.dementev_alex_p.repeatit.utils;

import com.github.dementev_alex_p.repeatit.cards.Card;
import com.github.dementev_alex_p.repeatit.collections.CardCollection;

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
                : String.format(CARD_VIEW_TEXT, prepareToView(frontSide), SUSPENSION_POINTS);
    }

    public static String convertForCreatingCardWithCollection(final String frontSide, final CardCollection collection) {
        return frontSide == null
                ? String.format(CARD_VIEW_FULL_TEXT, SUSPENSION_POINTS, SUSPENSION_POINTS, prepareToView(collection.getName()))
                : String.format(CARD_VIEW_FULL_TEXT, prepareToView(frontSide), SUSPENSION_POINTS, prepareToView(collection.getName()));
    }

    public static String convertCardToTextForView(final Card card) {
        return String.format(
                CARD_VIEW_FULL_TEXT,
                prepareToView(card.getFrontSide()),
                Optional.ofNullable(card.getBackSide()).map(CardTextConverter::prepareToView).orElse(DASH_BACK_SIDE),
                Optional.ofNullable(card.getCardCollection()).map(c -> prepareToView(c.getName())).orElse(DASH_BACK_SIDE)
        );
    }

    public static String convertCardToTextForEdition(final Card card) {
        return String.format(
                CARD_VIEW_FOR_EDITION_TEXT,
                prepareToView(card.getFrontSide()),
                Optional.ofNullable(card.getBackSide()).map(CardTextConverter::prepareToView).orElse(DASH_BACK_SIDE),
                Optional.ofNullable(card.getCardCollection()).map(c -> prepareToView(c.getName())).orElse(DASH_BACK_SIDE)
        );
    }


    public static String forTraining(final Card card, final boolean isShowBackSide) {
        return card.getBackSide() == null
                ? String.format(CARD_VIEW_WITHOUT_BACK_SIDE_TEXT, prepareToView(card.getFrontSide()))
                : String.format(CARD_VIEW_TEXT, prepareToView(card.getFrontSide()), isShowBackSide ? prepareToView(card.getBackSide()) : HIDE_BACK_SIDE);
    }

    public static String convertCardToShortText(final Card card) {
        return card.getBackSide() == null
                ? String.format(CARD_VIEW_WITHOUT_BACK_SIDE_TEXT, prepareToView(card.getFrontSide(), 100))
                : String.format(CARD_VIEW_TEXT, prepareToView(card.getFrontSide()), prepareToView(card.getBackSide(), 500));
    }

    public static String prepareToView(final String text) {
        return prepareToView(text, 3500);
    }

    private static String prepareToView(final String text, final int textLength) {
        if (text.trim().startsWith("<a href")) {
            return text;
        }
        final String textWithoutHTML = text
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        if (textWithoutHTML.length() <= textLength) {
            return textWithoutHTML;
        }
        return textWithoutHTML.substring(0, textLength) + "...";
    }

}
