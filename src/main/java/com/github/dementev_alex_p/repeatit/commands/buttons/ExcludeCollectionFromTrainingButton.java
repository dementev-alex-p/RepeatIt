package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class ExcludeCollectionFromTrainingButton extends CommandButton {

    public ExcludeCollectionFromTrainingButton(final long collectionId) {
        super(
                CommandEnum.EXCLUDE_COLLECTION_FROM_TRAINING,
                CommandEnum.EXCLUDE_COLLECTION_FROM_TRAINING.getDescription(),
                CommandParameterUtils.createCollectionIdParameter(collectionId)
        );
    }
}
