package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.commands.handlers.GenerateCardsHandler;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class GenerateCardsButton extends CommandButton {

    public GenerateCardsButton() {
        super(
                CommandEnum.GENERATE_CARDS,
                CommandEnum.GENERATE_CARDS.getDescription(),
                CommandParameterUtils.createAction(GenerateCardsHandler.START_ACTION)
        );
    }
}
