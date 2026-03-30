package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;


public class ViewCardButton extends CommandButton {
    public ViewCardButton(final long cardId) {
        super(
                CommandEnum.VIEW_CARD,
                CommandEnum.VIEW_CARD.getDescription(),
                CommandParameterUtils.createCardIdParameter(cardId)
        );
    }

    public ViewCardButton(final int number, final long cardId) {
        super(
                CommandEnum.VIEW_CARD,
                String.valueOf(number),
                CommandParameterUtils.createCardIdParameter(cardId)
        );
    }
}
