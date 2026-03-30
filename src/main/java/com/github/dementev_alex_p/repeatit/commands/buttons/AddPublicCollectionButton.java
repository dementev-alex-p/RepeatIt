package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class AddPublicCollectionButton extends CommandButton {
    public AddPublicCollectionButton(final long collectionId) {
        super(
                CommandEnum.ADD_PUBLIC_COLLECTION,
                "➕ Добавить коллекцию себе",
                CommandParameterUtils.createCollectionIdParameter(collectionId)
        );
    }
}
