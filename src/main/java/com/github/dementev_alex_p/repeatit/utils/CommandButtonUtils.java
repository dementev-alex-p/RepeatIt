package com.github.dementev_alex_p.repeatit.utils;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.CommandButton;

public class CommandButtonUtils {

    public static final String ACTION_PARAMETER_TEXT = "action";

    public static CommandParameter createCardIdParameter(final long cardId) {
        return new CommandParameter("card_id", String.valueOf(cardId));
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
}
