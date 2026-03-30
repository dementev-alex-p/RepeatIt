package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class EditCardBackSideButton extends CommandButton {
    public EditCardBackSideButton(final long cardId) {
        super(
                CommandEnum.EDIT_CARD_BACK_SIDE,
                CommandEnum.EDIT_CARD_BACK_SIDE.getDescription(),
                CommandParameterUtils.createCardIdParameter(cardId)
        );
    }
}
