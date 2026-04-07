package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;


public class ViewCardsInCollectionButton extends CommandButton {
    public static final String VIEW_CARDS_ACTION = "view_cards";

    public ViewCardsInCollectionButton(long collectionId) {
        super(
                CommandEnum.VIEW_COLLECTION,
                "📘 Просмотр карточек",
                CommandParameterUtils.createAction(VIEW_CARDS_ACTION),
                CommandParameterUtils.createCollectionIdParameter(collectionId)
        );
    }
}
