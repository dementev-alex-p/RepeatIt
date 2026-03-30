package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class CollectionNumberButton extends CommandButton{

    public CollectionNumberButton(final int number, final long collectionId) {
        super(
                CommandEnum.VIEW_SINGLE_COLLECTION,
                String.valueOf(number),
                CommandParameterUtils.createCollectionIdParameter(collectionId),
                CommandParameterUtils.createPageParameter(1)
        );
    }
}
