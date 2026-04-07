package com.github.dementev_alex_p.repeatit.commands.buttons;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import com.github.dementev_alex_p.repeatit.utils.CommandParameterUtils;

public class ShowBackSideButton extends CommandButton {
    public static final String TEXT = "📖 Показать содержание";
    public ShowBackSideButton(final long cardId) {
        super(
                CommandEnum.TRAINING,
                TEXT,
                CommandParameterUtils.createAction("show_back_side"),
                CommandParameterUtils.createCardIdParameter(cardId)
        );
    }

}
