package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class DeleteCardBackSideButton extends CommandButton {
    public DeleteCardBackSideButton(final long cardId) {
        super(
                CommandEnum.DELETE_CARD_BACK_SIDE,
                CommandEnum.DELETE_CARD_BACK_SIDE.getDescription(),
                CommandParameterUtils.createCardIdParameter(cardId)
        );
    }
}
