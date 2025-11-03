package com.github.dementev_alex_p.repeatit.commands.result.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandButtonUtils;

public class ShowBackSideButton extends CommandButton {
    public static final String TEXT = "Показать";
    public ShowBackSideButton(final long cardId) {
        super(
                CommandEnum.TRAINING,
                TEXT,
                CommandButtonUtils.createActionParameter("show_back_side"),
                CommandButtonUtils.createCardIdParameter(cardId)
        );
    }

}
