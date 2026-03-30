package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class EditCollectionTitleButton extends CommandButton {
    public EditCollectionTitleButton(final long collectionId) {
        super(
                CommandEnum.EDIT_COLLECTION,
                "✍ Изменить название",
                CommandParameterUtils.createCollectionIdParameter(collectionId)
        );
    }
}
