package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class DeleteCollectionButton extends CommandButton {
    public DeleteCollectionButton(final long collectionId) {
        super(
                CommandEnum.DELETE_COLLECTION,
                "❌ Удалить",
                CommandParameterUtils.createCollectionIdParameter(collectionId)
        );
    }
}
