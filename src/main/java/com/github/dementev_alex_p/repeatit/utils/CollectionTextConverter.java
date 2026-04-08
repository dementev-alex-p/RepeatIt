package com.github.dementev_alex_p.repeatit.utils;

import com.github.dementev_alex_p.repeatit.collections.CardCollection;

public class CollectionTextConverter {

    private static final String VIEW_TEXT = """
            <strong>Название</strong>: %s
            <strong>Описание</strong> : %s
            """;
    private static final String WITHOUT_DESCRIPTION_TEXT = """
            <strong>Название</strong>: %s
            """;

    public static String convert(final CardCollection cardCollection) {
        return cardCollection.getDescription() == null
                ? String.format(WITHOUT_DESCRIPTION_TEXT, escapeForHtml(cardCollection.getName()))
                : String.format(VIEW_TEXT, escapeForHtml(cardCollection.getName()), escapeForHtml(cardCollection.getDescription()));
    }


    public static String escapeForHtml(String text) {
        return text
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

}
