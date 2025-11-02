package com.github.dementev_alex_p.repeatit.utils;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.CommandParameter;
import com.github.dementev_alex_p.repeatit.commands.handlers.EditionCardCommandHandler;
import com.github.dementev_alex_p.repeatit.commands.result.buttons.CommandButton;

public class CommandButtonUtils {

    public static final String ACTION_PARAMETER_TEXT = "action";

    public static CommandButton createForEditCardAfterCreation(final long cardId) {
        return new CommandButton(
                CommandEnum.EDIT_CARD,
                "Изменить карточку",
                createCardIdParameter(cardId),
                createActionParameter(EditionCardCommandHandler.START_EDITION_ACTION)
        );
    }

    public static CommandButton createForCreationAnotherCard() {
        return new CommandButton(
                CommandEnum.CREATE_CARD,
                "Создать еще",
                CommandEnum.CREATE_CARD.getDefaultParameter()
        );
    }

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
