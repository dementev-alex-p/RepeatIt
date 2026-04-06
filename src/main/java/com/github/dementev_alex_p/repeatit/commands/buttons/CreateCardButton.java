package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class CreateCardButton extends CommandButton {
    public static final String START_ACTION_CODE = "start";
    public CreateCardButton() {
        super(
                CommandEnum.CREATE_CARD,
                "➕ Создать карточку",
                CommandParameterUtils.createActionParameter(START_ACTION_CODE)
        );
    }
}
