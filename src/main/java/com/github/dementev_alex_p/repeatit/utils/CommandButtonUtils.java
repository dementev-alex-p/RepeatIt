package com.github.dementev_alex_p.repeatit.utils;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.CommandButton;

public class CommandButtonUtils {

    public static final String ACTION_PARAMETER_TEXT = "action";
    public static final String PAGE_PARAMETER_TEXT = "page";
    public static final String CARD_PARAMETER_TEXT = "card_id";
    public static final String COLLECTION_PARAMETER_TEXT = "card_collection_id";

    public static CommandParameter createCardIdParameter(final long cardId) {
        return new CommandParameter(CARD_PARAMETER_TEXT, String.valueOf(cardId));
    }

    public static CommandParameter createCollectionIdParameter(final long collectionId) {
        return new CommandParameter(COLLECTION_PARAMETER_TEXT, String.valueOf(collectionId));
    }

    public static CommandParameter createActionParameter(final String action) {
        return new CommandParameter(ACTION_PARAMETER_TEXT, action);
    }


    public static CommandButton createForDeletionCard(final long cardId) {
        return new CommandButton(
                CommandEnum.DELETE_CARD,
                CommandEnum.DELETE_CARD.getDescription(),
                createCardIdParameter(cardId)
        );
    }

    public static CommandParameter createPageParameter(final int page) {
        return new CommandParameter(PAGE_PARAMETER_TEXT, String.valueOf(page));
    }
}
