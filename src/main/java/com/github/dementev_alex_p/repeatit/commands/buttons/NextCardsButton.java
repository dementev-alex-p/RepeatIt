package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

import java.util.Arrays;

public class NextCardsButton extends CommandButton {
    public NextCardsButton(final int page, final long collectionId) {
        super(
                CommandEnum.VIEW_COLLECTION,
                ">>",
                Arrays.asList(
                        CommandParameterUtils.createPageParameter(page),
                        CommandParameterUtils.createCollectionIdParameter(collectionId),
                        CommandParameterUtils.createAction(ViewCardsInCollectionButton.VIEW_CARDS_ACTION)
                ));
    }
}
