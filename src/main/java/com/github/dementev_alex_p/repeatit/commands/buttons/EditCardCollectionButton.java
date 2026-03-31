package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class EditCardCollectionButton extends CommandButton {
    public EditCardCollectionButton(final long cardId) {
        super(
                CommandEnum.EDIT_CARD_COLLECTION,
                CommandEnum.EDIT_CARD_COLLECTION.getDescription(),
                CommandParameterUtils.createCardIdParameter(cardId)
        );
    }
}
