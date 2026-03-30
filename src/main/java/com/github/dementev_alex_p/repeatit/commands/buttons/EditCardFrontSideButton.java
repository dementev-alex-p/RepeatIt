package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class EditCardFrontSideButton extends CommandButton {
    public EditCardFrontSideButton(final long cardId) {
        super(
                CommandEnum.EDIT_CARD_FRONT_SIDE,
                CommandEnum.EDIT_CARD_FRONT_SIDE.getDescription(),
                CommandParameterUtils.createCardIdParameter(cardId)
        );
    }
}
