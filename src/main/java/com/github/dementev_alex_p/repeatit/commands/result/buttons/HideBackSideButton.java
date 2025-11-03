package com.github.dementev_alex_p.repeatit.commands.result.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;

public class HideBackSideButton extends CommandButton {
    public static final String TEXT = "📘 Скрыть содержание";
    public HideBackSideButton(final long cardId) {
        super(
                CommandEnum.TRAINING,
                TEXT,
                CommandButtonUtils.createActionParameter("hide_back_side"),
                CommandButtonUtils.createCardIdParameter(cardId)
        );
    }

}
