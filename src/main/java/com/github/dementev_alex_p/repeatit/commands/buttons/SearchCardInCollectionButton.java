package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;

public class SearchCardInCollectionButton extends CommandButton {
    public SearchCardInCollectionButton() {
        super(
                CommandEnum.SEARCH,
                "🔍︎ Найти карточку"
        );
    }
}
