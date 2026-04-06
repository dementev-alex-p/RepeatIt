package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class CancelExclusionCollectionButton extends CommandButton {

    public CancelExclusionCollectionButton(final long collectionId) {
        super(
                CommandEnum.REMOVE_COLLECTION_EXCLUSION,
                CommandEnum.REMOVE_COLLECTION_EXCLUSION.getDescription(),
                CommandParameterUtils.createCollectionIdParameter(collectionId)
        );
    }
}
