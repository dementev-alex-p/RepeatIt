package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;

public class CreateAnotherCardButton extends CommandButton {
    public CreateAnotherCardButton() {
        super(
                CommandEnum.CREATE_CARD,
                "➕ Создать еще",
                CommandEnum.CREATE_CARD.getDefaultParameter()
        );
    }
}
