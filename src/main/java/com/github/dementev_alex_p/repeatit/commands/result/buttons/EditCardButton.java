package com.github.dementev_alex_p.repeatit.commands.result.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;

public class EditCardButton extends CommandButton {
    public EditCardButton(final long cardId) {
        super(
                CommandEnum.EDIT_CARD,
                CommandEnum.EDIT_CARD.getDescription(),
                CommandEnum.EDIT_CARD.getDefaultParameter(),
                CommandButtonUtils.createCardIdParameter(cardId)
        );
    }
}
