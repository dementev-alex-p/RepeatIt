package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class DeleteCardButton extends CommandButton {
    public DeleteCardButton(final long cardId) {
        super(
                CommandEnum.DELETE_CARD,
                CommandEnum.DELETE_CARD.getDescription(),
                CommandParameterUtils.createCardIdParameter(cardId)
        );
    }
}
