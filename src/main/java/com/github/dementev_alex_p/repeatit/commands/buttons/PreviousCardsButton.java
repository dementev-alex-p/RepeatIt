package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class PreviousCardsButton extends CommandButton {
    public PreviousCardsButton(final int page, final long collectionId) {
        super(
                CommandEnum.VIEW_COLLECTION,
                "<<",
                CommandParameterUtils.createPageParameter(page),
                CommandParameterUtils.createCollectionIdParameter(collectionId),
                CommandParameterUtils.createActionParameter(ViewCardsInCollectionButton.VIEW_CARDS_ACTION)
        );
    }
}
