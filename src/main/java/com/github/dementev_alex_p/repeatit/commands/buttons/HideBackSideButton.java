package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class HideBackSideButton extends CommandButton {
    public static final String TEXT = "📘 Скрыть содержание";
    public HideBackSideButton(final long cardId) {
        super(
                CommandEnum.TRAINING,
                TEXT,
                CommandParameterUtils.createActionParameter("hide_back_side"),
                CommandParameterUtils.createCardIdParameter(cardId)
        );
    }

}
