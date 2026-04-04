package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class CreateCardWithCollectionId extends CommandButton {
    public CreateCardWithCollectionId(final long collectionId) {
        super(
                CommandEnum.CREATE_CARD,
                "➕ Создать карточку",
                CommandEnum.CREATE_CARD.getDefaultParameter(),
                CommandParameterUtils.createCollectionIdParameter(collectionId)
        );
    }
}
