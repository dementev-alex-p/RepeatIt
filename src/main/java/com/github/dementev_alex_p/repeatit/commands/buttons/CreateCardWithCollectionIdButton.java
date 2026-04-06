package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class CreateCardWithCollectionIdButton extends CommandButton {

    public CreateCardWithCollectionIdButton(final long collectionId) {
        super(
                CommandEnum.CREATE_CARD,
                "➕ Создать карточку",
                CommandParameterUtils.createActionParameter(CreateCardButton.START_ACTION_CODE),
                CommandParameterUtils.createCollectionIdParameter(collectionId)
        );
    }
}
